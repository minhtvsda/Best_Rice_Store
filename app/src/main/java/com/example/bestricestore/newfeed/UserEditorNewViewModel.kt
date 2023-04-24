package com.example.bestricestore.newfeed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class UserEditorNewViewModel constructor() : ViewModel() {
    // TODO: Implement the ViewModel
    var news: MutableLiveData<NewEntity?> = MutableLiveData()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    fun getNewById(id: String?) {
        val docRef: DocumentReference = db.collection(Constants.FS_NEW_SET).document(
            (id)!!
        ) // lay document theo id
        docRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
            //lay du lieu
            public override fun onComplete(task: Task<DocumentSnapshot>) {
                if (task.isSuccessful()) {
                    val document: DocumentSnapshot = task.getResult()
                    if (document.exists()) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.getData())
                        val n: NewEntity = NewEntity.Companion.getNewFromFirestore(document)
                        news.setValue(n)
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