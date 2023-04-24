package com.example.bestricestore

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.FoodEntity
import com.example.bestricestore.data.User
import com.example.bestricestore.databinding.FragmentFoodDetailBinding
import com.example.bestricestore.databinding.FragmentImageDialogBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.like.LikeButton
import com.like.OnLikeListener
import java.util.*


class FoodDetailFragment constructor() : Fragment() {
    private lateinit var mViewModel: FoodDetailViewModel
    private lateinit var binding: FragmentFoodDetailBinding
    private lateinit var dialogBinding: FragmentImageDialogBinding
    private lateinit var muser: User
    var foodCost: Int = 0
    var Sale: Int = 0
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val app: AppCompatActivity? = activity as AppCompatActivity?
        val ab: ActionBar? = app!!.supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(false)
        ab.title = "Order Food"

        mViewModel = ViewModelProvider(this).get(FoodDetailViewModel::class.java)
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        loadUser()
        mViewModel!!.food.observe(
            viewLifecycleOwner
        ) { foodEntity: FoodEntity ->
            Sale = 100 - foodEntity.salePercent
            binding!!.type.setText("Type: " + foodEntity.type)
            binding!!.name.setText("Name: " + foodEntity.name)
            binding!!.cost.setText("Cost: " + foodEntity.cost + " VND")
            Glide.with(requireContext()).load(foodEntity.imageUrl)
                .error(R.drawable.profile).into(binding!!.imageFood)
            binding!!.description.setText("Description: " + foodEntity.description)
            foodCost = foodEntity.cost
            binding!!.currentStatus.setText("CurrentStatus: \n" + foodEntity.currentStatus)
            if (Sale != 100) {
                binding!!.cost.setPaintFlags(binding!!.cost.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
                binding!!.saleCost.setText(""+(foodCost * Sale / 100) + " VND ( " + (100 - Sale) + "%)")
                binding!!.saleCost.setVisibility(View.VISIBLE)
            }
            if ((foodEntity.currentStatus == Constants.OUT_OF_STOCK_CURRENT_STATUS)) {
                binding!!.btnOrder.visibility = View.GONE
                binding!!.currentStatus.setTextColor(getResources().getColor(R.color.red))
            }
            binding!!.imageFood.setOnClickListener { _: View? ->
                showImage(
                    foodEntity.imageUrl
                )
            }
            binding!!.description.setOnClickListener { v: View? ->
                showDescription(
                    foodEntity.description
                )
            }
            binding.totalLike.text = "${foodEntity.totalLike} people like this food!"
            binding.totalSell.text = foodEntity.totalSell.toString() + " have sold!"
        }
        val foodId: String? = requireArguments().getString("foodId")
        mViewModel!!.getFoodById(foodId)
        calculateTotalCost()
        binding!!.btnAddressSet.setOnClickListener {
            binding!!.address.setText(muser.address)
            Toast.makeText(getContext(), "Use your address!", Toast.LENGTH_SHORT).show()
        }
        binding!!.btnOrder.setOnClickListener {
                v: View? -> order()

        }
        binding.likeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                binding.totalLike.text = "${mViewModel.food.value!!.totalLike!! + 1} people like this food!"
                mViewModel.updateLike(1)
                Toast.makeText(context, "You already like this food!", Toast.LENGTH_LONG).show()
                mViewModel.setUserClickLike()
            }
            override fun unLiked(likeButton: LikeButton) {
                binding.totalLike.text = "${mViewModel.food.value!!.totalLike!! - 1} people like this food!"
                mViewModel.updateLike(-1)
                Toast.makeText(context, "You already dislike this food!", Toast.LENGTH_LONG).show()
                mViewModel.setUserClickDisLike()
            }
        })
        return binding!!.root
    }

    private fun showDescription(description: String?) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(getContext())
        dialog.setMessage("Description: $description")
        dialog.create().show()
    }

    private fun order(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.order_dialog_fragment)
        dialog.show()
        val cost = dialog.findViewById<TextView>(R.id.cost)
        val price = foodCost * Sale / 100
        cost.text = "Price: $price VND"
        val quantity = dialog.findViewById<EditText>(R.id.quantity)
        val textViewTotalCost = dialog.findViewById<TextView>(R.id.textViewTotalCost)
        val btn_addressSet = dialog.findViewById<Button>(R.id.btn_addressSet)
        val note = dialog.findViewById<EditText>(R.id.note)
        val btn_Order = dialog.findViewById<Button>(R.id.btn_Order)
        val address = dialog.findViewById<EditText>(R.id.address)

        btn_Order.setOnClickListener{
            if ((quantity.text.toString() == Constants.EMPTY_STRING) || quantity.text.toString().toInt() <= 0) {
                Toast.makeText(getContext(), "Type correct number quantity!!!", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            var isValidated: Boolean = true
            if ((address.getText().toString() == Constants.EMPTY_STRING)) {
                address.setError("You must type the address!!")
                isValidated = false
            }
            if ((quantity.getText().toString() == Constants.EMPTY_STRING)) {
                quantity.setError("You must type the quantity!!")
                isValidated = false
            }

            var alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Are you sure to order!")
            alert.setCancelable(false)
            alert.setNegativeButton("No") { dialogInterface, i ->
                dialogInterface.cancel()
            }
            alert.setPositiveButton("Yes"){
                    _, _ ->
                if (isValidated) {
                    mViewModel!!.addtoCart(
                        mViewModel.food.value!!.id,
                        quantity.text.toString(),
                        price,
                        muser.phoneNumber,
                        muser.username,
                        muser.address,
                        note.text.toString(),
                        muser.email,
                        arguments?.getString("userToken")
                    )
                    Toast.makeText(context, "Add to cart successfully!!!", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    this.notification()
                    findNavController(requireView()).navigateUp()
                } else {
                    Toast.makeText(
                        getContext(),
                        "Add to cart failed!!! Please type all the require fields",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            alert.create().show()

        }

        btn_addressSet.setOnClickListener{
            address.setText(muser.address)
            Toast.makeText(context, "Use your address!", Toast.LENGTH_SHORT).show()
        }

        quantity.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if ((Constants.EMPTY_STRING == s.toString()) || (s.toString()
                        .toInt() <= 0) || (s.toString().toInt() > 20)
                ) {
                    quantity.error = "Please add a correct quantity! Max is 20."
                    Toast.makeText(
                        getContext(),
                        "Please add a correct quantity! Max is 20.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val q: Int = s.toString().toInt()
                val total: Int = foodCost * q * Sale / 100
                textViewTotalCost.text = "Total Cost: $total VND"
            }

            public override fun afterTextChanged(s: Editable?) {}
        })

    }
    fun notification(){
        val builder = NotificationCompat.Builder(requireContext())
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Check your order regularly")
            .setContentText("Please check your order regularly to update their status!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        val compat = NotificationManagerCompat.from(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        compat.notify(1, builder.build())
    }
    fun addtoCart(
        id: String?,
        quantity: String,
        username: String?,
        addressUser: String?,
        note: String?
    ) {



        if ((quantity == Constants.EMPTY_STRING) || quantity.toInt() <= 0) {
            Toast.makeText(getContext(), "Type correct number quantity!!!", Toast.LENGTH_LONG)
                .show()
            return
        }
        if (isCartValidated) {
            mViewModel!!.addtoCart(
                id,
                quantity,0,
                muser.phoneNumber,
                username,
                addressUser,
                note,
                muser.email,
                ""
            )
            Toast.makeText(getContext(), "Add to cart successfully!!!", Toast.LENGTH_LONG).show()
            findNavController(requireView()).navigateUp()
        } else {
            Toast.makeText(
                getContext(),
                "Add to cart failed!!! Please type all the require fields",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    private fun showImage(url: String?) {
        val fragment: ImageDialogFragment = ImageDialogFragment()
        val result: Bundle = Bundle()
        result.putString("bundleKeyImageUrl", url)
        // The child fragment needs to still set the result on its parent fragment manager
        childFragmentManager.setFragmentResult("requestKey", result)
        fragment.show(childFragmentManager, "TAG1")
    }

    private fun calculateTotalCost() {
        binding!!.quantity.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            public override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if ((Constants.EMPTY_STRING == s.toString()) || (s.toString()
                        .toInt() <= 0) || (s.toString().toInt() > 20)
                ) {
                    binding!!.quantity.setError("Please add a correct quantity! Max is 20.")
                    Toast.makeText(
                        getContext(),
                        "Please add a correct quantity! Max is 20.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val q: Int = s.toString().toInt()
                val total: Int = foodCost * q * Sale / 100
                binding!!.textViewTotalCost.setText("Total Cost: " + total + " VND")
            }

            public override fun afterTextChanged(s: Editable?) {}
        })
    }



    private val isCartValidated: Boolean
        private get() {
            val userAddress: EditText = binding!!.address
            val quantity: EditText = binding!!.quantity
            var isValidated: Boolean = true
            if ((userAddress.getText().toString() == Constants.EMPTY_STRING)) {
                userAddress.setError("You must type the address!!")
                isValidated = false
            }
            if ((quantity.getText().toString() == Constants.EMPTY_STRING)) {
                quantity.setError("You must type the quantity!!")
                isValidated = false
            }
            return isValidated
        }

    private fun loadUser() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val docRef: DocumentReference =
            FirebaseFirestore.getInstance().collection(Constants.FS_USER).document(
                user!!.getUid()
            )
        docRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot?> {
            public override fun onComplete(task: Task<DocumentSnapshot?>) {
                if (task.isSuccessful()) {
                    val document: DocumentSnapshot? = task.getResult()
                    if (document!!.exists()) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.getData())
                        muser = User.Companion.getUserFromFireStore(document)
                        if ((muser.roles == Constants.ROLE_DELIVERER)) {
                            binding!!.btnOrder.setVisibility(View.GONE)
                            binding!!.quantity.setVisibility(View.GONE)
                            binding!!.address.setVisibility(View.GONE)
                            binding!!.btnAddressSet.setVisibility(View.GONE)
                            binding!!.note.setVisibility(View.GONE)
                            binding!!.textViewTotalCost.setVisibility(View.GONE)
                        }
                    }
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getData(binding, findNavController(view), requireContext())
    }
    companion object {
        fun newInstance(): FoodDetailFragment {
            return FoodDetailFragment()
        }
    }
}