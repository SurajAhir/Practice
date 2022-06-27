package com.example.fatchcurrentlocation.Fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.ConversationAdatper
import com.example.fatchcurrentlocation.DataClasses.Conversations
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentShowConversationsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ShowConversations : Fragment() {
var conversationList:LinkedList<Conversations> = LinkedList()
    lateinit var binding: FragmentShowConversationsBinding
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentShowConversationsBinding.inflate(layoutInflater, container, false)
       initializeData()
        fetchDataFromApi()
//        binding.showConversationsRecyclerView.setOnScrollChangeListener(object :View.OnScrollChangeListener{
//            override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, p3: Int, p4: Int) {
//                if (v != null) {
//                    if (scrollY == v.height - v.measuredHeight) {
//                        if (MyDataClass.paginationForShowDetails.last_page == MyDataClass.page) {
//                            binding.showConversationsProgressBar.visibility = View.GONE
//                        } else {
//                            MyDataClass.page++
//                            binding.showConversationsProgressBar.visibility = View.VISIBLE
//                            fetchDataFromApi(MyDataClass.page)
//                        }
//                    }
////                    else{
////                        binding.showConversationsProgressBar.visibility = View.GONE
////                    }
//                }
////                else{
////                    binding.showConversationsProgressBar.visibility = View.GONE
////                }
//            }
//        })
        binding.showConversationsNestedScrollView.setOnScrollChangeListener(object :NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int,
            ) {
                if(v!=null){
                    if(scrollY==v.getChildAt(0).measuredHeight-v.measuredHeight){
                        if(MyDataClass.paginationForShowDetails.last_page==MyDataClass.page){
                            binding.showConversationsProgressBar.visibility=View.GONE
                        }else{
                            MyDataClass.page++
                            binding.showConversationsProgressBar.visibility = View.VISIBLE
                            fetchDataFromApi(MyDataClass.page)
                        }
                    }
                }else{
                    binding.showConversationsProgressBar.visibility=View.GONE
                }
            }
        })
        binding.showConversationsStartNewConversationBtn.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                if (MyDataClass.responseDataClass!!.user.can_converse) {
                    return
                } else {
                    var transaction = MyDataClass.getTransaction()
                    if (transaction != null) {
                        MyDataClass.homeNestedScrollView.visibility = View.GONE
                        MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                        transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                            StartNewConversation())
                        transaction.addToBackStack(null).commit()
                    }
                }
            }
        })
        return binding.root
    }

    private fun initializeData() {
        MyDataClass.page = 1
        MyDataClass.JumpToImagePosition=0
        MyDataClass.JumpedToImageList.clear()
        MyDataClass.isJumpedToImage=false
        MyDataClass.JumpedToImagePageNum=0
        MyDataClass.isGoConversation=true

        MyDataClass.isGoNotification=false
        MyDataClass.isGoProfile=false
        MyDataClass.isGoForLatestPosts=false
        MyDataClass.isPostThread=false
        conversationList.clear()
    }

    private fun fetchDataFromApi(page: Int = 1) {
        var retrofit = RetrofitManager.getRetrofit1()
        var api = retrofit.create(HitApi::class.java)
        api.getConversations(MyDataClass.api_key,
            MyDataClass.myUserId,
            page,
            "desc",
            "last_post_date").enqueue(object : Callback<ResponseThread> {
            override fun onResponse(
                call: Call<ResponseThread>,
                response: Response<ResponseThread>,
            ) {
                if (response.isSuccessful) {
                    var conversationList1 = response.body()?.conversations
                    if (conversationList1!!.isEmpty()) {
                        binding.showConversationsProgressBar.visibility = View.GONE
                        binding.showConversationsNoConversationTv.visibility = View.VISIBLE
                        binding.showConversationsRecyclerView.visibility = View.GONE
                        binding.showConversationsNoConversationTv.setText("You have no new conversations.")
                    } else {
                        conversationList.addAll(conversationList1)
                        MyDataClass.paginationForShowDetails = response.body()?.pagination!!
                        binding.showConversationsNoConversationTv.visibility = View.GONE
                        binding.showConversationsRecyclerView.visibility = View.VISIBLE
                        binding.showConversationsRecyclerView.adapter =
                            context?.let { ConversationAdatper(it, conversationList) }
                        binding.showConversationsRecyclerView.layoutManager =
                            LinearLayoutManager(context)
                        binding.showConversationsProgressBar.visibility=View.GONE
                    }
                }else{
                    binding.showConversationsProgressBar.visibility=View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                binding.showConversationsProgressBar.visibility = View.GONE
            }
        })
    }

}