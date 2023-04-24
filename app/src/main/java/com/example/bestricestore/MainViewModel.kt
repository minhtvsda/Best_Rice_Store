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
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainViewModel constructor() : ViewModel() {


    var foodList: MutableLiveData<List<FoodEntity>> = MutableLiveData()
    var foodListLike: MutableLiveData<List<FoodEntity>> = MutableLiveData()
    var foodListSell: MutableLiveData<List<FoodEntity>> = MutableLiveData()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()

    // TODO: Implement the ViewModel
    //lay collection
    //lay tat ca ve
    init {
        getFoods("")
    }
    fun getFoods() {
            val fList: MutableList<FoodEntity> = ArrayList()
            db.collection(Constants.FS_FOOD_SET) //lay collection
                .orderBy("cost")
                .whereEqualTo("currentStatus", Constants.AVAILABLE_CURRENT_STATUS)
                .get() //lay tat ca ve
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                    public override fun onComplete(task: Task<QuerySnapshot>) {
                        if (task.isSuccessful()) { //colection la` 1 tap hop cac document
                            for (document: QueryDocumentSnapshot in task.getResult()) {
                                Log.d(
                                    Constants.FIRE_STORE,
                                    document.getId() + " => " + document.getData()
                                )
                                val f: FoodEntity =
                                    FoodEntity.Companion.getFoodFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                                    fList.add(f)
                            }
                            foodList.setValue(fList)
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
    fun getFoods(not_Type: String){
        val fList: MutableList<FoodEntity> = ArrayList()
        db.collection(Constants.FS_FOOD_SET) //lay collection
            .orderBy("cost")
            .whereEqualTo("currentStatus", Constants.AVAILABLE_CURRENT_STATUS)
            .get() //lay tat ca ve
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                public override fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful()) { //colection la` 1 tap hop cac document
                        for (document: QueryDocumentSnapshot in task.getResult()) {
                            Log.d(
                                Constants.FIRE_STORE,
                                document.getId() + " => " + document.getData()
                            )
                            val f: FoodEntity =
                                FoodEntity.Companion.getFoodFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                            fList.add(f)
                        }
                        if (not_Type != "not like") {
                            foodListLike.value = fList
                            sortBy("favorite")
                        }
                        if (not_Type != "not sold") {
                            val sellList = ArrayList<FoodEntity>()
                            sellList.addAll(fList)
                            foodListSell.value = sellList
                            sortBy("")
                        }

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
    fun sortBy(type: String){

        val compareByType: java.util.Comparator<FoodEntity> =
            Comparator<FoodEntity> { f1, f2 ->
                if (type == "favorite") -f1.totalLike!!.compareTo(f2.totalLike!!) else -f1.totalSell!!.compareTo(f2.totalSell!!)
            }
        Collections.sort(if (type == "favorite") foodListLike.value!! else foodListSell.value!!, compareByType)
    }
    fun getFoodAdmin() {
        val fList: MutableList<FoodEntity> = ArrayList()

        db.collection(Constants.FS_FOOD_SET) //lay collection
            .orderBy("cost")
            .get() //lay tat ca ve
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                public override fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful()) { //colection la` 1 tap hop cac document
                        for (document: QueryDocumentSnapshot in task.getResult()) {
                            Log.d(
                                Constants.FIRE_STORE,
                                document.getId() + " => " + document.getData()
                            )
                            val f: FoodEntity =
                                FoodEntity.Companion.getFoodFromFirestore(document) //bien moi 1 document tu firestore(K,V) thanh be
                            fList.add(f)
                        }
                        foodList.setValue(fList)
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

}