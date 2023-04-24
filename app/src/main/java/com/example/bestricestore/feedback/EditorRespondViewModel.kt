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

class EditorRespondViewModel constructor() : ViewModel() {
    // TODO: Implement the ViewModel
    var respond: MutableLiveData<Respond> = MutableLiveData()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    fun getRespondById(id: String?) {
        db.collection(Constants.FS_RESPOND) //lay collection
            .document((id)!!)
            .get()
            .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                public override fun onComplete(task: Task<DocumentSnapshot>) {
                    if (task.isSuccessful()) { //colection la` 1 tap hop cac document
                        val document: DocumentSnapshot = task.getResult()
                        if (document.exists()) {
                            Log.d(
                                Constants.FIRE_STORE,
                                document.getId() + " => " + document.getData()
                            )
                            val r: Respond =
                                Respond.Companion.getRespondFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                            respond.setValue(r)
                        }
                    }
                }
            })
    }
}