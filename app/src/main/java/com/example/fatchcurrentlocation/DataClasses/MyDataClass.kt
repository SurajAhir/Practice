package com.example.fatchcurrentlocation.DataClasses

import android.content.Context
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.fatchcurrentlocation.AdaptersClasses.ShowPostsOfThreadsAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.UserProfileAdapter
import java.util.*

object MyDataClass {
    lateinit var responseDataClass: ResponseDataClass
    var path: Int = 0
    var page: Int = 1
    lateinit var paginationForShowDetails: Pagination
    lateinit var paginationForPostsOfThreads: Pagination
    lateinit var title: String
    var myUserId: Int = 0
    lateinit var homeNestedScrollView: NestedScrollView
    lateinit var homeFragmentContainerView: FragmentContainerView
    var isFragmentAttached: Boolean = false
    var threadId: Int = 0
    var pageForPosts: Int = 1
    lateinit var getTransaction: () -> FragmentTransaction
    var countFrag: Int = 0
    lateinit var onBack: () -> Unit
    lateinit var reactionDialog: (Int, Any, Int) -> DialogFragment

    lateinit var datePick:(LinkedList<Int>)->Unit
    var reactionType: Int = 0
    var api_key: String = "4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4"
}