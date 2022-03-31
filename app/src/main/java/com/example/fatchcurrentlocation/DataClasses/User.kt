package com.example.fatchcurrentlocation.DataClasses

import java.io.Serializable

data class User(val about:String,val register_date:Int,val timezone:String,val user_group_id:Int,val user_id:Int,val username:String,
                val view_url:String,val message_count:Int,val reaction_score:Int,val trophy_points:Int,val user_title:String):Serializable