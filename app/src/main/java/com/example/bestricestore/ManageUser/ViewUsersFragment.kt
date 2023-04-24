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
import com.example.bestricestore.ManageUser.UserListAdapter
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.DeliLink
import com.example.bestricestore.data.User
import com.example.bestricestore.databinding.FragmentViewUsersBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ViewUsersFragment constructor() : Fragment(), UserListAdapter.ListUserListener {
    private lateinit var mViewModel: ViewUsersViewModel
    private lateinit var binding: FragmentViewUsersBinding
    private var adapter: UserListAdapter? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mViewModel = ViewModelProvider(this).get(ViewUsersViewModel::class.java)
        binding = FragmentViewUsersBinding.inflate(inflater, container, false)
        val app: AppCompatActivity? = getActivity() as AppCompatActivity?
        //get activity, app chinh la ca activity chua fragment  nay
        val ab: ActionBar? = app!!.getSupportActionBar() //lay phan giai mau tim
        ab!!.setHomeButtonEnabled(true)
        ab.setDisplayShowHomeEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_baseline_home_24)
        ab.title = "Manage User"
        setHasOptionsMenu(true)
        val rv = binding!!.recyclerView
        rv.setHasFixedSize(true)
        rv.addItemDecoration(
            DividerItemDecoration(
                getContext(),
                (LinearLayoutManager(context).orientation)
            )
        )
        mViewModel!!.userList.observe(
            viewLifecycleOwner
        ) { userList: List<User> ->
            adapter = UserListAdapter(userList, this)
            binding!!.recyclerView.setAdapter(adapter)
            binding!!.recyclerView.setLayoutManager(LinearLayoutManager(getActivity()))
        }
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }
        return binding!!.getRoot()
    }

    public override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_viewusers, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_search_users) // get our menu item.
        //Interface for direct access to a previously created menu item.
        val sv =
            MenuItemCompat.getActionView(menuItem) as androidx.appcompat.widget.SearchView // getting search view of our item.
        sv.setMaxWidth(Int.MAX_VALUE)
        // below line is to call set on query text listener method.
        sv.setQueryHint("Search by phone number, username, address!")
        sv.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            public override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            public override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })
    }

    fun filter(text: String) {
        val filterList: MutableList<User> = ArrayList()
        for (user: User in mViewModel!!.userList.getValue()!!) {
            if ((user.phoneNumber!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || user.username!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || user.address!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || user.roles!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault())))
            ) {
                filterList.add(user)
            }
        }
        adapter!!.filterList(filterList)
    }

    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_search_customer -> mViewModel!!.getUserByRole(Constants.ROLE_CUSTOMER)
            R.id.action_search_deliverer -> mViewModel!!.getUserByRole(Constants.ROLE_DELIVERER)
            R.id.action_search_banned_users -> mViewModel!!.getUserByRole(Constants.FS_ROLE_BAN)
            android.R.id.home -> findNavController(requireView()).navigate(R.id.mainFragment)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    public override fun onItemClick(userId: String?, i: Int) {
        if (i == 1) {
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            db.collection(Constants.FS_USER).document((userId)!!)
                .update("roles", Constants.FS_ROLE_BAN)
                .addOnSuccessListener(object : OnSuccessListener<Void?> {
                    public override fun onSuccess(aVoid: Void?) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully updated!")
                        mViewModel!!.users
                        Toast.makeText(
                            getContext(),
                            "The user has been banned!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            val bundle: Bundle = Bundle()
            bundle.putString("userId", userId)
            findNavController(requireView()).navigate(R.id.editorUsersFragment, bundle)
        }
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
    public override fun onResume() {
        super.onResume()
        mViewModel!!.users
    }


    companion object {
        fun newInstance(): ViewUsersFragment {
            return ViewUsersFragment()
        }
    }
}