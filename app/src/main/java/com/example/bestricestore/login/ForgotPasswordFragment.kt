package com.example.bestricestore.login

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bestricestore.databinding.FragmentForgotPasswordBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {
    private var binding: FragmentForgotPasswordBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        binding!!.btnSentNewPassword.setOnClickListener({ v: View? -> sendNewPassword() })

        // Inflate the layout for this fragment
        return binding!!.root
    }

    private fun sendNewPassword() {
        val progressDialog: ProgressDialog = ProgressDialog(context)
        val email: String = binding!!.edtEmail.text.toString().trim({ it <= ' ' })
        val emailconfirm: String =
            binding!!.edtEmailConfirm.text.toString().trim({ it <= ' ' })
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding!!.edtEmail.error = "Please type correct email!"
        } else if (!(email == emailconfirm)) {
            binding!!.edtEmailConfirm.error = "Please type the same email!"
        } else {
            progressDialog.show()
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                    override fun onComplete(task: Task<Void?>) {
                        if (task.isSuccessful) {
                            progressDialog.dismiss()
                            Toast.makeText(
                                context,
                                "Send email successful! Please check your email",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Send email fail! Make sure your email was already regested!",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressDialog.dismiss()
                        }
                    }
                })
        }
    }
}