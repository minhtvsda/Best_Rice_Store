package com.example.bestricestore.data

import com.google.firebase.firestore.DocumentSnapshot

class Feedback {
    var id: String? = null
    var title: String? = null
    var date: String? = null
    var purpose: String? = null
    var imageUrl: String? = null
    var userId: String? = null
    var userPhone: String? = null
    var userName: String? = null
    var feedbackStatus: String? = null

    constructor() {}
    constructor(
        title: String?,
        date: String?,
        purpose: String?,
        image: String?,
        userId: String?,
        userPhone: String?,
        userName: String?,
        feedbackStatus: String?
    ) {
        this.title = title
        this.date = date
        this.purpose = purpose
        imageUrl = image
        this.userId = userId
        this.userPhone = userPhone
        this.userName = userName
        this.feedbackStatus = feedbackStatus
    }

    constructor(
        id: String?,
        title: String?,
        date: String?,
        purpose: String?,
        imageUrl: String?,
        userId: String?,
        userPhone: String?,
        userName: String?,
        feedbackStatus: String?
    ) {
        this.id = id
        this.title = title
        this.date = date
        this.purpose = purpose
        this.imageUrl = imageUrl
        this.userId = userId
        this.userPhone = userPhone
        this.userName = userName
        this.feedbackStatus = feedbackStatus
    }

    companion object {
        fun getFeedbackFromFirestore(document: DocumentSnapshot): Feedback {
            val data = document.data
            val id = document.id
            val title = data!!["title"].toString()
            val userPhone = data["userPhone"].toString()
            val purpose = data["purpose"].toString()
            val userId = data["userId"].toString()
            val date = data["date"].toString()
            val imageUrl = data["imageUrl"].toString()
            val userName = data["userName"].toString()
            val feedbackStatus = data["feedbackStatus"].toString()
            return Feedback(
                id,
                title,
                date,
                purpose,
                imageUrl,
                userId,
                userPhone,
                userName,
                feedbackStatus
            )
        }
    }
}