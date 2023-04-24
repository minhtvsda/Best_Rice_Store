package com.example.bestricestore.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.User
import com.example.bestricestore.databinding.FragmentCustomerSignUpBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class CustomerSignUpFragment constructor() : Fragment() {
    private var binding: FragmentCustomerSignUpBinding? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCustomerSignUpBinding.inflate(inflater, container, false)
        binding!!.phone.setText(
            "Phone Number:" + FirebaseAuth.getInstance().getCurrentUser()!!.getPhoneNumber()
        )
        binding!!.button2.setOnClickListener(View.OnClickListener({ v: View? -> Register() }))
        return binding!!.getRoot()
    }

    private fun Register() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val username: String = binding!!.username.getText().toString()
        val email: String = binding!!.email.getText().toString()
        val address: String = binding!!.address.getText().toString()
        val u: User =
            User(username, email, address, Constants.ROLE_CUSTOMER, user!!.getPhoneNumber(),"")
        db.collection(Constants.FS_USER).document(user.getUid()).set(u)
            .addOnSuccessListener(object : OnSuccessListener<Void?> {
                public override fun onSuccess(unused: Void?) {
                    Toast.makeText(
                        getContext(),
                        "Create your account successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController(requireView()).navigate(R.id.mainFragment)
                }
            })
    }
}