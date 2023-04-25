package com.example.bestricestore.feedback

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.Respond
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class EditorRespondViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var respond: MutableLiveData<Respond> = MutableLiveData()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    fun getRespondById(id: String?) {
        db.collection(Constants.FS_RESPOND) //lay collection
            .document((id)!!)
            .get()
            .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                override fun onComplete(task: Task<DocumentSnapshot>) {
                    if (task.isSuccessful) { //colection la` 1 tap hop cac document
                        val document: DocumentSnapshot = task.result
                        if (document.exists()) {
                            Log.d(
                                Constants.FIRE_STORE,
                                document.id + " => " + document.data
                            )
                            val r: Respond =
                                Respond.getRespondFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                            respond.value = r
                        }
                    }
                }
            })
    }
}