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

class EditorCheckViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var muser: MutableLiveData<User> = MutableLiveData()
    fun getUserById(id: String?) {
        val docRef: DocumentReference =
            FirebaseFirestore.getInstance().collection(Constants.FS_USER).document(
                (id)!!
            ) // lay document theo id
        docRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
            //lay du lieu
            override fun onComplete(task: Task<DocumentSnapshot>) {
                if (task.isSuccessful) {
                    val document: DocumentSnapshot = task.result
                    if (document.exists()) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.data)
                        val u: User = User.getUserIdFromFireStore(document)
                        muser.setValue(u)
                    } else {
                        Log.d(Constants.FIRE_STORE, "No such document")
                    }
                } else {
                    Log.d(Constants.FIRE_STORE, "get failed with ", task.exception)
                }
            }
        })
    }
}