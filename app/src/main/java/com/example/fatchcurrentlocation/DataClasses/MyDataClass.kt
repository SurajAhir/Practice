package com.example.fatchcurrentlocation.DataClasses

import android.content.Context
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction

object MyDataClass {
    lateinit var responseDataClass: ResponseDataClass
    var path: Int = 0
    var page: Int = 1
    lateinit var pagination: Pagination
    lateinit var title: String
    lateinit var  homeNestedScrollView:NestedScrollView
    lateinit var homeFragmentContainerView:FragmentContainerView
     var threadId:Int=0
    var pageForPosts:Int=1
    lateinit var getTransaction: ()-> FragmentTransaction
    var countFrag:Int=0
}