package com.example.bestricestore.newfeed

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.NewEntity
import com.example.bestricestore.databinding.FragmentEditorNewBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class EditorNewFragment : Fragment() {
    private lateinit var mViewModel: EditorNewViewModel
    private lateinit var binding: FragmentEditorNewBinding
    private val REQUEST_CODE: Int = 200
    private var newUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        AppCompatActivity app = (AppCompatActivity)getActivity();
//        //get activity, app chinh la ca activity chua fragment  nay
//        ActionBar ab = app.getSupportActionBar(); //lay phan giai mau tim
//        ab.setHomeButtonEnabled(true);
//        ab.setDisplayShowHomeEnabled(true);
//        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setHomeAsUpIndicator(R.drawable.ic_outline_save_alt_24);
//        setHasOptionsMenu(true);
        mViewModel = ViewModelProvider(this).get(EditorNewViewModel::class.java)
        binding = FragmentEditorNewBinding.inflate(inflater, container, false)
        mViewModel.news.observe(
            viewLifecycleOwner
        ) { newEntity: NewEntity? ->
            binding.edtTitle.setText(newEntity!!.title)
            binding.edtNewInfo.setText(newEntity.newInfo)
            binding.textDate.text = newEntity.date
            if (newEntity.imageUrl != null) {
                Glide.with(requireContext()).load(newEntity.imageUrl)
                    .error(R.drawable.profile).into(binding.imageNew)
            }
            requireActivity().invalidateOptionsMenu()
        }
        val newId: String? = requireArguments().getString("newId")
        if (newId === Constants.NEW_ID) {
            binding.deleteNew.visibility = View.GONE
        }
        binding.imageNew.setOnClickListener { v: View? -> selectImage() }
        mViewModel.getNewById(newId)
        binding.saveNew.setOnClickListener { v: View? -> savethenReturn() }
        binding.deleteNew.setOnClickListener { v: View? -> deletethenReturn() }
        binding.textDate.setOnClickListener { v: View? ->
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val etDate: TextView = binding.textDate
            val datePickerDialog: DatePickerDialog = DatePickerDialog(
                requireContext(), object : OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker,
                        year1: Int,
                        month1: Int,
                        day1: Int
                    ) {
                        var month1: Int = month1
                        month1 += 1
                        val date: String = day1.toString() + "/" + month1 + "/" + year1
                        etDate.text = date
                        Toast.makeText(context, "Select date successfully!", Toast.LENGTH_LONG)
                            .show()
                    }
                }, year, month, day
            )
            datePickerDialog.show()
        }
        return binding.root
    }

    private fun selectImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_CODE) && (data != null) && (data.data != null)) {
            newUri = data.data
            binding.imageNew.setImageURI(newUri)
        }
    }

    private fun savethenReturn() {
        var isValidated: Boolean = true
        if ((binding.edtNewInfo.text.toString() == Constants.EMPTY_STRING)) {
            binding.edtNewInfo.error = "You need to type this field!"
            isValidated = false
        }
        if ((binding.edtTitle.text.toString() == Constants.EMPTY_STRING)) {
            binding.edtTitle.error = "You need to type this field!"
            isValidated = false
        }
        if (!isValidated) {
            return
        }
        val progressDialog: ProgressDialog = ProgressDialog(context)
        progressDialog.show()
        val id: String? = mViewModel.news.value?.id
        //lay tu food do id ko doi
        val title: String = binding.edtTitle.text.toString()
        val newInfo: String = binding.edtNewInfo.text.toString()
        val imageUrl: String? = mViewModel.news.value?.imageUrl
        val date: String = binding.textDate.text.toString()
        if (newUri != null) {
            val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
            val now: Date = Date()
            val fileName: String = formatter.format(now) // tao ten rieng biet cho file
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val storageReference: StorageReference =
                FirebaseStorage.getInstance().getReference("new/" + fileName)
            storageReference.putFile(newUri!!)
                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                        // lay url theo cach moi
                        val task: Task<Uri> = taskSnapshot.metadata!!
                            .reference!!.downloadUrl
                        task.addOnSuccessListener(object : OnSuccessListener<Uri> {
                            override fun onSuccess(uri: Uri) {
//                                    String imageUrl2 = uri.toString();
//                                    db.collection(Constants.FS_NEW_SET).document(id !=null ? id : "New" + (Constants.TIME - now.getTime()))
//                                            .update("imageUrl", photoLink);
                                val updateNew: NewEntity = NewEntity(
                                    id //                != null ? id : Constants.NEW_FOOD_ID//neu id la null thi ta se lay const
                                    , title, newInfo, uri.toString(), date
                                ) // sau nay co the dung add food
                                mViewModel.updateNew(updateNew)
                                Toast.makeText(context, "Successful", Toast.LENGTH_SHORT)
                                    .show()
                                progressDialog.dismiss()
                                findNavController(requireView()).navigate(R.id.newFragment)
                            }
                        })
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            context,
                            "Failed to upload Image!Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            val updateNew: NewEntity = NewEntity(
                id //                != null ? id : Constants.NEW_FOOD_ID//neu id la null thi ta se lay const
                , title, newInfo, imageUrl, date
            ) // sau nay co the dung add food
            mViewModel.updateNew(updateNew)
            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            findNavController(requireView()).navigate(R.id.newFragment)
        }
    }

    private fun deletethenReturn() {
        mViewModel.deleteNew()
        Toast.makeText(context, "Delete food successfully!!", Toast.LENGTH_SHORT).show()
        findNavController(requireView()).navigate(R.id.newFragment)
    }

    companion object {
        fun newInstance(): EditorNewFragment {
            return EditorNewFragment()
        }
    }
}