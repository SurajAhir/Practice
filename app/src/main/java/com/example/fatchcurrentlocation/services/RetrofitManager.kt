package com.example.fatchcurrentlocation.services

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {
 var retrofit: Retrofit?=null
 var retrofitForUser: Retrofit?=null
    fun getRetrofit1():Retrofit{
        if(retrofit ==null){
            retrofit =Retrofit.Builder().client(OkHttpClient()).baseUrl("https://technofino.in/community/api/").addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().setLenient().create())).build()
        }
        return retrofit!!
    }

    @JvmName("getRetrofitForUser1")
    fun getRetrofitForUser():Retrofit{
        if(retrofitForUser ==null){
            retrofitForUser =Retrofit.Builder().client(OkHttpClient()).baseUrl("https://technofino.in/androidapi/").addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().setLenient().create())).build()
        }
        return retrofitForUser!!
    }
}
