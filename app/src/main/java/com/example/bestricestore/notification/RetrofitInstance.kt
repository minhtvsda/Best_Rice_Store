package com.example.bestricestore.notification

import com.example.bestricestore.data.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        private val retrofit by lazy { //lazy mean variables only init if we need it
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        //create our actual API that can get from the instance

        val api by lazy {
            retrofit.create(NotificationApi ::class.java)

        }
    }
}