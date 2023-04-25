package com.example.bestricestore.newfeed

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

class NewViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var newList: MutableLiveData<List<NewEntity>> = MutableLiveData()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    init {
        news
    }//bien moi 1 document tu firestore(K,V) thanh be//colection la` 1 tap hop cac document

    //lay collection
    //lay tat ca ve
    val news: Unit
        get() {
            val List: MutableList<NewEntity> = ArrayList()
            db.collection(Constants.FS_NEW_SET) //lay collection
                .limit(10)
                .get() //lay tat ca ve
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                    override fun onComplete(task: Task<QuerySnapshot>) {
                        if (task.isSuccessful) { //colection la` 1 tap hop cac document
                            for (document: QueryDocumentSnapshot in task.result) {
                                Log.d(
                                    Constants.FIRE_STORE,
                                    document.id + " => " + document.data
                                )
                                val n: NewEntity =
                                    NewEntity.getNewFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                                List.add(n)
                            }
                            newList.setValue(List)
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
}