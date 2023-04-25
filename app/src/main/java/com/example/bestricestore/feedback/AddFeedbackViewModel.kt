package com.example.bestricestore.feedback

import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddFeedbackViewModel : ViewModel() {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var muser: User? = null
    fun sendFeedback(f: Feedback?) {
        db.collection(Constants.FS_FEEDBACK)
            .document("Feedback" + (Constants.TIME - Date().time))
            .set((f)!!)
    }

    init {
        db.collection(Constants.FS_USER)
            .document(user!!.uid)
            .get()
            .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                override fun onComplete(task: Task<DocumentSnapshot>) {
                    if (task.isSuccessful) {
                        if (task.result.exists()) {
                            muser = User.getUserFromFireStore(task.result)
                        }
                    }
                }
            })
    }
    // TODO: Implement the ViewModel
}