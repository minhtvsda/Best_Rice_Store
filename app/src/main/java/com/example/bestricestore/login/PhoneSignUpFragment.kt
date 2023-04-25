package com.example.bestricestore.login

import android.app.ProgressDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.databinding.FragmentPhoneSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

class PhoneSignUpFragment : Fragment() {
    private var binding: FragmentPhoneSignUpBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.title = "Log In"
        // Inflate the layout for this fragment
        binding = FragmentPhoneSignUpBinding.inflate(inflater, container, false)
        binding!!.button.setOnClickListener({ v: View? -> VerifyPhone() })
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button even
                    Toast.makeText(context, "You have to login first!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding!!.root
    }

    private fun VerifyPhone() {
        if ((binding!!.editPhoneNumber.text.toString() == Constants.EMPTY_STRING)) {
            Toast.makeText(context, "You have to type phone number!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val progressDialog: ProgressDialog = ProgressDialog(context)
        progressDialog.show()
        val phoneNumber: String = binding!!.editPhoneNumber.text.toString().trim { it <= ' ' }
        val options: PhoneAuthOptions = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
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
                    val bundle: Bundle = Bundle()
                    bundle.putString("phoneNumber", phoneNumber)
                    bundle.putString("Id", verificationId)
                    progressDialog.dismiss()
                    findNavController(requireView()).navigate(R.id.phoneVerifyFragment, bundle)
                }
            }) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity(), object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user: FirebaseUser? = task.result.user
                        // Update UI
//                            gotoMainactivity();
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
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
    fun asdsda(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
//            binding!!.editTextTextPersonName.text = token.toString()
            // Log and toast
//            val msg = getString(androidx.browser.R.string.copy_toast_msg, token)
            Log.d(ContentValues.TAG, token.toString())
            Toast.makeText(context, token.toString(), Toast.LENGTH_SHORT).show()
        })
    }
}