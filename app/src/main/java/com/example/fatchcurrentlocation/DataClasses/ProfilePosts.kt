package com.example.fatchcurrentlocation.DataClasses

data class ProfilePosts(
    val is_reacted_to: Boolean,
    val message: String,
    val message_parsed: String,
    val post_date: Long,
    val profile_user_id: Int,
    val reaction_score: Int,
    val User: User,
    val user_title: String,
    val username: String,
    val profile_post_id: Int,
    val visitor_reaction_id: Int,
    val LatestComments:List<LatestComments>,
    val Attachments:List<AttachmentDataResponse>,
    val attach_count:Int,
)
