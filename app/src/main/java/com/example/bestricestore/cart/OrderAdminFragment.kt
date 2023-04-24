package com.example.bestricestore.cart

import android.app.DatePickerDialog
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
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.bestricestore.R
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.example.bestricestore.databinding.FragmentOrderAdminBinding
import com.example.bestricestore.ImageDialogFragment
import com.example.bestricestore.notification.NotificationData
import com.example.bestricestore.notification.PushNotification
import com.example.bestricestore.notification.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderAdminFragment : Fragment(), AdorderListAdapter.ListAdorderListener {
    private var mViewModel: OrderAdminViewModel? = null
    private var binding: FragmentOrderAdminBinding? = null
    private var adapter: AdorderListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val app = activity as AppCompatActivity?
        app!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        app!!.supportActionBar!!.title = "Checking Order"

        setHasOptionsMenu(true)
        mViewModel = ViewModelProvider(this).get(OrderAdminViewModel::class.java)
        binding = FragmentOrderAdminBinding.inflate(inflater, container, false)
        val rv = binding!!.recyclerViewOrder
        rv.setHasFixedSize(true)
        rv.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )
        mViewModel!!.orderList.observe(
            viewLifecycleOwner
        ) { orderList: List<CartItem?>? ->
            adapter = AdorderListAdapter(orderList, this, context)
            rv.adapter = adapter
            rv.layoutManager = LinearLayoutManager(activity)
        }
        binding!!.bottomNavigation.setOnNavigationItemSelectedListener { onNavigationItemSelected(it) }
        return binding!!.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_adorderfragment, menu)
        val menuItem = menu.findItem(R.id.action_search_order)
        val sv = MenuItemCompat.getActionView(menuItem) as SearchView
        sv.maxWidth = Int.MAX_VALUE
        sv.queryHint = "Search by Time,Name,cost, user phone number"
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (mViewModel!!.orderList.value != null) {
                    sv.onActionViewCollapsed()
                    filter(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (mViewModel!!.orderList.value != null) {
                    filter(newText)
                }
                return false
            }
        })
    }

    private fun filter(newText: String) {
        val filterList: MutableList<CartItem?> = ArrayList()
        for (ci in mViewModel!!.orderList.value!!) {
            if (ci.time!!.lowercase(Locale.getDefault())
                    .contains(newText.lowercase(Locale.getDefault()))
                || ci.name!!.lowercase(Locale.getDefault())
                    .contains(newText.lowercase(Locale.getDefault()))
                || ci.cost.toString().lowercase(Locale.getDefault()).contains(
                    newText.lowercase(
                        Locale.getDefault()
                    )
                )
                || ci.userPhonenumber!!.lowercase(Locale.getDefault())
                    .contains(newText.lowercase(Locale.getDefault()))
            ) {
                filterList.add(ci)
            }
            adapter!!.filterList(filterList)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search_waiting_restaurant -> mViewModel!!.getOrder(Constants.STATUS_WAITING_RESTAURANT)
            R.id.action_search_waiting_deliverer -> mViewModel!!.getOrder(Constants.STATUS_WAITING)
            R.id.action_search_user_cancel -> mViewModel!!.getOrder(Constants.STATUS_CANCEL)
            R.id.action_search_restaurant_cancel -> mViewModel!!.getOrder(Constants.STATUS_ADMIN_CANCEL)
            R.id.action_search_delivering -> mViewModel!!.getOrder(Constants.STATUS_DELIVERING)
            R.id.action_search_done -> mViewModel!!.getOrder(Constants.STATUS_DONE)
            R.id.action_calculator -> calculator()
            R.id.action_calendar -> showCalendar()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showCalendar() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            requireContext(), { view, year1, month1, day1 ->
                var month1 = month1
                month1 += 1
                val date = "$day1/$month1/$year1"
                filterDate(date)
                Toast.makeText(context, "Search date with specific day!", Toast.LENGTH_LONG).show()
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun filterDate(newText: String) {
        val filterList: MutableList<CartItem?> = ArrayList()
        for (ci in mViewModel!!.orderList.value!!) {
            if (ci.time!!.lowercase(Locale.getDefault())
                    .contains(newText.lowercase(Locale.getDefault()))
            ) {
                filterList.add(ci)
            }
            adapter!!.filterList(filterList)
        }
    }

    private fun calculator() {
        var totalRevenue = 0
        for (ci in mViewModel!!.orderList.value!!) {
            totalRevenue += ci.cost
        }
        val fragment = TotalRevenueDialogFragment()
        val result = Bundle()
        result.putString("bundleKeytotalRevenue", Integer.toString(totalRevenue))
        childFragmentManager.setFragmentResult("requestKey", result)
//        fragment.show(childFragmentManager, "TAG")
    }

    override fun onItemAdorderClick(cartId: String?, status: String?) {
        val db = FirebaseFirestore.getInstance()
        val formatter = SimpleDateFormat("HH:mm dd/MM/yyyy")
        val now = Date()
        val time = formatter.format(now)
        if (status === Constants.STATUS_WAITING) {
            db.collection(Constants.FS_FOOD_CART).document(cartId!!)
                .update(
                    "status", Constants.STATUS_WAITING,
                    "time", time
                )
                .addOnSuccessListener {
                    Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully updated!")
                    mViewModel!!.getOrder(Constants.STATUS_WAITING_RESTAURANT)
                    Toast.makeText(context, "Accept bill successfully!!", Toast.LENGTH_SHORT).show()
                }
        } else {
            db.collection(Constants.FS_FOOD_CART).document(cartId!!)
                .update(
                    "status", Constants.STATUS_ADMIN_CANCEL,
                    "time", time
                )
                .addOnSuccessListener {
                    Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully updated!")
                    mViewModel!!.getOrder(Constants.STATUS_WAITING_RESTAURANT)
                    Toast.makeText(context, "Cancel bill successfully!!", Toast.LENGTH_SHORT).show()
                }
        }
        db.collection(Constants.FS_FOOD_CART).document(cartId!!)
            .get()
            .addOnCompleteListener {
                this.pushNotification(status, it.result.getString("userToken").toString())
            }
    }
    private fun pushNotification(status: String?, userToken : String){
        val title = if (status === Constants.STATUS_WAITING) Constants.PUSH_NOTIFY_ADMIN_WAITING else Constants.PUSH_NOTIFY_ADMIN_CANCEL
        val message = if (status === Constants.STATUS_WAITING) Constants.PUSH_NOTIFY_ADMIN_WAITING else Constants.PUSH_NOTIFY_ADMIN_CANCEL

        PushNotification(NotificationData(title, message), userToken)  //whom we send notification
            .also {
                sendNotification(it)
            }
    }
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val  respond = RetrofitInstance.api.postNotification(notification)
            if(respond.isSuccessful){
                Log.d("Order Send Notification", "Respond: ${Gson().toJson(respond)}")
            } else{
                Log.e("sendNotification", respond.errorBody().toString())
            }
        } catch (e: Exception){
            Log.e("sendNotification",  e.toString())

//            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
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
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.Home -> Navigation.findNavController(requireView()).navigate(R.id.mainFragment)
            R.id.shoppingCart -> Toast.makeText(context, "You are still there", Toast.LENGTH_SHORT).show()
            R.id.News -> Navigation.findNavController(requireView()).navigate(R.id.newFragment)
            R.id.Profile -> Navigation.findNavController(requireView()).navigate(R.id.profileFragment)
        }
        return true
    }
    companion object {
        fun newInstance(): OrderAdminFragment {
            return OrderAdminFragment()
        }
    }
}