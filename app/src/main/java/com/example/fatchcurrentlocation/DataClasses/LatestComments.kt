package com.example.fatchcurrentlocation.DataClasses

data class LatestComments(
    val comment_date: Long,
    val is_reacted_to: Boolean,
    val message: String,
    val message_parsed: String,
    val profile_post_comment_id: Int,
    val profile_post_id: Int,
    val reaction_score: Int,
    val User: User,
)
