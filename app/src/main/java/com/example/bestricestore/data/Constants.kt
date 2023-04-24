package com.example.bestricestore.data

object Constants {
    const val EMPTY_STRING = ""
    const val FS_FOOD_SET = "foodset"
    const val FS_FOOD_CART = "foodcart"
    const val NEW_ID = ""
    const val FIRE_STORE = "firestore"
    const val ROLE_CUSTOMER = "customer"
    const val ROLE_DELIVERER = "deliverer"
    const val ROLE_ADMIN = "admin"
    const val FS_USER = "users"
    const val REQUEST_CODE = 2000
    const val STATUS_WAITING = "1.Waiting for deliverer's acceptance"
    const val STATUS_CANCEL = "0.Cancel the food"
    const val STATUS_DELIVERING = "2.Food is being delivered!"
    const val STATUS_DONE = "3.Done!"
    const val FS_STORAGE_URL = "gs://appricestore.appspot.com"
    const val FS_NEW_SET = "newset"
    const val STATUS_ADMIN_CANCEL = "0.The bill cancels cause sold out! Sorry about that."
    const val STATUS_WAITING_RESTAURANT = "1. Waiting for the restaurant's accept!"
    const val FS_DELI_LINK = "delilink"
    const val DELIVERER_REGISTER_WAITING = "deliverer register"
    const val FS_ROLE_BAN = "banned"
    const val DELIVERER_REGISTER_DECLINED = "declined deliverer register"
    const val TIME = 2000000000000L
    const val FS_FEEDBACK = "feedback"
    const val FS_RESPOND = "respond"
    const val AVAILABLE_CURRENT_STATUS = "Available"
    const val OUT_OF_STOCK_CURRENT_STATUS = "Out of stock"
    const val FEEDBACK_NOT_RESPONDED = "not responded"
    const val FEEDBACK_ALREADY_RESPONDED = "already responded"
    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAACHQZPEw:APA91bF-ACfeyMrQnAZ9ENfMNeEQ8PleidkMllth3tsx5ATzHT04fyQE3nenZ5-1DJW14WMKNr--KXO00fssgwyXnEmDmEoLOEiRjFaSsIabMOKu8ObEiMvZC7Rzxh2fN0E7SQmRZVBG"
    const val CONTENT_TYPE = "application/json"
    const val TOPIC_ORDER = "orders"
    //when we send notification, that notification will be sent only devices that subscribe to that specific topic
    const val CHANNEL_ID = "order channel"
    const val PUSH_NOTIFY_ORDER_DELIVERING = "Your order is delivering!!!"
    const val PUSH_NOTIFY_ORDER_DONE = "Your order has already done! Good meal"
    const val PUSH_NOTIFY_ADMIN_WAITING = "Your order is cooking now! Please wait a minutes!"
    const val PUSH_NOTIFY_ADMIN_CANCEL = "Your order is canceled because it is out of stock! We are so sorry."

}