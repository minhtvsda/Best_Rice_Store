package com.example.bestricestore.cart

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckOrderViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var userDeliname: String? = null
    var userdeliPhone: String? = null
    var orderList = MutableLiveData<List<CartItem>>()
    var user = FirebaseAuth.getInstance().currentUser
    var db = FirebaseFirestore.getInstance()

    init {
        order
    }

    //                                userdeliPhone = document.getString("phoneNumber");
    val profile: Unit
        get() {
            if (user == null) {
                return
            }
            db.collection(Constants.FS_USER).document(user!!.uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document.exists()) {
                            Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.data)
                            userDeliname = document.getString("username")
                            //                                userdeliPhone = document.getString("phoneNumber");
                        } else {
                            Log.d(ContentValues.TAG, "No such document")
                        }
                    } else {
                        Log.d(ContentValues.TAG, "get failed with ", task.exception)
                    }
                }
        }//colection la` 1 tap hop cac document

    //lay collection
    val order: Unit
        get() {
            val cList: MutableList<CartItem> = ArrayList()
            db.collection(Constants.FS_FOOD_CART) //lay collection
                .whereEqualTo("status", Constants.STATUS_WAITING)
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

    fun getDeliOrder(status: String?) {
        val cList: MutableList<CartItem> = ArrayList()
        db.collection(Constants.FS_FOOD_CART) //lay collection
            .whereEqualTo("deliverername", userDeliname)
            .whereEqualTo("status", status)
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