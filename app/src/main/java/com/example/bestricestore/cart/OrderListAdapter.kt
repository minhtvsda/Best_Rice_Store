package com.example.bestricestore.cart
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.bestricestore.R
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.example.bestricestore.databinding.ListOrderBinding

class OrderListAdapter(
    private val orderList: List<CartItem?>?,
    private val listener: ListOrderListener,
    private val context: Context?
) : RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {
    interface ListOrderListener {
        fun onItemOrderClick(cartId: String?, status: String?)
        fun onImageClick(url: String?)
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderViewBinding: ListOrderBinding

        init {
            orderViewBinding = ListOrderBinding.bind(itemView)
        }

        fun bindData(cData: CartItem?) {
            orderViewBinding.cartName.text = "Name: " + cData!!.name
            orderViewBinding.cartUsername.text = "Receiver name: " + cData.username
            orderViewBinding.cartCost.text = "Cost: " + cData.cost
            orderViewBinding.cartQuantity.text = "Quantity: " + cData.quantity
            orderViewBinding.cartUseraddress.text = "User address: " + cData.useraddress
            orderViewBinding.cartStatus.text = cData.status
            orderViewBinding.cartUserphone.text = "UserPhone: " + cData.userPhonenumber
            orderViewBinding.time.text = "Time: " + cData.time
            if (cData.status != Constants.STATUS_WAITING) {
                orderViewBinding.btnAccept.visibility = View.GONE
            }
            orderViewBinding.btnAccept.setOnClickListener { v: View? ->
                listener.onItemOrderClick(
                    cData!!.id,
                    Constants.STATUS_DELIVERING
                )
            }
            if (cData.status != Constants.STATUS_DELIVERING) {
                orderViewBinding.btnDeliveryDone.visibility = View.GONE
            }
            Glide.with(context!!).load(cData.imageUrl)
                .override(70, 130)
                .fitCenter().error(R.drawable.profile).into(orderViewBinding.imageView)
            orderViewBinding.imageView.setOnClickListener { v: View? -> listener.onImageClick(cData.imageUrl) }
            orderViewBinding.btnDeliveryDone.setOnClickListener { v: View? ->
                listener.onItemOrderClick(
                    cData!!.id,
                    Constants.STATUS_DONE
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemview = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_order, parent, false)
        return OrderViewHolder(itemview)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val cData = orderList!![position]
        holder.bindData(cData)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }
}