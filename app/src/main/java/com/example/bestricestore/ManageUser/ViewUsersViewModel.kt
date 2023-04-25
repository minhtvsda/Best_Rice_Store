package com.example.bestricestore.ManageUser

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*

class ViewUsersViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var userList: MutableLiveData<List<User>> = MutableLiveData()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        users
    }//bien moi 1 document tu firestore(K,V) thanh be//colection la` 1 tap hop cac document//lay collection

    //lay tat ca ve
//bien moi 1 document tu firestore(K,V) thanh be//colection la` 1 tap hop cac document
    //lay collection
    //lay tat ca ve
    val users: Unit
        get() {
            val uList: MutableList<User> = ArrayList()
            db.collection(Constants.FS_USER) //lay collection
                .whereEqualTo("roles", Constants.ROLE_CUSTOMER)
                .get() //lay tat ca ve
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                    override fun onComplete(task: Task<QuerySnapshot>) {
                        if (task.isSuccessful) { //colection la` 1 tap hop cac document
                            for (document: QueryDocumentSnapshot in task.result) {
                                Log.d(
                                    Constants.FIRE_STORE,
                                    document.id + " => " + document.data
                                )
                                val u: User =
                                    User.getUserIdFromFireStore(document) //bien moi 1 document tu firestore(K,V) thanh be
                                uList.add(u)
                            }
                            userList.setValue(uList)
                        } else {
                            Log.w(
                                Constants.FIRE_STORE,
                                "Error getting documents.",
                                task.exception
                            )
                        }
                    }
                })
            db.collection(Constants.FS_USER) //lay collection
                .whereEqualTo("roles", Constants.ROLE_DELIVERER)
                .get() //lay tat ca ve
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                    override fun onComplete(task: Task<QuerySnapshot>) {
                        if (task.isSuccessful) { //colection la` 1 tap hop cac document
                            for (document: QueryDocumentSnapshot in task.result) {
                                Log.d(
                                    Constants.FIRE_STORE,
                                    document.id + " => " + document.data
                                )
                                val u: User =
                                    User.getUserIdFromFireStore(document) //bien moi 1 document tu firestore(K,V) thanh be
                                uList.add(u)
                            }
                            userList.setValue(uList)
                        } else {
                            Log.w(
                                Constants.FIRE_STORE,
                                "Error getting documents.",
                                task.exception
                            )
                        }
                    }
                })
        }

    fun getUserByRole(roles: String?) {
        val uList: MutableList<User> = ArrayList()
        db.collection(Constants.FS_USER) //lay collection
            .whereEqualTo("roles", roles)
            .get() //lay tat ca ve
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                override fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful) { //colection la` 1 tap hop cac document
                        for (document: QueryDocumentSnapshot in task.result) {
                            Log.d(
                                Constants.FIRE_STORE,
                                document.id + " => " + document.data
                            )
                            val u: User =
                                User.getUserIdFromFireStore(document) //bien moi 1 document tu firestore(K,V) thanh be
                            uList.add(u)
                        }
                        userList.setValue(uList)
                    } else {
                        Log.w(Constants.FIRE_STORE, "Error getting documents.", task.exception)
                    }
                }
            })
    }
}