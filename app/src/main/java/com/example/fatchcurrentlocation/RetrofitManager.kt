package com.example.fatchcurrentlocation

import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.POST

object RetrofitManager {
 var retrofit: Retrofit?=null
    fun getRetrofit1():Retrofit{
        if(retrofit==null){
            retrofit=Retrofit.Builder().baseUrl("https://technofino.in/community/api/").addConverterFactory(
                GsonConverterFactory.create()).build()
        }
        return retrofit!!
    }
}
