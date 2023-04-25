package com.example.bestricestore

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.example.bestricestore.EditorViewModel
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.FoodEntity
import com.example.bestricestore.data.NewEntity
import com.example.bestricestore.data.User
import com.example.bestricestore.databinding.FragmentCartdialogBinding
import com.example.bestricestore.databinding.FragmentEditorBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class EditorFragment : Fragment() {
    private lateinit var mViewModel: EditorViewModel
    private lateinit var binding: FragmentEditorBinding
    private lateinit var dialogbinding: FragmentCartdialogBinding
    private val muser: User? = null
    private var foodUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val app: AppCompatActivity? = activity as AppCompatActivity?
        //get activity, app chinh la ca activity chua fragment  nay
        val ab: ActionBar? = app!!.supportActionBar //lay phan giai mau tim
        ab!!.setHomeButtonEnabled(true)
        ab.setDisplayShowHomeEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_outline_save_alt_24)
        setHasOptionsMenu(true)
        mViewModel = ViewModelProvider(this).get(EditorViewModel::class.java)
        binding = FragmentEditorBinding.inflate(inflater, container, false)
        dialogbinding = FragmentCartdialogBinding.inflate(inflater, container, false)
        mViewModel.food.observe(
            viewLifecycleOwner,
            { foodEntity: FoodEntity ->
                binding.type.setText(foodEntity.type)
                binding.name.setText(foodEntity.name)
                binding.cost.setText(foodEntity.cost.toString())
                binding.description.setText(foodEntity.description)
                binding.salePercent.setText(Integer.toString(foodEntity.salePercent))
                binding.currentStatus.text = foodEntity.currentStatus
                if (!(foodEntity.imageUrl == "")) {
                    Glide.with(requireContext()).load(foodEntity.imageUrl)
                        .error(R.drawable.profile).into(binding.imageFood)
                }
                if (!(foodEntity.type == Constants.EMPTY_STRING)) {
                    binding.spinnerType.visibility = View.GONE
                }
                requireActivity().invalidateOptionsMenu()
            }
        )
        val foodId = requireArguments().getString("foodId")
        mViewModel.getFoodById(foodId!!)
        binding.imageFood.setOnClickListener { v: View? -> selectImage() }
        val type: List<String?> = listOf("Select type", "Drink", "Rice", "Noodle", "Other")
        val aa2: ArrayAdapter<String?> =
            ArrayAdapter<String?>((requireContext()), android.R.layout.simple_spinner_item, type)
        binding.type.setOnClickListener { v: View? ->
            binding.spinnerType.visibility = View.VISIBLE
        }
        aa2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerType.adapter = aa2
        binding.spinnerType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    binding.type.setText(type.get(position))
                    binding.spinnerType.visibility = View.GONE
                    if (position == 4) {
                        binding.type.setText("")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        val status: List<String?> = listOf(
            "Select the current status",
            Constants.AVAILABLE_CURRENT_STATUS,
            Constants.OUT_OF_STOCK_CURRENT_STATUS
        )
        val aa: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_item, status)
        aa.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerCurrentStatus.adapter = aa
        binding.currentStatus.setOnClickListener { v: View? ->
            binding.spinnerCurrentStatus.visibility = View.VISIBLE
        }
        binding.spinnerCurrentStatus.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    binding.currentStatus.text = status.get(position)
                    binding.spinnerCurrentStatus.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        return binding.root
    }

    private fun selectImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 100) && (data != null) && (data.data != null)) {
            foodUri = data.data
            binding.imageFood.setImageURI(foodUri)
        }
    }

    private fun savethenReturn(): Boolean {
        val progressDialog: ProgressDialog = ProgressDialog(context)
        progressDialog.setTitle("Saving")
        progressDialog.show()
        val id: String = mViewModel.food.value!!.id!!
        //lay tu food do id ko doi
        val type: String = binding.type.text.toString()
        val name: String = binding.name.text.toString()
        val cost: Int = binding.cost.text.toString().toInt()
        val description: String = binding.description.text.toString()
        val imageUrl: String = mViewModel.food.value!!.imageUrl!!
        val salePercent: Int = binding.salePercent.text.toString().toInt()
        val currentStatus: String = binding.currentStatus.text.toString()
        if (foodUri != null) {
            val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
            val now: Date = Date()
            val fileName: String = formatter.format(now) // tao ten rieng biet cho file
            val storageReference: StorageReference =
                FirebaseStorage.getInstance().getReference("image/" + fileName)
            storageReference.putFile(foodUri!!)
                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                        // lay url theo cach moi
                        val task: Task<Uri> = taskSnapshot.metadata!!
                            .reference!!.downloadUrl
                        task.addOnSuccessListener { uri ->
                            val photoLink: String = uri.toString()
                            val updateFood: FoodEntity = FoodEntity(
                                id //                != null ? id : Constants.NEW_FOOD_ID//neu id la null thi ta se lay const
                                ,
                                type,
                                name,
                                cost,
                                description,
                                photoLink,
                                salePercent,
                                currentStatus
                            )
                            mViewModel.updateFood(updateFood)
                            progressDialog.dismiss()
                            Toast.makeText(
                                context,
                                "Update food successful!",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController(requireView()).navigate(R.id.mainFragment)
                        }
                    }
                })
            return true
        } else {
            val updateFood: FoodEntity = FoodEntity(
                id //                != null ? id : Constants.NEW_FOOD_ID//neu id la null thi ta se lay const
                , type, name, cost, description, imageUrl, salePercent, currentStatus
            ) // sau nay co the dung add food
            mViewModel.updateFood(updateFood)
            progressDialog.dismiss()
            Toast.makeText(context, "Update food successful!", Toast.LENGTH_LONG).show()
            findNavController(requireView()).navigate(R.id.mainFragment) // 1 trong 3 cach de nhay giua cac fragment
            return true
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val f: FoodEntity? = mViewModel.food.value
            menu.findItem(R.id.action_delete).isVisible = !(f != null && (f.id == Constants.NEW_ID))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> if (isFoodValidated) savethenReturn() else false
            R.id.action_delete -> deletethenReturn()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deletethenReturn(): Boolean {
        Log.i(this.javaClass.name, "delete and return")
        mViewModel.deleteFood()
        Toast.makeText(context, "Delete food successfully!!", Toast.LENGTH_SHORT).show()
        findNavController(requireView()).navigate(R.id.mainFragment)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_delete, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private val isFoodValidated: Boolean
        private get() {
            val name: EditText = binding.name
            val type: EditText = binding.type
            val cost: EditText = binding.cost
            val description: EditText = binding.description
            val salePercent: EditText = binding.salePercent
            var isValidated: Boolean = true
            if ((name.text.toString() == Constants.EMPTY_STRING)) {
                name.error = "You must type the name!!"
                isValidated = false
            }
            if ((type.text.toString() == Constants.EMPTY_STRING)) {
                type.error = "You must type the type!!"
                isValidated = false
            }
            if (((cost.text.toString() == Constants.EMPTY_STRING) || cost.text.toString()
                    .toInt() <= 0)
            ) {
                cost.error = "You must type the cost!!"
                isValidated = false
            }
            if (((salePercent.text
                    .toString() == Constants.EMPTY_STRING) || salePercent.text.toString()
                    .toInt() < 0)
            ) {
                salePercent.error = "You must type the salePercent!!!"
                isValidated = false
            }
            if ((description.text.toString() == Constants.EMPTY_STRING)) {
                description.error = "You must type the cost!!"
                isValidated = false
            }
            return isValidated
        }

    companion object {
        fun newInstance(): EditorFragment {
            return EditorFragment()
        }
    }
}