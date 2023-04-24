package com.example.bestricestore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import java.util.*


class EditorViewModel constructor() : ViewModel() {
    // TODO: Implement the ViewModel
    var food: MutableLiveData<FoodEntity> = MutableLiveData()
    var user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    fun getFoodById(id: String) {
        if (id === Constants.NEW_ID) {
            food.setValue(FoodEntity())
            return
        }
        val docRef: DocumentReference =
            db.collection(Constants.FS_FOOD_SET).document(id) // lay document theo id
        docRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot?> {
            //lay du lieu
            override fun onComplete(task: Task<DocumentSnapshot?>) {
                if (task.isSuccessful) {
                    val document: DocumentSnapshot = task.result!!
                    if (document.exists()) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.getData())
                        val f: FoodEntity = FoodEntity.getFoodFromFirestore(document)
                        food.setValue(f)
                    } else {
                        Log.d(Constants.FIRE_STORE, "No such document")
                    }
                } else {
                    Log.d(Constants.FIRE_STORE, "get failed with ", task.getException())
                }
            }

        })
    }

    fun updateFood(updateFood: FoodEntity) {
        val bMap: Map<String, Any?> = updateFood.mapWithoutId
        if (updateFood.id === Constants.EMPTY_STRING) {
            //insert new
            db.collection(Constants.FS_FOOD_SET)
                .add((bMap)!!)
                .addOnSuccessListener(object : OnSuccessListener<DocumentReference?> {
                    public override fun onSuccess(documentReference: DocumentReference?) {
                        Log.d(
                            Constants.FIRE_STORE,
                            "DocumentSnapshot written with ID: " + documentReference!!.getId()
                        )
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    public override fun onFailure(e: Exception) {
                        Log.w(Constants.FIRE_STORE, "Error adding document", e)
                    }
                })

        } else {
            //update
            db.collection(Constants.FS_FOOD_SET)
                .document(updateFood.id!!) //chon id ma ta muon sua
                .set((bMap)!!) //sua noi dung thong qua bMap
                .addOnSuccessListener(object : OnSuccessListener<Void?> {
                    public override fun onSuccess(aVoid: Void?) {
                        Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully written!")
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    public override fun onFailure(e: Exception) {
                        Log.w(Constants.FIRE_STORE, "Error writing document", e)
                    }
                })
        }
    }

    fun deleteFood() {
        db.collection(Constants.FS_FOOD_SET).document(food.value!!.id!!)
            .delete()
            .addOnSuccessListener(object : OnSuccessListener<Void?> {
                public override fun onSuccess(aVoid: Void?) {
                    Log.d(Constants.FIRE_STORE, "DocumentSnapshot successfully deleted!")
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                public override fun onFailure(e: Exception) {
                    Log.w(Constants.FIRE_STORE, "Error deleting document", e)
                }
            })
    }
}