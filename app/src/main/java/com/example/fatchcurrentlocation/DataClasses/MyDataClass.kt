package com.example.fatchcurrentlocation.DataClasses

import android.graphics.Bitmap
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import java.util.*
import kotlin.collections.ArrayList

object MyDataClass {
     var responseDataClass: ResponseDataClass?=null
    var isJumpedToImage=false
    var JumpedToImagePageNum=0
    var JumpedToImageList:LinkedList<Posts> =LinkedList()
    var JumpToImagePosition=0
    var path: Int = 0
    var page: Int = 1
    lateinit var paginationForShowDetails: Pagination
    lateinit var paginationForPostsOfThreads: Pagination
    lateinit var title: String
    var myUserId: Int = 0
    lateinit var homeNestedScrollView: NestedScrollView
    lateinit var homeFragmentContainerView: FragmentContainerView
    var isFragmentAttached: Boolean = false
    lateinit var isClickedList: java.util.ArrayList<Boolean>
    var isClickedValueForBtn=false
    var isEnteredInShowDetails=false
    var isGoProfile=false
    var isGoConversation=false
    var isGoNotification=false
    var isGoForLatestPosts=false
    var isPostThread=false
    var threadId: Int = 0
    var pageForPosts: Int = 1
    lateinit var getTransaction: () -> FragmentTransaction
    lateinit var getTotalAlertsCount: ()->Unit
    lateinit var getTotalConversationsCount: ()->Unit
    var countFrag: Int = 0
    lateinit var onBack: () -> Unit
    lateinit var reactionDialog: (Int, Any, Int) -> DialogFragment
    var attachmentFileListItem: ArrayList<String> = ArrayList()
    var attachmentFileListAttachmentId: ArrayList<Int> = ArrayList()
    lateinit var datePick: (LinkedList<Int>) -> Unit
    lateinit var onReceiveData:(String)->Unit
    var reactionType: Int = 0
    lateinit var funAdapter: () -> Unit
//    var api_key: String = "4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4"
    var api_key: String = "p-rQmDsW41tBC768faylyvJcd8KOnp3g"
    var userFragmentImage:Any?=null
    var userFragmentRequestCode:Int=0
    var gridImage:Bitmap? =null

}