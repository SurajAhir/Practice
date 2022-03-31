package com.example.fatchcurrentlocation.DataClasses

data class ResponseThread(val threads:List<Threads>,val posts: List<Posts>, val pagination: Pagination, val sticky:List<Threads>)