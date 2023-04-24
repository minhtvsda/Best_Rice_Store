package com.example.bestricestore.login

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.User
import com.example.bestricestore.databinding.FragmentChangePasswordBinding
import com.example.bestricestore.databinding.FragmentProfileBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import java.util.*

class ProfileFragment : Fragment() {
    private lateinit var mViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    var user = FirebaseAuth.getInstance().currentUser
    private var date: String? = null
    private var userGender: String? = null
    private var uri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val app = activity as AppCompatActivity?
        val ab = app!!.supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(false)
        ab.title = "Profile"
        mViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val gender = listOf("Select your gender", "Male", "Female", "Other")
        val aa: ArrayAdapter<*> =
            ArrayAdapter<String?>(requireContext(), android.R.layout.simple_spinner_item, gender)
        // create view for each of elements and display value
        aa.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line) // create specify view for elements
        binding!!.SpinnerGender.adapter = aa
        binding!!.profileGender.setOnClickListener {
            binding!!.SpinnerGender.visibility = View.VISIBLE
        }
        binding!!.SpinnerGender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        return
                    } else {
                        userGender = gender[position]
                        binding!!.profileGender.text = userGender
                        binding!!.SpinnerGender.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(context, "Please choose a correct gender!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        mViewModel!!.muser.observe(
            viewLifecycleOwner
        ) { u: User ->
            if ((u.roles == Constants.ROLE_ADMIN)) {
                binding!!.btnManageUsers.visibility = View.VISIBLE
            }
            binding!!.profileAddress.setText(u.address)
            binding!!.profileName.setText(u.username)
            binding!!.profileRoles.text = "Role: " + u.roles
            binding!!.profileGender.text = "Your gender: " + u.gender
            binding!!.profileDob.text = "Your DOB: " + u.dob
            binding!!.profileEmail.setText(u.email)
            if (user!!.photoUrl != null) {
                Glide.with(requireContext()).load(user!!.photoUrl)
                    .error(R.drawable.profile).into(
                    binding!!.profileAvatar
                )
            }
        }
        mViewModel!!.getUserProfile(user!!.uid)
        binding!!.buttonSignout.setOnClickListener { v: View? -> SignOut() }
        binding!!.profileAvatar.setOnClickListener { v: View? -> openGallery() }
        binding!!.buttonSave.setOnClickListener { v: View? -> UpdateProfile() }
        binding!!.btnChangePassword.setOnClickListener { v: View? ->
            findNavController(
                requireView()
            )
                .navigate(R.id.changePasswordFragment)
        }
        binding!!.btnManageUsers.setOnClickListener { v: View? ->
            findNavController(
                requireView()
            )
                .navigate(R.id.manageUsersFragment)
        }
        binding!!.profileDob.setOnClickListener { v: View? -> selectDOB() }
        binding!!.buttonAboutUs.setOnClickListener { v: View? -> showAboutUs() }
        return binding!!.root
    }

    private fun showAboutUs() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.about_us)
        dialog.show()
    }

    private fun selectDOB() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        binding!!.profileDob.setOnClickListener { v: View? ->
            val etDate = binding!!.profileDob
            val datePickerDialog = DatePickerDialog(
                requireContext(), DatePickerDialog.OnDateSetListener { view, year1, month1, day1 ->
                    var month1 = month1
                    month1 += 1
                    date = "$day1/$month1/$year1"
                    etDate.text = date
                    Toast.makeText(context, "Select date successfully!", Toast.LENGTH_LONG).show()
                }, year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun UpdateProfile() {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        if (uri != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(binding!!.profileName.text.toString())
                .setPhotoUri(uri)
                .build()
            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "User profile updated.")
                        Toast.makeText(context, "Update successful!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        if (date == null) {
            date = mViewModel!!.muser.value!!.dob
        }
        if (userGender == null) {
            userGender = mViewModel!!.muser.value!!.gender
        }
        mViewModel!!.updateUserProfiler(
            binding!!.profileName.text.toString(),
            binding!!.profileEmail.text.toString(),
            binding!!.profileAddress.text.toString(),
            mViewModel!!.muser.value!!.roles,
            date,
            userGender,
            user!!.uid
        )
        progressDialog.dismiss()
        Toast.makeText(context, "Update your profiles successful!", Toast.LENGTH_SHORT).show()
        findNavController(requireView()).navigate(R.id.mainFragment)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 300)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 300 && data != null && data.data != null) {
            uri = data.data
            binding!!.profileAvatar.setImageURI(uri)
        }
    }

    fun setImageURI(uri: Uri?) {
        binding!!.profileAvatar.setImageURI(uri)
    }

    private fun SignOut() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Successfull!!!", Toast.LENGTH_LONG).show()
        findNavController(requireView()).navigate(R.id.phoneSignUpFragment)
    }
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Home -> findNavController(requireView()).navigate(R.id.mainFragment)
            R.id.shoppingCart -> findNavController(requireView()).navigate(R.id.orderAdminFragment)
            R.id.News -> findNavController(requireView()).navigate(R.id.newFragment)
            R.id.Profile -> findNavController(requireView()).navigate(R.id.profileFragment)
        }
        return true
    }
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}