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
import com.example.bestricestore.ImageDialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class EditorCheckFragment : Fragment() {
    private lateinit var mViewModel: EditorCheckViewModel
    private lateinit var binding: FragmentEditorCheckBinding
    private var role: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val app: AppCompatActivity? = activity as AppCompatActivity?
        //get activity, app chinh la ca activity chua fragment  nay
        val ab: ActionBar? = app!!.supportActionBar //lay phan giai mau tim
        ab!!.setHomeButtonEnabled(true)
        ab.setDisplayShowHomeEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_outline_save_alt_24)
        setHasOptionsMenu(true)
        mViewModel = ViewModelProvider(this).get(EditorCheckViewModel::class.java)
        binding = FragmentEditorCheckBinding.inflate(inflater, container, false)
        mViewModel.muser.observe(
            this.viewLifecycleOwner
        ) { user: User ->
            role = user.roles
            binding.userPhoneNumber.text = "Phone: " + user.phoneNumber
            binding.userName.text = "Name: " + user.username
            binding.userEmail.text = "Email: " + user.email
            binding.userRoles.text = "Role: $role"
        }
        val userId: String? = requireArguments().getString("userId")
        mViewModel.getUserById(userId)
        getDeliLink(userId)
        binding.btnAccept.setOnClickListener { v: View? ->
            role = Constants.ROLE_DELIVERER
            binding.userRoles.text = "Role: " + role
            Toast.makeText(
                context,
                "Change the roles to Deliverer! Click save to save your change!",
                Toast.LENGTH_LONG
            ).show()
        }
        binding.btnDecline.setOnClickListener { v: View? ->
            role = Constants.DELIVERER_REGISTER_DECLINED
            binding.userRoles.text = "Role: " + role
            Toast.makeText(
                context,
                "Change the roles to declined register! Click save to save your change!",
                Toast.LENGTH_LONG
            ).show()
        }
        return binding.root
    }

    private fun showImageDialog(url: String?) {
        val fragment: ImageDialogFragment = ImageDialogFragment()
        val result: Bundle = Bundle()
        result.putString("bundleKeyImageUrl", url)
        // The child fragment needs to still set the result on its parent fragment manager
        childFragmentManager.setFragmentResult("requestKey", result)
        fragment.show(childFragmentManager, "TAG1")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                savethenReturn()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun savethenReturn() {
        FirebaseFirestore.getInstance().collection(Constants.FS_USER).document(
            mViewModel.muser.value!!.id!!
        )
            .update("roles", role)
        findNavController(requireView()).navigate(R.id.checkRegisterFragment)
    }

    private fun getDeliLink(id: String?) {
        FirebaseFirestore.getInstance().collection(Constants.FS_DELI_LINK).document(
            (id)!!
        )
            .get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                //lay du lieu
                override fun onComplete(task: Task<DocumentSnapshot>) {
                    if (task.isSuccessful) {
                        val document: DocumentSnapshot = task.result
                        if (document.exists()) {
                            Log.d(
                                Constants.FIRE_STORE,
                                "DocumentSnapshot data: " + document.data
                            )
                            val cl: DeliLink = DeliLink.getDeliLinkFromFireStore(document)
                            Glide.with(requireContext()).load(cl.idUrl)
                                .error(R.drawable.profile).into(binding.imageId)
                            Glide.with(requireContext()).load(cl.drivingUrl)
                                .error(R.drawable.profile).into(binding.imageDrivingLicense)
                            Glide.with(requireContext()).load(cl.motorUrl)
                                .error(R.drawable.profile).into(binding.imageMotorLicense)
                            binding.imageId.setOnClickListener { v: View? ->
                                showImageDialog(
                                    cl.idUrl
                                )
                            }
                            binding.imageDrivingLicense.setOnClickListener { v: View? ->
                                showImageDialog(
                                    cl.drivingUrl
                                )
                            }
                            binding.imageMotorLicense.setOnClickListener { v: View? ->
                                showImageDialog(
                                    cl.motorUrl
                                )
                            }
                        } else {
                            Log.d(Constants.FIRE_STORE, "No such document")
                        }
                    } else {
                        Log.d(Constants.FIRE_STORE, "get failed with ", task.exception)
                    }
                }
            })
    }

    companion object {
        fun newInstance(): EditorCheckFragment {
            return EditorCheckFragment()
        }
    }
}