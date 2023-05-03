package com.tweb.project30.util

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object RetrofitHelper {
    var BASE_URL = "http://192.168.10.107:8080/Project30/"

    fun getInstance(): Retrofit {
       return Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }
}