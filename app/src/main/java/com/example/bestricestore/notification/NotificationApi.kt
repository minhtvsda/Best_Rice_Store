package com.example.bestricestore.notification

import com.example.bestricestore.data.Constants.CONTENT_TYPE
import com.example.bestricestore.data.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key= $SERVER_KEY", "Content-Type:$CONTENT_TYPE")        //override authorization of request because we want to pass server key in the request headers.
    @POST("fcm/send")   // the part after const BASE URL
    suspend fun postNotification(
        @Body notification: PushNotification
    ) : Response<ResponseBody>
}