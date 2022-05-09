package com.example.fatchcurrentlocation.DataClasses

data class Alerts(
    val action: String,
    val alert_id: Int,
    val alert_text: String,
    val alerted_user_id: Int,
    val User: User,
    val event_date:Long,
    val view_date:Long,
    val content_id:Int
)
