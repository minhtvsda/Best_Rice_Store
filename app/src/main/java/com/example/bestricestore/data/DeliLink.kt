package com.example.bestricestore.data

import com.google.firebase.firestore.DocumentSnapshot

class DeliLink {
    constructor()
    constructor(idUrl: String?, drivingUrl: String?, motorUrl: String?) {
        this.idUrl = idUrl
        this.drivingUrl = drivingUrl
        this.motorUrl = motorUrl
    }

    var idUrl: String? = null
    var drivingUrl: String? = null
    var motorUrl: String? = null

    companion object {
        fun getDeliLinkFromFireStore(document: DocumentSnapshot): DeliLink {
            val data = document.data
            val idUrl = data!!["idUrl"].toString()
            val drivingUrl = data["drivingUrl"].toString()
            val motorUrl = data["motorUrl"].toString()
            return DeliLink(idUrl, drivingUrl, motorUrl)
        }
    }
}