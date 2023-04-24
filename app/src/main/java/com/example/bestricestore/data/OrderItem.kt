package com.example.bestricestore.data

data class OrderItem(var cartList : List<CartItem>,) {
    companion object{
        fun listCartToMap (cartList: List<CartItem>)  : List<MutableMap<String, Any?>> {
            val list = mutableListOf<MutableMap<String, Any? >>()
            cartList.forEach { cart ->
                val bMap: MutableMap<String, Any?> = HashMap() // tao ra 1 map
                bMap["type"] = cart.id
                bMap["userPhonenumber"] = cart.userPhonenumber
                bMap["imageUrl"] = cart.imageUrl
                bMap["type"] = cart.type
                bMap["status"] = cart.status
                bMap["deliverername"] = cart.deliverername
                bMap["delivererid"] = cart.delivererid
                bMap["deliPhone"] = cart.deliPhone
                bMap["userEmail"] = cart.userEmail
                bMap["name"] = cart.name
                bMap["username"] = cart.username
                bMap["useraddress"] = cart.useraddress
                bMap["note"] = cart.note
                bMap["userid"] = cart.userid
                bMap["time"] = cart.time
                bMap["cost"] = cart.cost
                bMap["quantity"] = cart.quantity

                list.add(bMap)
            }
            return list
        }
    }
}