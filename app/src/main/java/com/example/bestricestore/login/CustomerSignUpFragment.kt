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

class CustomerSignUpFragment : Fragment() {
    private var binding: FragmentCustomerSignUpBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCustomerSignUpBinding.inflate(inflater, container, false)
        binding!!.phone.text =
            "Phone Number:" + FirebaseAuth.getInstance().currentUser!!.phoneNumber
        binding!!.button2.setOnClickListener({ v: View? -> Register() })
        return binding!!.root
    }

    private fun Register() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val username: String = binding!!.username.text.toString()
        val email: String = binding!!.email.text.toString()
        val address: String = binding!!.address.text.toString()
        val u: User =
            User(username, email, address, Constants.ROLE_CUSTOMER, user!!.phoneNumber,"")
        db.collection(Constants.FS_USER).document(user.uid).set(u)
            .addOnSuccessListener(object : OnSuccessListener<Void?> {
                override fun onSuccess(unused: Void?) {
                    Toast.makeText(
                        context,
                        "Create your account successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController(requireView()).navigate(R.id.mainFragment)
                }
            })
    }
}