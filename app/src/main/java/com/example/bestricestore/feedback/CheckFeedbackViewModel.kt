package com.example.bestricestore.feedback

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class CheckFeedbackViewModel constructor() : ViewModel() {
    // TODO: Implement the ViewModel
    var feedbackList: MutableLiveData<List<Feedback>> = MutableLiveData()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var user: FirebaseUser? = FirebaseAuth.getInstance()
        .getCurrentUser()//bien moi 1 document tu firestore(K,V) thanh be//colection la` 1 tap hop cac document

    //lay collection
    //lay tat ca ve
    val feedbacks: Unit
        get() {
            val fList: MutableList<Feedback> = ArrayList()
            db.collection(Constants.FS_FEEDBACK) //lay collection
                .get() //lay tat ca ve
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                    public override fun onComplete(task: Task<QuerySnapshot>) {
                        if (task.isSuccessful()) { //colection la` 1 tap hop cac document
                            for (document: QueryDocumentSnapshot in task.getResult()) {
                                Log.d(
                                    Constants.FIRE_STORE,
                                    document.getId() + " => " + document.getData()
                                )
                                val f: Feedback =
                                    Feedback.Companion.getFeedbackFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                                fList.add(f)
                            }
                            feedbackList.setValue(fList)
                        } else {
                            Log.w(
                                Constants.FIRE_STORE,
                                "Error getting documents.",
                                task.getException()
                            )
                        }
                    }
                })
        }

    fun usergetFeedbacks() {
        val fList: MutableList<Feedback> = ArrayList()
        db.collection(Constants.FS_FEEDBACK) //lay collection
            .whereEqualTo("userId", user!!.getUid())
            .limit(20)
            .get() //lay tat ca ve
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                public override fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful()) { //colection la` 1 tap hop cac document
                        for (document: QueryDocumentSnapshot in task.getResult()) {
                            Log.d(
                                Constants.FIRE_STORE,
                                document.getId() + " => " + document.getData()
                            )
                            val f: Feedback =
                                Feedback.Companion.getFeedbackFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                            fList.add(f)
                        }
                        feedbackList.setValue(fList)
                    } else {
                        Log.w(Constants.FIRE_STORE, "Error getting documents.", task.getException())
                    }
                }
            })
    }

    fun usergetFeedbacksByStatus(status: String?) {
        val fList: MutableList<Feedback> = ArrayList()
        db.collection(Constants.FS_FEEDBACK) //lay collection
            .whereEqualTo("userId", user!!.getUid())
            .whereEqualTo("feedbackStatus", status)
            .limit(20)
            .get() //lay tat ca ve
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                public override fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful()) { //colection la` 1 tap hop cac document
                        for (document: QueryDocumentSnapshot in task.getResult()) {
                            Log.d(
                                Constants.FIRE_STORE,
                                document.getId() + " => " + document.getData()
                            )
                            val f: Feedback =
                                Feedback.Companion.getFeedbackFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                            fList.add(f)
                        }
                        feedbackList.setValue(fList)
                    } else {
                        Log.w(Constants.FIRE_STORE, "Error getting documents.", task.getException())
                    }
                }
            })
    }

    fun getFeedbacksByStatus(status: String?) {
        val fList: MutableList<Feedback> = ArrayList()
        db.collection(Constants.FS_FEEDBACK) //lay collection
            .whereEqualTo("feedbackStatus", status)
            .get() //lay tat ca ve
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                public override fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful()) { //colection la` 1 tap hop cac document
                        for (document: QueryDocumentSnapshot in task.getResult()) {
                            Log.d(
                                Constants.FIRE_STORE,
                                document.getId() + " => " + document.getData()
                            )
                            val f: Feedback =
                                Feedback.Companion.getFeedbackFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                            fList.add(f)
                        }
                        feedbackList.setValue(fList)
                    } else {
                        Log.w(Constants.FIRE_STORE, "Error getting documents.", task.getException())
                    }
                }
            })
    }
}