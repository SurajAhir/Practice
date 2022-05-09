package com.example.fatchcurrentlocation.DataClasses

import com.google.gson.JsonObject

data class Conversations(
    val conversation_id: Int,
    val first_message_id: Int,
    val last_message_date: Long,
    val last_message_id: Int,
    val last_message_user_id: Int,
    val recipient_count:Int,
//    val recipients:Map<String,String>,
    val recipients:JsonObject,
    val start_date:Long,
    val reply_count:Int,
    val Starter:User,
    val title:String,
    val user_id:Int,
    val username:String

)
