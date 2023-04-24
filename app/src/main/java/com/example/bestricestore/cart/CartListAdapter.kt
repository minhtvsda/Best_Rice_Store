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
import com.example.bestricestore.databinding.ListAdorderBinding
import com.example.bestricestore.databinding.ListCartBinding

class CartListAdapter(
    private var cartList: List<CartItem?>?,
    private val listener: ListCartListener,
    private val context: Context?
) : RecyclerView.Adapter<CartListAdapter.CartViewHolder>() {
    interface ListCartListener {
        fun onItemCartClickDelete(cartId: String?)
        fun onImageClick(url: String?)
    }

    inner class CartViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    ) {
        private val cartViewBinding: ListCartBinding

        init {
            cartViewBinding = ListCartBinding.bind(itemView!!)
        }

        fun bindData(cData: CartItem?) {
            val status = cData!!.status
            cartViewBinding.cartName.text = "Food name: " + cData.name
            cartViewBinding.cartCost.text = "Total Cost: " + cData.cost
            cartViewBinding.cartType.text = "Type: " + cData.type
            cartViewBinding.cartQuantity.text = "Quantity: " + cData.quantity
            cartViewBinding.cartStatus.text = """
                   Status:
                   ${cData.status}
                   """.trimIndent()
            cartViewBinding.cartDeliname.text = "Deliverer's name: " + cData.deliverername
            cartViewBinding.cartDeliPhone.text = """Deli phone:
 ${cData.deliPhone}"""
            cartViewBinding.cartTime.text = "Time: " + cData.time
            cartViewBinding.cartUseraddress.text = """Place receive:
 ${cData.useraddress}"""
            if (cData.status != Constants.STATUS_WAITING_RESTAURANT) {
                cartViewBinding.btnCancel.visibility = View.GONE
            }
            if (status == Constants.STATUS_CANCEL || status == Constants.STATUS_WAITING || status == Constants.STATUS_WAITING_RESTAURANT || status == Constants.STATUS_ADMIN_CANCEL) {
                cartViewBinding.cartDeliname.visibility = View.GONE
                cartViewBinding.cartDeliPhone.visibility = View.GONE
            }
            cartViewBinding.btnCancel.setOnClickListener { v: View? ->
                listener.onItemCartClickDelete(
                    cData.id
                )
            }
            cartViewBinding.imageView.setOnClickListener { v: View? -> listener.onImageClick(cData.imageUrl) }
            Glide.with(context!!).load(cData.imageUrl)
                .error(R.drawable.profile)
                .override(70, 130)
                .fitCenter().into(cartViewBinding.imageView)
            cartViewBinding.cartNote.text = "Note: " + cData.note
        }
    }

    fun filterList(cartList: List<CartItem?>?) {
        this.cartList = cartList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_cart, parent, false)
        //meaning get everything from app
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cData = cartList!![position]
        holder.bindData(cData)
    }

    override fun getItemCount(): Int {
        return cartList!!.size
    }
}