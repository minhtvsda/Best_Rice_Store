package com.example.bestricestore.cart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CartViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var cartList = MutableLiveData<List<CartItem>>()

    init {
        getCarts(Constants.STATUS_WAITING_RESTAURANT)
    }

    fun getCarts(status: String?) {
        val cList: MutableList<CartItem> = ArrayList()
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.FS_FOOD_CART) //lay collection
            .whereEqualTo("userid", user!!.uid)
            .whereEqualTo("status", status)
            //                .orderBy("time")
            .limit(10)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) { //colection la` 1 tap hop cac document
                    for (document in task.result) {
                        Log.d(Constants.FIRE_STORE, document.id + " => " + document.data)
                        val c: CartItem = CartItem.Companion.getCartFromFirestore(document)
                        cList.add(c)
                    }
                    cartList.setValue(cList)
                } else {
                    Log.w(Constants.FIRE_STORE, "Error getting documents.", task.exception)
                }
            }
    }

    fun getcartFood(): MutableLiveData<List<CartItem>> {
        val list: List<CartItem> = ArrayList()
        cartList.value = list
        return cartList
    }
}