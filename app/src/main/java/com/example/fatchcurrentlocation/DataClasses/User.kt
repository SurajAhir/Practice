package com.example.fatchcurrentlocation.DataClasses

import java.io.Serializable

data class User(
    var about: String,
    val register_date: Long,
    val timezone: String,
    val user_group_id: Int,
    val user_id: Int,
    val username: String,
    val view_url: String,
    val message_count: Int,
    val reaction_score: Int,
    val trophy_points: Int,
    val user_title: String,
    val last_activity: Long,
    val avatar_urls:Avatar_Urls,
    var email:String,
    var show_dob_date:Boolean,
    var show_dob_year:Boolean,
    var receive_admin_email:Boolean,
    var website:String,
    var location:String,
    var activity_visible:Boolean,
    var allow_post_profile:String,
    var allow_receive_news_feed:String,
    var allow_send_personal_conversation:String,
    var allow_view_identities:String,
    val allow_view_profile:String,
    var visible:Boolean,
    val can_converse:Boolean,
    val can_post_profile:Boolean,
    val can_view_profile:Boolean,
    val can_view_profile_posts:Boolean
) : Serializable