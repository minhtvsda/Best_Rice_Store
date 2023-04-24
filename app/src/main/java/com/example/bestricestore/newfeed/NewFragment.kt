package com.example.bestricestore.newfeed

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.NewEntity
import com.example.bestricestore.databinding.FragmentNewBinding
import com.example.bestricestore.newfeed.NewListAdapter.ListNewListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class NewFragment constructor() : Fragment(), ListNewListener {
    private lateinit var mViewModel: NewViewModel
    private lateinit var binding: FragmentNewBinding
    private var adapter: NewListAdapter? = null
    var userRole: String? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val app: AppCompatActivity? = getActivity() as AppCompatActivity?
        val ab: ActionBar? = app!!.supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(false)
        ab.title = "New Feed"
        mViewModel = ViewModelProvider(this).get(NewViewModel::class.java)
        binding = FragmentNewBinding.inflate(inflater, container, false)
        hideUIwithRoles()
        val rv: RecyclerView = binding!!.recyclerViewNew
        rv.setHasFixedSize(true)
        rv.addItemDecoration(
            DividerItemDecoration(
                getContext(),
                (LinearLayoutManager(getContext()).getOrientation())
            )
        )
        mViewModel!!.newList.observe(
            viewLifecycleOwner,
            Observer { newList: List<NewEntity> ->
                adapter = NewListAdapter(getContext(), newList, this)
                binding!!.recyclerViewNew.setAdapter(adapter)
                binding!!.recyclerViewNew.setLayoutManager(LinearLayoutManager(getActivity()))
            }
        )
        binding!!.fabAddNew.setOnClickListener { v: View? ->
            onItemClick(
                Constants.NEW_ID
            )
        }
        binding!!.bottomNavigation.setOnNavigationItemSelectedListener { i: MenuItem ->
            onNavigationItemSelected(
                i
            )
        }
        binding!!.btnFeedback.setOnClickListener(View.OnClickListener { v: View? ->
            findNavController(requireView()).navigate(R.id.feedbackFragment)
        })
        binding!!.btnCheckFeedback.setOnClickListener { v: View? ->
            val bundle: Bundle = Bundle()
            bundle.putString("role", Constants.ROLE_ADMIN)
            findNavController(requireView()).navigate(R.id.checkFeedbackFragment, bundle)
        }
        return binding!!.getRoot()
    }

    private fun hideUIwithRoles() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        if (user == null) {
            return
        }
        db.collection(Constants.FS_USER).document(user.getUid()).get()
            .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                public override fun onComplete(task: Task<DocumentSnapshot>) {
                    if (task.isSuccessful()) {
                        val document: DocumentSnapshot = task.getResult()
                        if (document.exists()) {
                            Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.getData())
                            userRole = document.getString("roles")
                            if ((userRole == Constants.ROLE_ADMIN)) {
                                binding!!.fabAddNew.setVisibility(View.VISIBLE)
                                binding!!.btnCheckFeedback.setVisibility(View.VISIBLE)
                                binding!!.btnFeedback.setVisibility(View.GONE)
                            }
                        }
                    }
                }
            })
    }

    public override fun onItemClick(newId: String) {
        val bundle: Bundle = Bundle()
        bundle.putString("newId", newId)
        if ((userRole == Constants.ROLE_ADMIN)) {
            findNavController(requireView()).navigate(R.id.editorNewFragment, bundle)
        } else {
            findNavController(requireView()).navigate(R.id.userEditorNewFragment, bundle)
        }
    }

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.Home -> findNavController(requireView()).navigate(R.id.mainFragment)
            R.id.shoppingCart -> {
                if ((userRole == Constants.ROLE_CUSTOMER)) {
                    findNavController(requireView()).navigate(R.id.cartFragment)
                }
                if ((userRole == Constants.ROLE_DELIVERER)) {
                    findNavController(requireView()).navigate(R.id.checkOrderFragment)
                }
                if ((userRole == Constants.ROLE_ADMIN)) {
                    findNavController(requireView()).navigate(R.id.orderAdminFragment)
                }
            }
            R.id.News -> {
                mViewModel.news
                Toast.makeText(getContext(), "You are still in!", Toast.LENGTH_SHORT).show()
            }
            R.id.Profile -> findNavController(requireView()).navigate(R.id.profileFragment)
        }
        return true
    }

    companion object {
        fun newInstance(): NewFragment {
            return NewFragment()
        }
    }
}