package com.example.bestricestore.data

import com.google.firebase.firestore.DocumentSnapshot

class FoodEntity @JvmOverloads constructor(
    var id: String? = Constants.EMPTY_STRING,
    var type: String? = Constants.EMPTY_STRING,
    var name: String? = Constants.EMPTY_STRING,
    var cost: Int = 0,
    var description: String? = Constants.EMPTY_STRING,
    var imageUrl: String? = Constants.EMPTY_STRING,
    var salePercent: Int = 0,
    var currentStatus: String? = Constants.AVAILABLE_CURRENT_STATUS,
    var totalLike : Int? = 0,
    var totalSell : Int? = 0,
) {
    // tao ra 1 map
    // lay title


    val mapWithoutId: Map<String, Any?>
        get() {
            val bMap: MutableMap<String, Any?> = HashMap() // tao ra 1 map
            bMap["type"] = type // lay title
            bMap["name"] = name
            bMap["cost"] = cost
            bMap["description"] = description
            bMap["imageUrl"] = imageUrl
            bMap["salePercent"] = salePercent
            bMap["currentStatus"] = currentStatus
            bMap["totalLike"] = totalLike
            bMap["totalSell"] = totalSell
            return bMap
        }

    companion object {
        const val typeString = "type"
        fun getFoodFromFirestore(document: DocumentSnapshot): FoodEntity {
            return if (document.exists()) {
                val data = document.data
                val id = document.id
                val type = data!![typeString].toString()
                val name = data["name"].toString()
                val cost = data["cost"].toString().toInt()
                val description = data["description"].toString()
                val imageUrl = data["imageUrl"].toString()
                val salePercent = data["salePercent"].toString().toInt()
                val currentStatus = data["currentStatus"].toString()
                val totalLike = data.getOrDefault("totalLike", 0).toString().toInt()
                val totalSell = data.getOrDefault("totalSell", 0).toString().toInt()
                FoodEntity(id, type, name, cost, description, imageUrl, salePercent, currentStatus, totalLike, totalSell)
            } else {
                FoodEntity()
            }
        }

    }
}