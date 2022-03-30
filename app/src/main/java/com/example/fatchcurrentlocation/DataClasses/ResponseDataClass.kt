package com.example.fatchcurrentlocation.DataClasses

import java.io.Serializable
data class ResponseDataClass(val success:Boolean,val user:User,val errors:List<UserLoginError>):Serializable