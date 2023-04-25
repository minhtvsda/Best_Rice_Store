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

class EditorFeedbackViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var feedback: MutableLiveData<Feedback> = MutableLiveData()
    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    fun getFeedbackById(id: String?) {
        val docRef: DocumentReference = db.collection(Constants.FS_FEEDBACK).document(
            (id)!!
        ) // lay document theo id
        docRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
            //lay du lieu
            override fun onComplete(task: Task<DocumentSnapshot>) {
                if (task.isSuccessful) {
                    val document: DocumentSnapshot = task.result
                    if (document.exists()) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.data)
                        val f: Feedback = Feedback.getFeedbackFromFirestore(document)
                        feedback.setValue(f)
                    } else {
                        Log.d(Constants.FIRE_STORE, "No such document")
                    }
                } else {
                    Log.d(Constants.FIRE_STORE, "get failed with ", task.exception)
                }
            }
        })
    }

    fun sendRespond(respond: Respond?) {
        db.collection(Constants.FS_RESPOND)
            .document("Respond" + (Constants.TIME - Date().time))
            .set((respond)!!)
    }

    fun deleteFeedback(id: String?) {
        db.collection(Constants.FS_FEEDBACK).document((id)!!)
            .delete()
    }

    fun changFeedbackStatus(id: String?) {
        db.collection(Constants.FS_FEEDBACK).document((id)!!)
            .update("feedbackStatus", Constants.FEEDBACK_ALREADY_RESPONDED)
    }
}