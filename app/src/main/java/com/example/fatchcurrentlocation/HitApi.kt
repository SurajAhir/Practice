package com.example.fatchcurrentlocation

import com.example.fatchcurrentlocation.DataClasses.Node
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import retrofit2.Call
import retrofit2.http.*

interface HitApi {
    @POST("auth/")
    @FormUrlEncoded
    fun getResponseDataForLogin(
        @Header("XF-Api-Key") key: String,
        @Field("login") login: String,
        @Field("password") password: String,
    ): Call<ResponseDataClass>

    @GET("nodes/")
    fun getNodesResponse(@Header("XF-Api-Key") key: String): Call<Node>

    @GET("forums/{id}/threads/")
    fun getForumsResponse(
        @Header("XF-Api-Key") key: String,
        @Path("id") id: Int,
        @Query("page") page: Int,
    ): Call<ResponseThread>

    @GET("threads/{id}/posts/")
    fun getPostsOfThreadsResonse(
        @Header("XF-Api-Key") key: String,
        @Path("id") id: Int,
        @Query("page") page: Int,
    ): Call<ResponseThread>
}