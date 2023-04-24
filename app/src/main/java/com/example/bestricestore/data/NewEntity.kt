package com.example.bestricestore.data

import com.google.firebase.firestore.DocumentSnapshot

class NewEntity {
    var id: String? = null
    var title: String? = null
    var newInfo: String? = null
    var imageUrl: String? = null
    var date: String? = null

    constructor() {}
    constructor(title: String?, newInfo: String?, imageUrl: String?) {
        id = Constants.EMPTY_STRING
        this.title = title
        this.newInfo = newInfo
        this.imageUrl = imageUrl
    }

    constructor(id: String?, title: String?, newInfo: String?, imageUrl: String?, date: String?) {
        this.id = id
        this.title = title
        this.newInfo = newInfo
        this.imageUrl = imageUrl
        this.date = date
    }

    // tao ra 1 map
    // lay title
    val mapWithoutId: Map<String, Any?>
        get() {
            val bMap: MutableMap<String, Any?> = HashMap() // tao ra 1 map
            bMap["title"] = title // lay title
            bMap["newInfo"] = newInfo
            bMap["imageUrl"] = imageUrl
            bMap["date"] = date
            return bMap
        }

    companion object {
        fun getNewFromFirestore(document: DocumentSnapshot): NewEntity {
            return if (document.exists()) {
                val data = document.data
                val id = document.id
                val title = data!!["title"].toString()
                val newInfo = data["newInfo"].toString()
                val imageUrl = data["imageUrl"].toString()
                val date = data["date"].toString()
                NewEntity(id, title, newInfo, imageUrl, date)
            } else {
                NewEntity()
            }
        }
    }
}