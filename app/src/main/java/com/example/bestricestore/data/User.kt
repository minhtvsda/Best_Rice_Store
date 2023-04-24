package com.example.bestricestore.data

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.DocumentSnapshot
@IgnoreExtraProperties
class User {
    var username: String? = null
    var phoneNumber: String? = null
    var id: String? = null
    var dob: String? = null
    var gender: String? = null
    var email: String? = null
    var address: String? = null
    var roles: String? = null
    var tokenId : String? = null

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(
        id: String?,
        username: String?,
        email: String?,
        address: String?,
        roles: String?,
        phoneNumber: String?,
        dob: String?,
        gender: String?,
        tokenId: String?
    ) {
        this.phoneNumber = phoneNumber
        this.username = username
        this.email = email
        this.address = address
        this.roles = roles
        this.id = id
        this.dob = dob
        this.gender = gender
        this.tokenId = tokenId
    }

    constructor(
        username: String?,
        email: String?,
        address: String?,
        roles: String?,
        phoneNumber: String?,
        dob: String?,
        gender: String?,
        tokenId: String?
    ) {
        this.phoneNumber = phoneNumber
        this.username = username
        this.email = email
        this.address = address
        this.roles = roles
        this.dob = dob
        this.gender = gender
        this.tokenId = tokenId

    }

    constructor(
        username: String?,
        email: String?,
        address: String?,
        roles: String?,
        phoneNumber: String?,
        tokenId: String?

    ) {
        this.phoneNumber = phoneNumber
        this.username = username
        this.email = email
        this.address = address
        this.roles = roles
        this.tokenId = tokenId

    }

    constructor(
        id: String?,
        username: String?,
        email: String?,
        address: String?,
        roles: String?,
        phoneNumber: String?,
        tokenId: String?
    ) {
        this.phoneNumber = phoneNumber
        this.username = username
        this.email = email
        this.address = address
        this.roles = roles
        this.id = id
        this.tokenId = tokenId

    }

    companion object {
        fun getUserFromFireStore(document: DocumentSnapshot): User {
            val data = document.data
            val username = data!!["username"].toString()
            val email = data["email"].toString()
            val address = data["address"].toString()
            val roles = data["roles"].toString()
            val phoneNumber = data["phoneNumber"].toString()
            val dob = data["dob"].toString()
            val gender = data["gender"].toString()
            val tokenId = data["tokenId"].toString()

            return User(username, email, address, roles, phoneNumber, dob, gender,tokenId)
        }

        fun getUserIdFromFireStore(document: DocumentSnapshot): User {
            val data = document.data
            val id = document.id
            val username = data!!["username"].toString()
            val email = data["email"].toString()
            val address = data["address"].toString()
            val roles = data["roles"].toString()
            val phoneNumber = data["phoneNumber"].toString()
            val dob = data["dob"].toString()
            val gender = data["gender"].toString()
            val tokenId = data["tokenId"].toString()
            return User(id, username, email, address, roles, phoneNumber, dob, gender,tokenId)
        }
    }
}