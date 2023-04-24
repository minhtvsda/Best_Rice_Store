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
import com.example.bestricestore.data.Constants
import com.example.bestricestore.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private lateinit var mViewModel: SignUpViewModel
    private lateinit var binding: FragmentSignUpBinding
    private var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        progressDialog = ProgressDialog(context)
        binding!!.btnsignupCustomer.setOnClickListener { v: View? -> onClickSignUp(Constants.ROLE_CUSTOMER) }
        binding!!.btnsignupDelivery.setOnClickListener { v: View? -> onClickSignUp(Constants.ROLE_DELIVERER) }
        return binding!!.root
    }

    private fun onClickSignUp(role: String?) {
        val strEmail = binding!!.email.text.toString().trim { it <= ' ' }
        val strPassword = binding!!.password.text.toString().trim { it <= ' ' }
        val strAddress = binding!!.address.text.toString().trim { it <= ' ' }
        val username = binding!!.username.text.toString().trim { it <= ' ' }
        if (strAddress == Constants.EMPTY_STRING || strEmail == Constants.EMPTY_STRING || strAddress == Constants.EMPTY_STRING || username == Constants.EMPTY_STRING) {
            Toast.makeText(context, "Please type all the fields!!", Toast.LENGTH_SHORT).show()
            return
        }
        mViewModel!!.onClickSignUp(context, username, strEmail, strPassword, strAddress, role)
        //        if (mViewModel.Signup){
//        Navigation.findNavController(getView()).navigate(R.id.mainFragment);
//        }
    }

    companion object {
        fun newInstance(): SignUpFragment {
            return SignUpFragment()
        }
    }
}