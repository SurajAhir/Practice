package com.example.fatchcurrentlocation.services

import com.example.fatchcurrentlocation.DataClasses.Node
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
        @Query("direction") direction: String,
        @Query("order") order: String,
    ): Call<ResponseThread>

    @GET("forums/{id}/threads/")
    fun getForumsResponseByFilter(
        @Header("XF-Api-Key") key: String,
        @Path("id") id: Int,
        @Query("direction") direction: String,
        @Query("order") order: String,
        @Query("starter_id") starter_id: Int,
        @Query("last_days") last_days: String,
    ): Call<ResponseThread>

    @GET("threads/{id}/posts/")
    fun getPostsOfThreadsResonse(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") thread_id: Int,
        @Query("page") page: Int,
        @Query("direction") direction: String,
        @Query("order") order: String,
    ): Call<ResponseThread>

    @POST("posts/")
    @FormUrlEncoded
    fun getResponseOfComments(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("thread_id") thread_id: Int,
        @Field("message") message: String,
        @Field("attachment_key") attachment_key: String,
    ): Call<ResponseThread>

    @DELETE("posts/{id}/")
    fun deletePost(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") post_id: Int,
    ): Call<Map<String, Boolean>>

    @POST("attachments/new-key")
    @FormUrlEncoded
    fun generateAttachmentKey(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("context[thread_id]") thread_id: Int,
        @Field("type") type: String,
    ): Call<Map<String, String>>

    @POST("attachments/new-key")
    @FormUrlEncoded
    fun generateAttachmentKeyForPostThread(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("context[node_id]") node_id: Int,
        @Field("type") type: String,
    ): Call<Map<String, String>>

    @POST("attachments/new-key")
    @FormUrlEncoded
    fun generateAttachmentKeyForUserProfile(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("context[profile_user_id]") profile_user_id: Int,
        @Field("type") type: String,
    ): Call<Map<String, String>>

    @POST("attachments/new-key")
    @FormUrlEncoded
    fun generateAttachmentKeyForUserProfileOfComments(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("context[profile_post_id]") profile_post_id: Int,
        @Field("type") type: String,
    ): Call<Map<String, String>>

    @POST("attachments/new-key")
    @FormUrlEncoded
    fun generateAttachmentKeyForConversation(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("type") type: String,
    ): Call<Map<String, String>>

    @POST("attachments/new-key")
    @FormUrlEncoded
    fun generateAttachmentKeyForPostReplyForConversation(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("context[conversation_id]") message_id: Int,
        @Field("type") type: String,
    ): Call<Map<String, String>>

    @Multipart
    @POST("attachments/")
    fun postAttachmentFile(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Part attachment: MultipartBody.Part,
        @Part("key") attachmentKey: RequestBody,
    ): Call<ResponseThread>

    @DELETE("attachments/{id}/")
    fun deleteAttachment(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") attachmentId: Int,
    ): Call<Map<String, Boolean>>

    @POST("posts/{id}/react")
    @FormUrlEncoded
    fun getReaponseOfReact(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") post_id: Int,
        @Field("reaction_id") reaction_id: Int,
    ): Call<Map<String, Any>>

    @FormUrlEncoded
    @POST("profile-post-comments/")
    fun getResponseOfProfilePostsOfComments(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("profile_post_id") profile_post_id: Int,
        @Field("message") message: String,
        @Field("attachment_key") attachment_key: String,
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
        @Field("attachment_key") attachment_key: String,
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
        @Field("attachment_key") attachment_key: String,
    ): Call<Map<String, Boolean>>

    @GET("alerts/")
    fun getAlerts(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Query("page") page: Int,
        @Query("direction") direction: String,
        @Query("order") order: String,
    ): Call<ResponseThread>

    @GET("conversations/")
    fun getConversations(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Query("page") page: Int,
        @Query("direction") direction: String,
        @Query("order") order: String,
    ): Call<ResponseThread>

    @GET("users/find-name")
    fun findUserName(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Query("username") username: String,
    ): Call<ResponseThread>

    @POST("conversations/")
    @FormUrlEncoded
    fun startNewConversation(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("recipient_ids[]") recipient_ids: List<Int>,
        @Field("title") title: String,
        @Field("message") message: String,
        @Field("attachment_key") attachment_key: String,
        @Field("conversation_open") conversation_open: Boolean,
        @Field("open_invite") open_invite: Boolean,
    ): Call<ResponseThread>

    @GET("conversations/{id}/messages")
    fun getConversationMessages(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") conversation_id: Int,
        @Query("page") page: Int,
    ): Call<ResponseDataClass>

    @GET("attachments/{id}/data")
    fun getAttachments(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") attachment_id: Int,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("conversation-messages/")
    fun postConversationReply(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Field("conversation_id") conversation_id: Int,
        @Field("message") message: String,
        @Field("attachment_key") attachment_key: String,
    ): Call<ResponseThread>

    @POST("conversations/{id}/invite")
    @FormUrlEncoded
    fun inviteMembers(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") conversation_id: Int,
        @Field("recipient_ids[]") recipient_ids: List<Int>,
    ): Call<Map<String, Boolean>>

    @GET("conversations/{id}/")
    fun getConversationsByConversId(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") conversation_id: Int,
    ): Call<ResponseThread>

    @GET("posts/{id}/")
    fun getPostOfAlerts(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id") content_id: Int,
    ): Call<ResponseThread>

    @GET("threads/")
    fun getLatestPosts(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
    ):Call<ResponseThread>
//    @Query("page") page: Int,
//    @Query("order") order: String,
//    @Query("direction") direction: String,
    @GET("users/find-email")
    fun findUserEmail(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Query("email") email:String
    ):Call<ResponseDataClass>

    @GET("alerts/")
    fun getUnViewedAlerts(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Query("unread") unread: Boolean,
    ): Call<ResponseDataClass>

    @GET("conversations/")
    fun getUnViewedConversations(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Query("unread") unread: Boolean,
    ): Call<ResponseDataClass>
    @POST("conversations/{id}/mark-read")
    fun markReadConversation(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id")conversation_id: Int
    ):Call<Map<String,Boolean>>
    @POST("alerts/{id}/mark")
    @FormUrlEncoded
    fun markReadAlerts(
        @Header("XF-Api-Key") key: String,
        @Header("XF-Api-User") userId: Int,
        @Path("id")conversation_id: Int,
        @Field("read")read:Boolean
    ):Call<Map<String,Boolean>>

    @POST("api.php")
    @FormUrlEncoded
    fun getUserEmailFromTwitterId(
        @Field("provider_id")provider_id:Long
    ):Call<Map<String,Any>>
}