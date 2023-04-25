package com.example.bestricestore.newfeed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.util.*

class EditorNewViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var news: MutableLiveData<NewEntity?> = MutableLiveData()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    fun getNewById(id: String?) {
        if (id === Constants.NEW_ID) {
            news.value = NewEntity()
            return
        }
        val docRef: DocumentReference = db.collection(Constants.FS_NEW_SET).document(
            (id)!!
        ) // lay document theo id
        docRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
            //lay du lieu
            override fun onComplete(task: Task<DocumentSnapshot>) {
                if (task.isSuccessful) {
                    val document: DocumentSnapshot = task.result
                    if (document.exists()) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.data)
                        val n: NewEntity = NewEntity.getNewFromFirestore(document)
                        news.setValue(n)
                    } else {
                        Log.d(Constants.FIRE_STORE, "No such document")
                    }
                } else {
                    Log.d(Constants.FIRE_STORE, "get failed with ", task.exception)
                }
            }
        })
    }

    fun updateNew(updateNew: NewEntity) {
        val bMap: Map<String, Any?> = updateNew.mapWithoutId
        if (updateNew.id == null || (updateNew.id == Constants.NEW_ID)) {
            val id: String = "New" + (Constants.TIME - Date().time)
            //insert new
            db.collection(Constants.FS_NEW_SET).document(id)
                .set((bMap))
                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                    override fun onComplete(task: Task<Void?>) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully written!")
                    }
                })
        } else {
            //update
            db.collection(Constants.FS_NEW_SET)
                .document(updateNew.id.toString()) //chon id ma ta muon sua
                .set((bMap)) //sua noi dung thong qua bMap
                .addOnSuccessListener(object : OnSuccessListener<Void?> {
                    override fun onSuccess(aVoid: Void?) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully written!")
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        Log.w(Constants.FIRE_STORE, "Error writing document", e)
                    }
                })
        }
    }

    fun deleteNew() {
        db.collection(Constants.FS_NEW_SET).document(news.value!!.id.toString())
            .delete()
            .addOnSuccessListener(object : OnSuccessListener<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully deleted!")
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    Log.w(Constants.FIRE_STORE, "Error deleting document", e)
                }
            })
    }
}