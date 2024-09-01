package com.example.minidelivery.api

import com.example.minidelivery.data.RequestData
import com.example.minidelivery.data.ResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/orders")
    fun postData(@Body requestData: RequestData): Call<ResponseData>
}