package com.example.fatchcurrentlocation

import com.example.fatchcurrentlocation.DataClasses.AttachmentDataResponse
import com.example.fatchcurrentlocation.DataClasses.Node
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File
import java.lang.ref.ReferenceQueue


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
        @Header("XF-Api-User") userId: Int,
        @Path("id") id: Int,
        @Query("page") page: Int,
    ): Call<ResponseThread>

    @POST("posts/")
    @FormUrlEncoded
    fun getResponseOfComments(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("thread_id") thread_id: Int, @Field("message") message: String,
    ): Call<ResponseThread>

    @POST("attachments/new-key")
    @FormUrlEncoded
    fun generateAttachmentKey(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("context[thread_id]") thread_id: Int, @Field("type") type: String,
    ): Call<Map<String, String>>

    @Multipart
    @POST("attachments/")
    fun postAttachmentFile(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Part attachment: MultipartBody.Part,
        @Part("key") attachmentKey: RequestBody
    ): Call<ResponseObject>

    @POST("posts/{id}/react")
    @FormUrlEncoded
    fun getReaponseOfReact(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") post_id:Int,
        @Field("reaction_id")reaction_id:Int
    ): Call<Map<String,Any>>

}