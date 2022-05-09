package com.example.fatchcurrentlocation.DataClasses

data class Posts(
    val message: String,
    val message_parsed: String,
    val position: Int,
    val post_date: Long,
    val thread_id: Int,
    val user_id: Int,
    val post_id: Int,
    val is_reacted_to: Boolean,
    val visitor_reaction_id: Int,
    val reaction_score: Int,
    val User:User,
    val Attachments:List<AttachmentDataResponse>,
    val conversation_id:Int,
    val message_id:Int,
val attach_count:Int,
    val is_staff:Boolean,
    val is_admin:Boolean,
    val is_super_admin:Boolean,
    val Thread:Threads
)
