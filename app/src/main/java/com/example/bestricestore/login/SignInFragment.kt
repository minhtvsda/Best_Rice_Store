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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.R
import com.example.bestricestore.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {
    private lateinit var mViewModel: SignInViewModel
    private lateinit var binding: FragmentSignInBinding
    var mAuth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        binding!!.layoutSignup.setOnClickListener { v: View? ->
            findNavController(
                requireView()
            ).navigate(R.id.signUpFragment)
        }
        binding!!.buttonSignin.setOnClickListener { v: View? -> SignIn() }
        binding!!.btnForgotPassword.setOnClickListener { v: View? ->
            findNavController(
                requireView()
            ).navigate(R.id.forgotPasswordFragment)
        }
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button even
                    Toast.makeText(context, "You have to login first!", Toast.LENGTH_SHORT).show()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding!!.root
    }

    private fun SignIn() {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        val email = binding!!.email.text.toString().trim { it <= ' ' }
        val password = binding!!.password.text.toString().trim { it <= ' ' }
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    // Sign in success, update UI with the signed-in user's information
                    if (user!!.isEmailVerified) {
                        Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show()
                        findNavController(requireView()).navigate(R.id.mainFragment)
                        progressDialog.dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Your email is not verified yet! Please check the inbox in your email to verify and try again!",
                            Toast.LENGTH_LONG
                        ).show()
                        progressDialog.dismiss()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        context,
                        "Failed! Your email or password is not correct!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                }
            }
    }

    companion object {
        fun newInstance(): SignInFragment {
            return SignInFragment()
        }
    }
}