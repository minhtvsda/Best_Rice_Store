package com.example.bestricestore.ManageUser

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.DeliLink
import com.example.bestricestore.data.User
import com.example.bestricestore.databinding.FragmentEditorCheckBinding
import com.example.bestricestore.databinding.FragmentEditorUsersBinding
import com.example.bestricestore.ImageDialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class EditorUsersFragment constructor() : Fragment() {
    private lateinit var mViewModel: EditorUsersViewModel
    private lateinit var binding: FragmentEditorUsersBinding
    var role : String?  = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(EditorUsersViewModel::class.java)
        binding = FragmentEditorUsersBinding.inflate(inflater, container, false)
        //        AppCompatActivity app = (AppCompatActivity)getActivity();
//        //get activity, app chinh la ca activity chua fragment  nay
//        ActionBar ab = app.getSupportActionBar();
//        ab.setHomeButtonEnabled(true);
//        ab.setDisplayShowHomeEnabled(true);
//        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setHomeAsUpIndicator(R.drawable.ic_outline_save_alt_24);
//        setHasOptionsMenu(true);
        mViewModel!!.muser.observe(
            viewLifecycleOwner
        ) { user: User ->
            binding!!.userPhoneNumber.setText("User Phone Number:\n "+user.phoneNumber)
            binding!!.userName.setText("User name:\n "+user.username)
            binding!!.userAddress.setText("User address:\n "+user.address)
            binding!!.userEmail.setText("User email:\n "+user.email)
            binding!!.userRoles.text = "User role: "+user.roles
            if ((user.roles == Constants.FS_ROLE_BAN)) {
                binding!!.btnBanUser.visibility = View.GONE
            }
        }
        val userId: String? = requireArguments().getString("userId")
        mViewModel!!.getUserById(userId)
        binding!!.btnBanUser.setOnClickListener {
            role = Constants.FS_ROLE_BAN
            binding!!.userRoles.text = "User role: " + Constants.FS_ROLE_BAN
            Toast.makeText(
                getContext(),
                "You have banned the user by role! Now Click Save User to done! ", Toast.LENGTH_LONG
            ).show()
        }
        binding!!.buttonSave.setOnClickListener { v: View? -> savethenReturn() }
        return binding!!.getRoot()
    }

    private fun savethenReturn(): Boolean {
        val id: String = mViewModel!!.muser.getValue()!!.id!!
        val user = mViewModel.muser.value!!
        val PhoneNumber: String = mViewModel.muser.value?.phoneNumber!!
        val name: String = user.username!!
        val address: String = user.address!!
        val email: String = user.email!!
        val roles: String = if (role == null) user.roles!! else role!!
        val u: User = User(id, name, email, address, roles, PhoneNumber)
        FirebaseFirestore.getInstance().collection(Constants.FS_USER).document(id)
            .set(u)
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                public override fun onComplete(task: Task<Void?>) {
                    Toast.makeText(getContext(), "Update user successful!", Toast.LENGTH_LONG)
                        .show()
                    findNavController(requireView()).navigateUp()
                }
            })
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> savethenReturn()
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance(): EditorUsersFragment {
            return EditorUsersFragment()
        }
    }
}