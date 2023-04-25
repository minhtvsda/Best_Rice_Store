package com.example.bestricestore.login

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var muser: MutableLiveData<User> = MutableLiveData()
    fun getUserProfile(id: String?) {
        val docRef: DocumentReference =
            FirebaseFirestore.getInstance().collection(Constants.FS_USER).document(
                (id)!!
            )
        docRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
            //lay du lieu
            override fun onComplete(task: Task<DocumentSnapshot>) {
                if (task.isSuccessful) {
                    val document: DocumentSnapshot = task.result
                    if (document.exists()) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.data)
                        val u: User = User.getUserFromFireStore(document)
                        muser.setValue(u)
                        //get role
//                        roleUser = document.getString("roles");
                    } else {
                        Log.d(Constants.FIRE_STORE, "No such document")
                    }
                } else {
                    Log.d(Constants.FIRE_STORE, "get failed with ", task.exception)
                }
            }
        })
    }

    fun updateUserProfiler(
        name: String?,
        email: String?,
        address: String?,
        role: String?,
        dob: String?,
        gender: String?,
        id: String?
    ) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val u: User = User(name, email, address, role, user!!.phoneNumber, dob, gender, muser.value?.tokenId)
        db.collection(Constants.FS_USER).document((id)!!).set(u)
            .addOnSuccessListener(object : OnSuccessListener<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                    muser.value = u
                }
            })
    }
}