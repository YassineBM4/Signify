package com.example.visionsignapp.ui.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://192.168.137.1:3000/"

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAuthApi(): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}