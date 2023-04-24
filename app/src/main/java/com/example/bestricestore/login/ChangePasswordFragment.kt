package com.example.bestricestore.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.databinding.FragmentChangePasswordBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePasswordFragment constructor() : Fragment() {
    private lateinit var mViewModel: ChangePasswordViewModel
    private lateinit var binding: FragmentChangePasswordBinding
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        binding!!.btnSavePassword.setOnClickListener { v: View? -> updatePassword() }
        return binding!!.getRoot()
    }

    private fun updatePassword() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val newPassword: String = binding!!.password.getText().toString()
        val confirmPassword: String = binding!!.confirmPassword.text.toString()
        if (!(newPassword == confirmPassword)) {
            Toast.makeText(getContext(), "Please type the same!", Toast.LENGTH_LONG).show()
            binding!!.confirmPassword.setError("Must be the same password!")
            return
        }
        user!!.updatePassword(newPassword)
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                public override fun onComplete(task: Task<Void?>) {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                            getContext(),
                            "Update password successful!",
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController(requireView()).navigateUp()
                    } else {
                        Toast.makeText(
                            getContext(),
                            "Update password failed! You need to re-login your account",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
    }

    companion object {
        fun newInstance(): ChangePasswordFragment {
            return ChangePasswordFragment()
        }
    }
}