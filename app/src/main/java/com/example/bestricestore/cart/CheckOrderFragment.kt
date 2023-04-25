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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.bestricestore.R
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.example.bestricestore.databinding.FragmentCheckOrderBinding
import com.example.bestricestore.ImageDialogFragment
import com.example.bestricestore.data.Constants.PUSH_NOTIFY_ORDER_DONE
import com.example.bestricestore.notification.NotificationData
import com.example.bestricestore.notification.PushNotification
import com.example.bestricestore.notification.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
class CheckOrderFragment : Fragment(), OrderListAdapter.ListOrderListener {
    private var mViewModel: CheckOrderViewModel? = null
    private var binding: FragmentCheckOrderBinding? = null
    private var adapter: OrderListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val app: AppCompatActivity? = activity as AppCompatActivity?
        val ab: ActionBar? = app!!.supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(false)
        ab.title = "Checking Order"
        mViewModel = ViewModelProvider(this).get(CheckOrderViewModel::class.java)
        binding = FragmentCheckOrderBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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
            adapter = OrderListAdapter(orderList, this, context)
            rv.adapter = adapter
            rv.layoutManager = LinearLayoutManager(activity)
        }
        mViewModel!!.profile
        binding!!.bottomNavigation.setOnNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }
        return binding!!.root
    }

    override fun onItemOrderClick(cartId: String?, status: String?) {
        val formatter = SimpleDateFormat("ss:mm:HH dd/MM/yyyy")
        val now = Date()
        val time = formatter.format(now)
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (status === Constants.STATUS_DELIVERING) {

            db.collection(Constants.FS_FOOD_CART).document(cartId!!)
                .update(
                    "status", Constants.STATUS_DELIVERING,
                    "deliverername", mViewModel!!.userDeliname,
                    "delivererid", user!!.uid,
                    "deliPhone", user.phoneNumber,
                    "time", time
                )
                .addOnSuccessListener {
                    Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully updated!")
                    mViewModel!!.order
                    Toast.makeText(context, "Accept bill successfully!!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w(
                        Constants.FIRE_STORE,
                        "Error updating document",
                        e
                    )
                }
        } else {
            db.collection(Constants.FS_FOOD_CART).document(cartId!!)
                .update(
                    "status", Constants.STATUS_DONE,
                    "deliverername", mViewModel!!.userDeliname,
                    "delivererid", user!!.uid,
                    "time", time
                )
                .addOnSuccessListener {
                    Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully updated!")
                    mViewModel!!.order
                    Toast.makeText(context, "Accept bill successfully!!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w(
                        Constants.FIRE_STORE,
                        "Error updating document",
                        e
                    )
                }
        }
        db.collection(Constants.FS_FOOD_CART).document(cartId)
            .get()
            .addOnCompleteListener {
                this.pushNotification(status, it.result.getString("userToken").toString()
                )

            }
    }

    private fun pushNotification(status: String?, userToken : String){

        val title = if (status === Constants.STATUS_DELIVERING) Constants.PUSH_NOTIFY_ORDER_DELIVERING else PUSH_NOTIFY_ORDER_DONE
        val message = if (status === Constants.STATUS_DELIVERING) Constants.PUSH_NOTIFY_ORDER_DELIVERING else PUSH_NOTIFY_ORDER_DONE

        PushNotification(NotificationData(title, message), userToken)  //whom we send notification
            .also {
                sendNotification(it)
            }
    }
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val respond = RetrofitInstance.api.postNotification(notification)
            if (respond.isSuccessful) {
                Log.d("Order Send Notification", "Respond: ${Gson().toJson(respond)}")
            } else {
                Log.e("sendNotification", respond.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.w(
                "sendNotification",
                "Error Notification",
                e
            )
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
        inflater.inflate(R.menu.menu_orderfragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search_waiting_deliverer -> mViewModel?.order
            R.id.action_search_deliverinng -> mViewModel!!.getDeliOrder(Constants.STATUS_DELIVERING)
            R.id.action_search_done -> mViewModel!!.getDeliOrder(Constants.STATUS_DONE)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Home -> Navigation.findNavController(requireView()).navigate(R.id.mainFragment)
            R.id.shoppingCart -> Toast.makeText(
                context,
                "You are still there",
                Toast.LENGTH_SHORT
            ).show()
            R.id.News -> Navigation.findNavController(requireView()).navigate(R.id.newFragment)
            R.id.Profile -> Navigation.findNavController(requireView())
                .navigate(R.id.profileFragment)
        }
        return true
    }





}