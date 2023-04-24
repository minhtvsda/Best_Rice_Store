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

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.bestricestore.R
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.example.bestricestore.databinding.ListAdorderBinding

class AdorderListAdapter(
    private var orderList: List<CartItem?>?,
    private val listener: ListAdorderListener,
    private val context: Context?
) : RecyclerView.Adapter<AdorderListAdapter.AdorderViewHolder>() {
    interface ListAdorderListener {
        fun onItemAdorderClick(cartId: String?, status: String?)
        fun onImageClick(url: String?)
    }

    inner class AdorderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val adorderListBinding: ListAdorderBinding

        init {
            adorderListBinding = ListAdorderBinding.bind(itemView)
        }

        fun bindData(cData: CartItem?) {
            val status = cData!!.status
            adorderListBinding.cartName.text = "Name: " + cData.name
            adorderListBinding.cartUsername.text = "Receiver name: " + cData.username
            adorderListBinding.cartCost.text = "Cost: " + cData.cost
            adorderListBinding.cartQuantity.text = "Quantity: " + cData.quantity
            adorderListBinding.cartUseraddress.text = "User address: " + cData.useraddress
            adorderListBinding.cartStatus.text = """
                   Status:
                   ${cData.status}
                   """.trimIndent()
            adorderListBinding.cartDeliveryName.text = "Delivery name: ${cData.deliverername} "
            if (status != Constants.STATUS_WAITING_RESTAURANT) {
                adorderListBinding.btnDeclinedBill.visibility = View.GONE
                adorderListBinding.btnAcceptBill.visibility = View.GONE
            }
            if (status == Constants.STATUS_CANCEL || status == Constants.STATUS_WAITING || status == Constants.STATUS_WAITING_RESTAURANT || status == Constants.STATUS_ADMIN_CANCEL) {
                adorderListBinding.cartDeliveryName.visibility = View.GONE
                adorderListBinding.cartDeliveryPhone.visibility = View.GONE
            }
            adorderListBinding.cartUserphone.text = "UserPhone: ${cData.userPhonenumber}"
            adorderListBinding.cartDeliveryPhone.text = "DeliPhone:" + cData.deliPhone
            adorderListBinding.cartTime.text = "Time: " + cData.time
            adorderListBinding.btnAcceptBill.setOnClickListener {
                listener.onItemAdorderClick(
                    cData.id,
                    Constants.STATUS_WAITING
                )
            }
            adorderListBinding.btnDeclinedBill.setOnClickListener {
                listener.onItemAdorderClick(
                    cData.id,
                    Constants.STATUS_ADMIN_CANCEL
                )
            }
            Glide.with(context!!).load(cData.imageUrl)
                .override(70, 130).fitCenter().error(R.drawable.profile)
                .into(adorderListBinding.imageView)
            adorderListBinding.imageView.setOnClickListener { v: View? ->
                listener.onImageClick(
                    cData.imageUrl
                )
            }
            adorderListBinding.cartNote.text = "Note: " + cData.note
            adorderListBinding.userEmail.text = "UserEmail: " + cData.userEmail
        }
    }

    fun filterList(filterList: List<CartItem?>?) {
        orderList = filterList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdorderViewHolder {
        val itemview = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_adorder, parent, false)
        return AdorderViewHolder(itemview)
    }

    override fun onBindViewHolder(holder: AdorderViewHolder, position: Int) {
        val cData = orderList!![position]
        holder.bindData(cData)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }
}