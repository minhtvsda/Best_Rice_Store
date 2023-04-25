package com.example.bestricestore.cart

import androidx.fragment.app.Fragment
import com.example.bestricestore.databinding.FragmentCartBinding
import android.view.*
import android.widget.Toast
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.bestricestore.R
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.example.bestricestore.ImageDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
class CartFragment : Fragment(), CartListAdapter.ListCartListener {
    private var mViewModel: CartViewModel? = null
    private var binding: FragmentCartBinding? = null
    private var adapter: CartListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val app: AppCompatActivity? = activity as AppCompatActivity?
        val ab: ActionBar? = app!!.supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(true)
        ab.title = "Cart"
        mViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        binding = FragmentCartBinding.inflate(inflater, container, false)
        val rv = binding!!.recyclerViewCart
        rv.setHasFixedSize(true)
        rv.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )
        mViewModel!!.cartList.observe(
            viewLifecycleOwner
        ) { cartList: List<CartItem?>? ->
            if (cartList!!.isEmpty()) {
                binding!!.textView.visibility = View.VISIBLE
            }
            if (!cartList.isEmpty()) {
                binding!!.textView.visibility = View.GONE
            }
            adapter = CartListAdapter(cartList, this, context)
            binding!!.recyclerViewCart.adapter = adapter
            binding!!.recyclerViewCart.layoutManager = LinearLayoutManager(activity)
        }
        binding!!.bottomNavigation.setOnNavigationItemSelectedListener { onNavigationItemSelected(it) }
        return binding!!.root
    }

    override fun onItemCartClickDelete(cartId: String?) {
        val formatter = SimpleDateFormat("ss:mm:HH dd/MM/yyyy")
        val now = Date()
        val time = formatter.format(now)
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.FS_FOOD_CART).document(cartId!!)
            .update("status", Constants.STATUS_CANCEL, "time", time)
            .addOnSuccessListener {
                Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully updated!")
                mViewModel!!.getCarts(Constants.STATUS_WAITING_RESTAURANT)
                Toast.makeText(context, "Cancel successfully!!", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onImageClick(url: String?) {
        val fragment = ImageDialogFragment()
        val result = Bundle()
        result.putString("bundleKeyImageUrl", url)
        // The child fragment needs to still set the result on its parent fragment manager
        childFragmentManager.setFragmentResult("requestKey", result)
        fragment.show(childFragmentManager, "TAG1")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_cartfragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search_waiting -> mViewModel!!.getCarts(Constants.STATUS_WAITING)
            R.id.action_search_cancel -> mViewModel!!.getCarts(Constants.STATUS_CANCEL)
            R.id.action_search_delivering_customer -> mViewModel!!.getCarts(Constants.STATUS_DELIVERING)
            R.id.action_search_waiting_restaurant -> mViewModel!!.getCarts(Constants.STATUS_WAITING_RESTAURANT)
            R.id.action_search_cancel_restaurant -> mViewModel!!.getCarts(Constants.STATUS_ADMIN_CANCEL)
            R.id.action_search_done_customer -> mViewModel!!.getCarts(Constants.STATUS_DONE)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Home -> Navigation.findNavController(requireView()).navigate(R.id.mainFragment)
            R.id.shoppingCart -> Toast.makeText(context, "You are still there", Toast.LENGTH_SHORT).show()
            R.id.News -> Navigation.findNavController(requireView()).navigate(R.id.newFragment)
            R.id.Profile -> Navigation.findNavController(requireView()).navigate(R.id.profileFragment)
        }
        return true
    }
    companion object {
        fun newInstance(): CartFragment {
            return CartFragment()
        }
    }
}