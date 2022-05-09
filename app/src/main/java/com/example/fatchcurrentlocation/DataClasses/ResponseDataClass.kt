package com.example.fatchcurrentlocation.DataClasses

import java.io.Serializable

data class ResponseDataClass(
    val profile_posts: List<ProfilePosts>,
    val pagination: Pagination,
    val success: Boolean,
    val user: User,
    val errors: List<UserLoginError>,
    var me:User,
    val messages:List<Posts>
) : Serializable