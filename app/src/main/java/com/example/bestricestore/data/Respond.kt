package com.example.bestricestore.data

import com.google.firebase.firestore.DocumentSnapshot

class Respond {
    var id: String? = null
    var feedbackUrl: String? = null
    var userId: String? = null
    var userPhone: String? = null
    var userName: String? = null
    var date: String? = null
    var title: String? = null
    var purpose: String? = null
    var respondTitle: String? = null
    var respondAnswer: String? = null
    var respondUrl: String? = null

    constructor()
    constructor(
        id: String?,
        feedbackUrl: String?,
        userId: String?,
        userPhone: String?,
        userName: String?,
        date: String?,
        title: String?,
        purpose: String?,
        respondTitle: String?,
        respondAnswer: String?,
        respondUrl: String?
    ) {
        this.id = id
        this.feedbackUrl = feedbackUrl
        this.userId = userId
        this.userPhone = userPhone
        this.userName = userName
        this.date = date
        this.title = title
        this.purpose = purpose
        this.respondTitle = respondTitle
        this.respondAnswer = respondAnswer
        this.respondUrl = respondUrl
    }

    constructor(
        feedbackUrl: String?,
        userId: String?,
        userPhone: String?,
        userName: String?,
        date: String?,
        title: String?,
        purpose: String?,
        respondTitle: String?,
        respondAnswer: String?,
        respondUrl: String?
    ) {
        this.feedbackUrl = feedbackUrl
        this.userId = userId
        this.userPhone = userPhone
        this.userName = userName
        this.date = date
        this.title = title
        this.purpose = purpose
        this.respondTitle = respondTitle
        this.respondAnswer = respondAnswer
        this.respondUrl = respondUrl
    }

    companion object {
        fun getRespondFromFirestore(document: DocumentSnapshot): Respond {
            return if (document.exists()) {
                val data = document.data
                val id = document.id
                val feedbackUrl = data!!["feedbackUrl"].toString()
                val userId = data["userId"].toString()
                val userPhone = data["userPhone"].toString()
                val userName = data["userName"].toString()
                val date = data["date"].toString()
                val title = data["title"].toString()
                val purpose = data["purpose"].toString()
                val respondTitle = data["respondTitle"].toString()
                val respondAnswer = data["respondAnswer"].toString()
                val respondUrl = data["respondUrl"].toString()
                Respond(
                    id,
                    feedbackUrl,
                    userId,
                    userPhone,
                    userName,
                    date,
                    title,
                    purpose,
                    respondTitle,
                    respondAnswer,
                    respondUrl
                )
            } else {
                Respond()
            }
        }
    }
}