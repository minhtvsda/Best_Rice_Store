package com.example.bestricestore.login

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.databinding.FragmentPhoneVerifyBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

class PhoneVerifyFragment : Fragment() {
    private var binding: FragmentPhoneVerifyBinding? = null
    private var mverificationId: String? = null
    private var phoneNumber: String? = null
    var progressDialog: ProgressDialog? = null
    private var mforceResendingToken: ForceResendingToken? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhoneVerifyBinding.inflate(inflater, container, false)
        mverificationId = requireArguments().getString("Id")
        phoneNumber = requireArguments().getString("phoneNumber")
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle("Loading!")
        binding!!.buttonVerify.setOnClickListener { v: View? -> VerifyOtp() }
        binding!!.phoneNumber.text = phoneNumber
        binding!!.sendAgain.setOnClickListener({ v: View? -> sendAgain() })
        return binding!!.root
    }

    private fun sendAgain() {
        val mCallbacks: OnVerificationStateChangedCallbacks =
            object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, forceResendingToken)
                    mverificationId = verificationId
                    mforceResendingToken = forceResendingToken
                }
            }
        val options: PhoneAuthOptions = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber((phoneNumber)!!) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setForceResendingToken((mforceResendingToken)!!)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun VerifyOtp() {
        if ((binding!!.editOtp.text.toString() == Constants.EMPTY_STRING)) {
            Toast.makeText(context, "You have to type the phone number!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        progressDialog!!.show()
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
            (mverificationId)!!,
            binding!!.editOtp.text.toString()
        )
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity(), object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = task.getResult().getUser();
                        // Update UI
                        Toast.makeText(context, "Successful!", Toast.LENGTH_SHORT).show()
                        progressDialog!!.dismiss()
                        findNavController(requireView()).navigate(R.id.splashFragment)
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            progressDialog!!.dismiss()
                            Toast.makeText(
                                context,
                                "The verification code entered was invalid",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
    }

}