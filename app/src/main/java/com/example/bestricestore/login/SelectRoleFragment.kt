package com.example.bestricestore.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.R
import com.example.bestricestore.databinding.FragmentSelectRoleBinding


class SelectRoleFragment constructor() : Fragment() {
    private var binding: FragmentSelectRoleBinding? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectRoleBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        binding!!.buttonCustomer.setOnClickListener { v: View? ->
            findNavController(
                requireView()
            ).navigate(R.id.customerSignUpFragment)
        }
        binding!!.buttonDeliverer.setOnClickListener { v: View? ->
            findNavController(
                requireView()
            ).navigate(R.id.deliSignUpFragment)
        }
        return binding!!.getRoot()
    }
}