package com.example.bestricestore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.databinding.FragmentManageUsersBinding


class ManageUsersFragment : Fragment() {
    private var binding: FragmentManageUsersBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentManageUsersBinding.inflate(inflater, container, false)
        binding!!.buttonViewAll.setOnClickListener { v: View? ->
            findNavController(
                requireView()
            ).navigate(R.id.viewUsersFragment)
        }
        binding!!.buttonCheck.setOnClickListener { v: View? ->
            findNavController(
                requireView()
            ).navigate(R.id.checkRegisterFragment)
        }
        return binding!!.root
    }
}