package com.example.bestricestore.data

import com.google.firebase.firestore.DocumentSnapshot

class CartItem {
    var id: String? = null
    var userPhonenumber: String? = null
    var imageUrl: String? = null
    var type: String? = null
    var status: String? = null
    var deliverername: String? = null
    var delivererid: String? = null
    var deliPhone: String? = null
    var userEmail: String? = null
    var name: String? = null
    var username: String? = null
    var useraddress: String? = null
    var note: String? = null
    var userid: String? = null
    var time: String? = null
    var cost = 0
    var quantity = 0
    var userToken : String? = null
//    var totalLike : Int? = 0
//    var totalSell : Int? = 0

    constructor()
    constructor(
        id: String?,
        type: String?,
        name: String?,
        phone: String?,
        cost: Int,
        quantity: Int,
        userid: String?,
        username: String?,
        useraddress: String?,
        note: String?,
        status: String?,
        deliverername: String?,
        delivererid: String?,
        deliPhone: String?,
        time: String?,
        imageUrl: String?,
        userEmail: String?,
        userToken: String?,
//        totalLike : Int?,
//        totalSell : Int?
    ) {
        this.id = id
        this.time = time
        userPhonenumber = phone
        this.type = type
        this.name = name
        this.cost = cost
        this.quantity = quantity
        this.username = username
        this.userid = userid
        this.useraddress = useraddress
        this.note = note
        this.status = status
        this.deliverername = deliverername
        this.delivererid = delivererid
        this.deliPhone = deliPhone
        this.imageUrl = imageUrl
        this.userEmail = userEmail
        this.userToken = userToken
//        this.totalLike = totalLike
//        this.totalSell = totalSell
    }

    constructor(
        type: String?,
        name: String?,
        phone: String?,
        cost: Int,
        quantity: Int,
        userid: String?,
        username: String?,
        useraddress: String?,
        note: String?,
        status: String?,
        deliverername: String?,
        delivererid: String?,
        deliPhone: String?,
        time: String?,
        imageUrl: String?,
        userEmail: String?,
        userToken: String?,
//        totalLike : Int?,
//        totalSell : Int?,
    ) {
        this.time = time
        userPhonenumber = phone
        this.type = type
        this.name = name
        this.cost = cost
        this.quantity = quantity
        this.username = username
        this.userid = userid
        this.useraddress = useraddress
        this.note = note
        this.status = status
        this.deliverername = deliverername
        this.delivererid = delivererid
        this.deliPhone = deliPhone
        this.imageUrl = imageUrl
        this.userEmail = userEmail
        this.userToken = userToken
//        this.totalLike = totalLike
//        this.totalSell = totalSell
    }

    companion object {
        //    public Map<String, Object> getCartWithoutId() {
        //        Map<String, Object> bMap = new HashMap<>();// tao ra 1 map
        //        bMap.put("type", this.type); // lay title
        //        bMap.put("name", this.name);
        //        bMap.put("cost", this.cost);
        //        bMap.put("quantity",this.quantity);
        //        bMap.put("userid",this.userid);
        //        bMap.put("username",this.username);
        //        bMap.put("useraddress",this.useraddress);
        //        bMap.put("note",this.note);
        //        bMap.put("status",this.status);
        //        bMap.put("deliverername",this.deliverername);
        //        bMap.put("delivereid", this.delivererid);
        //        bMap.put("userPhonenumber",this.userPhonenumber);
        //        bMap.put("time", this.time);
        //        bMap.put("deliPhone", this.deliPhone);
        //        bMap.put("imageUrl", this.imageUrl);
        //        return bMap;
        //    }
        fun getCartFromFirestore(document: DocumentSnapshot): CartItem {
            return if (document.exists()) {
                val data = document.data
                val id = document.id
                val type = data!!["type"].toString()
                val name = data["name"].toString()
                val cost = data["cost"].toString().toInt()
                val quantity = data["quantity"].toString().toInt()
                val username = data["username"].toString()
                val useraddress = data["useraddress"].toString()
                val note = data["note"].toString()
                val status = data["status"].toString()
                val userid = data["userid"].toString()
                val deliverername = data["deliverername"].toString()
                val delivererid = data["delivererid"].toString()
                val userPhonenumber = data["userPhonenumber"].toString()
                val deliPhone = data["deliPhone"].toString()
                val time = data["time"].toString()
                val imageUrl = data["imageUrl"].toString()
                val userEmail = data["userEmail"].toString()
                val userToken = data["userToken"].toString()

                CartItem(
                    id, type, name, userPhonenumber, cost, quantity, userid, username, useraddress,
                    note, status, deliverername, delivererid, deliPhone, time, imageUrl, userEmail, userToken
                )
            } else {
                CartItem()
            }
        }
    }
}