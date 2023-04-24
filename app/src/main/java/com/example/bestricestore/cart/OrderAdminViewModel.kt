package com.example.bestricestore.cart

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderAdminViewModel : ViewModel() {
    var orderList = MutableLiveData<List<CartItem>?>()
    var user = FirebaseAuth.getInstance().currentUser
    var db = FirebaseFirestore.getInstance()

    init {
        getOrder(Constants.STATUS_WAITING_RESTAURANT)
    }

    fun getOrder(status: String?) {
        val cList: MutableList<CartItem> = ArrayList()
        db.collection(Constants.FS_FOOD_CART) //lay collection
            .whereEqualTo("status", status) //                .orderBy("time")
            .limit(20)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) { //colection la` 1 tap hop cac document
                    for (document in task.result) {
                        Log.d(Constants.FIRE_STORE, document.id + " => " + document.data)
                        val c: CartItem = CartItem.Companion.getCartFromFirestore(document)
                        cList.add(c)
                    }
                    orderList.setValue(cList)
                } else {
                    Log.w(Constants.FIRE_STORE, "Error getting documents.", task.exception)
                }
            }
    }
}