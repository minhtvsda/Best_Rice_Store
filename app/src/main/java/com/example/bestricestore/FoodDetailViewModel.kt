package com.example.bestricestore
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.bestricestore.data.*
import com.example.bestricestore.databinding.FragmentFoodDetailBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.text.SimpleDateFormat
import java.util.*

class FoodDetailViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var food: MutableLiveData<FoodEntity> = MutableLiveData()
    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentFoodDetailBinding
    private lateinit var navController: NavController
    private lateinit var context: Context

    fun getData(binding: FragmentFoodDetailBinding, navController: NavController, context: Context ){
        this.binding = binding
        this.navController = navController
        this.context = context
    }
    fun getFoodById(id: String?) {
      // lay document theo id
        val docRef: DocumentReference = db.collection(Constants.FS_FOOD_SET).document(
            (id)!!
        )
        docRef.get().addOnCompleteListener { task ->

            //lay du lieu
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.data)
                    val f: FoodEntity = FoodEntity.getFoodFromFirestore(document)
                    food.value = f
                    val doc = db.collection(Constants.FS_FOOD_SET+"/${f.id}/like")
                        .document(user!!.uid)
                        .get()
                        .addOnCompleteListener() {
                                task -> if (task.isSuccessful){
                            val document = task.result
                            if (document.exists()){
                                Log.d(Constants.FIRE_STORE, "DocumentSnapshot data: " + document.data)
                                val isLike = document.data!!["isLike"].toString()
                                if (isLike == "true"){
                                    binding.likeButton.isLiked = true
                                }
                            }
                        }
                        }
                } else {
                    Log.d(Constants.FIRE_STORE, "No such document")
                }
            } else {
                Log.d(Constants.FIRE_STORE, "get failed with ", task.exception)
            }
        }
    }

    fun setUserClickLike() {
        val map = hashMapOf(
            "isLike" to true
        )
        db.collection(Constants.FS_FOOD_SET+"/${food.value!!.id}/like")
            .document(user!!.uid)
            .set(map)
    }

    fun setUserClickDisLike() {
        val map = hashMapOf(
            "isLike" to false
        )
        db.collection(Constants.FS_FOOD_SET+"/${food.value!!.id}/like")
            .document(user!!.uid)
            .set(map)
    }


    fun addtoCart(
        foodId: String?,
        quantity: String,
        price: Int,
        userPhoneNumber: String?,
        username: String?,
        userAddress: String?,
        note: String?,
        userEmail: String?,
        userToken: String?
    ) {
        val userRef: DocumentReference = db.collection(Constants.FS_USER).document(
            user!!.uid
        )
        val docRef: DocumentReference = db.collection(Constants.FS_FOOD_SET).document(
            (foodId)!!
        )
        val formatter: SimpleDateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")
        val now: Date = Date()
        val time: String = formatter.format(now)
        val totalCost: Int = price * quantity.toInt()
        val timemilli: Long = Calendar.getInstance().timeInMillis
        val id: String = "Cart" + (Constants.TIME - timemilli)
        val cartItem    = CartItem( //                Constants.EMPTY_STRING,
            food.value!!.type,
            food.value!!.name,
            userPhoneNumber,
            totalCost,
            quantity.toInt(),
            user!!.uid,
            username,
            userAddress,
            note,
            Constants.STATUS_WAITING_RESTAURANT,
            Constants.EMPTY_STRING,
            Constants.EMPTY_STRING,
            Constants.EMPTY_STRING,
            time,
            food.value?.imageUrl.toString(),
            userEmail,
            userToken,
//            0,
//            0
        )
        db.collection(Constants.FS_FOOD_CART)
            .document(id)
            .set(cartItem)
        updateSell(quantity.toInt())
    }
    fun updateSell(intSell: Int){
        db.collection(Constants.FS_FOOD_SET).document(
            (food.value!!.id)!!
        ).update("totalSell", food.value!!.totalSell!! + intSell)
    }
    fun updateLike(intLike : Int) {
        val docRef: DocumentReference = db.collection(Constants.FS_FOOD_SET).document(
            (food.value!!.id)!!
        )
        docRef.update("totalLike", food.value!!.totalLike!! + intLike)
            .addOnSuccessListener { getFoodById(food.value?.id!!) }
    }

}