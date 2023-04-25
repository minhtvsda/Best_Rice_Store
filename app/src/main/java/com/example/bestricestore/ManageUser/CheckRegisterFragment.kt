package com.example.bestricestore.ManageUser

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
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
import com.example.bestricestore.data.User
import com.example.bestricestore.databinding.FragmentCheckRegisterBinding
import java.util.*
import kotlin.collections.ArrayList

class CheckRegisterFragment : Fragment(), RegisterListAdapter.ListUserListener {
    private lateinit var mViewModel: CheckRegisterViewModel
    private lateinit var binding: FragmentCheckRegisterBinding
    private lateinit var adapter: RegisterListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewModel = ViewModelProvider(this).get(CheckRegisterViewModel::class.java)
        binding = FragmentCheckRegisterBinding.inflate(inflater, container, false)
        val app = activity as AppCompatActivity?
        //get activity, app chinh la ca activity chua fragment  nay
        val ab: ActionBar? = app!!.supportActionBar //lay phan giai mau tim
        ab!!.setHomeButtonEnabled(true)
        ab.setDisplayShowHomeEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_baseline_home_24)
        setHasOptionsMenu(true)
        val rv = binding.recyclerView
        rv.setHasFixedSize(true)
        rv.addItemDecoration(
            DividerItemDecoration(
                context,
                (LinearLayoutManager(context).orientation)
            )
        )
        mViewModel.userList.observe(
            viewLifecycleOwner
        ) { userList: List<User> ->
            adapter = RegisterListAdapter(userList, this)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        }
        binding.bottomNavigation.setOnNavigationItemSelectedListener { onNavigationItemSelected(it) }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_checkregister, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_search_users) // get our menu item.
        //Interface for direct access to a previously created menu item.
        val sv: SearchView =
            MenuItemCompat.getActionView(menuItem) as SearchView // getting search view of our item.
        sv.maxWidth = Int.MAX_VALUE
        // below line is to call set on query text listener method.
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })
    }

    fun filter(text: String) {
        val filterList: MutableList<User> = ArrayList()
        for (user: User in mViewModel.userList.value!!) {
            if ((user.phoneNumber!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || user.username!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || user.address!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault())))
            ) {
                filterList.add(user)
            }
        }
        adapter.filterList(filterList)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search_deliverer_register -> mViewModel.getUserByRole((Constants.DELIVERER_REGISTER_WAITING))
            R.id.action_search_deliverer_register_declined -> mViewModel.getUserByRole((Constants.DELIVERER_REGISTER_DECLINED))
            R.id.action_show_all_deliverer_register -> mViewModel.allDeliRegister
            android.R.id.home -> findNavController(requireView()).navigate(R.id.mainFragment)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onItemClick(userId: String?) {
        val b: Bundle = Bundle()
        b.putString("userId", userId)
        findNavController(requireView()).navigate(R.id.editorCheckFragment, b)
    }

    override fun onResume() {
        super.onResume()
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
        fun newInstance(): CheckRegisterFragment {
            return CheckRegisterFragment()
        }
    }
}