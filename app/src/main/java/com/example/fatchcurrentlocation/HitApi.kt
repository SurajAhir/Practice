package com.example.fatchcurrentlocation

import com.example.fatchcurrentlocation.DataClasses.Node
import com.example.fatchcurrentlocation.DataClasses.ReponseThread
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.DataClasses.Threads
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface HitApi{
    @POST("auth/")
    @FormUrlEncoded
    fun getResponseDataForLogin(@Header("XF-Api-Key") key:String,@Field("login")login:String,@Field("password")password:String
    ): Call<ResponseDataClass>

    @GET("nodes/")
    fun getNodesResponse(@Header("XF-Api-Key") key:String):Call<Node>

    @GET("forums/{id}/threads/")
    fun getForumsResponse(@Header("XF-Api-Key") key:String,@Path("id")id:Int,@Query("page")page:Int):Call<ReponseThread>
}