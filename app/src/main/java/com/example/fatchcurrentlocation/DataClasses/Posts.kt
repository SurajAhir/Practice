package com.example.fatchcurrentlocation.DataClasses

data class Posts(val message:String,val message_parsed:String,val position:Int,val post_date:Long
,val User:ForumUser,val thread_id:Int,val user_id:Int,val post_id:Int,val is_reacted_to:Boolean,val visitor_reaction_id:Int)
