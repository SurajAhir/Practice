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
        @Query("direction") direction: String,
        @Query("order") order: String,
    ): Call<ResponseThread>

    @GET("threads/{id}/posts/")
    fun getPostsOfThreadsResonse(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") id: Int,
        @Query("page") page: Int,
        @Query("direction") direction: String,
        @Query("order") order: String,
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
        @Part("key") attachmentKey: RequestBody,
    ): Call<ResponseThread>

    @POST("posts/{id}/react")
    @FormUrlEncoded
    fun getReaponseOfReact(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") post_id: Int,
        @Field("reaction_id") reaction_id: Int,
    ): Call<Map<String, Any>>

    @GET("users/{id}/profile-posts")
    fun getReaponseOfProfilePosts(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") user_id: Int,
    ): Call<ResponseDataClass>

    @FormUrlEncoded
    @POST("profile-posts/")
    fun getReaponseOfProfilePostsOfMessages(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("user_id") user_id: Int,
        @Field("message") message: String,
    ): Call<ResponseDataClass>

    @POST("profile-posts/{id}/react")
    @FormUrlEncoded
    fun getReaponseOfProfilePostsReact(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") post_id: Int,
        @Field("reaction_id") reaction_id: Int,
    ): Call<Map<String, Any>>

    @POST("me/avatar")
    @Multipart
    fun updateUserProfileImage(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Part avatar: MultipartBody.Part,
    ): Call<Map<String, Boolean>>

    @POST("me/email")
    @FormUrlEncoded
    fun updateUserEmailId(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("current_password") currentPassword: String,
        @Field("email") email: String,
    ): Call<Map<String, Boolean>>

    @POST("me/password")
    @FormUrlEncoded
    fun updateUserPassword(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("current_password") currentPassword: String,
        @Field("new_password") newPassword: String,
    ): Call<Map<String, Boolean>>

    @GET("me/")
    fun getUsersProfileResponse(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
    ): Call<ResponseDataClass>

    @DELETE("profile-posts/{id}/")
    fun deleteSpecificPost(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") post_id: Int,
    ): Call<Map<String, Boolean>>

    @POST("me/")
    @FormUrlEncoded
    fun updateProfile(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("option[receive_admin_email]") isReceiveNewsAndUpdate: Boolean,
        @Field("option[show_dob_date]") isShowDateAndMonth: Boolean,
        @Field("option[show_dob_year]") isShowYearOfBirth: Boolean,
        @Field("profile[location]") location: String,
        @Field("profile[website]") website: String,
        @Field("profile[about]") about: String,
        @Field("dob[day]") day: Int,
        @Field("dob[month]") month: Int,
        @Field("dob[year]") year: Int,
    ): Call<Map<String, Boolean>>

    @POST("me/")
    @FormUrlEncoded
    fun updatePrivacy(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("visible") visible: Boolean,
        @Field("activity_visible") activity_visible: Boolean,
        @Field("option[receive_admin_email]") isReceiveNewsAndUpdate: Boolean,
        @Field("option[show_dob_date]") isShowDateAndMonth: Boolean,
        @Field("option[show_dob_year]") isShowYearOfBirth: Boolean,
        @Field("privacy[allow_view_profile]") allowViewProfile: String,
        @Field("privacy[allow_post_profile]") allow_post_profile: String,
        @Field("privacy[allow_receive_news_feed]") allow_receive_news_feed: String,
        @Field("privacy[allow_send_personal_conversation]") allow_send_personal_conversation: String,
        @Field("privacy[allow_view_identities]") allow_view_identities: String,
    ): Call<Map<String, Boolean>>

    @GET("threads/")
    fun findAllThreadsBy(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Query("starter_id") user_Id: Int,
        @Query("direction") direction: String,
        @Query("order") order: String,
    ): Call<ResponseThread>

    @POST("threads/")
    @FormUrlEncoded
    fun postThread(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("node_id") node_id: Int,
        @Field("title") title: String,
        @Field("message") message: String,
    ): Call<Map<String, Boolean>>

    @GET("alerts/")
    fun getAlerts(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int
    ):Call<ResponseThread>
}