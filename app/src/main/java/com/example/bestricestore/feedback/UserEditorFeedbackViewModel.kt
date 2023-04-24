package com.example.bestricestore.feedback

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class UserEditorFeedbackViewModel constructor() : ViewModel() {
    // TODO: Implement the ViewModel
    var feedback: MutableLiveData<Feedback> = MutableLiveData()
    var user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    fun getFeedbackById(id: String?) {
        val docRef: DocumentReference = db.collection(Constants.FS_FEEDBACK).document(
            (id)!!
        ) // lay document theo id
        docRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
            //lay du lieu
            public override fun onComplete(task: Task<DocumentSnapshot>) {
                if (task.isSuccessful()) {
                    val document: DocumentSnapshot = task.getResult()
                    if (document.exists()) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.getData())
                        val f: Feedback = Feedback.Companion.getFeedbackFromFirestore(document)
                        feedback.setValue(f)
                    } else {
                        Log.d(Constants.FIRE_STORE, "No such document")
                    }
                } else {
                    Log.d(Constants.FIRE_STORE, "get failed with ", task.getException())
                }
            }
        })
    }
}