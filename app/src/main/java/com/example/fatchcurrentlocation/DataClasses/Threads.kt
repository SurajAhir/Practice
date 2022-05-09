package com.example.fatchcurrentlocation.DataClasses

data class Threads(
    val last_post_username: String,
    val title: String,
    val reply_count: Int,
    val post_date: Long,
    val User: User,
    val thread_id: Int,
    val Forum:ForumUser,
    val last_post_date:Long,
    val username:String
)
