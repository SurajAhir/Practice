package com.example.fatchcurrentlocation.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.AdaptersClasses.ShowPostsOfThreadsAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Posts
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.databinding.FragmentShowPostsOfThreadsBinding
import retrofit2.Call
import retrofit2.Response
import java.util.*

class ShowPostsOfThreads(
    val lastPostUsername: String,
    val category: String,
    val title1: String,
    val threadId: Int,
    val lastPostDate: String,
) : Fragment() {
    lateinit var binding: FragmentShowPostsOfThreadsBinding
    var list1: LinkedList<Posts> = LinkedList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentShowPostsOfThreadsBinding.inflate(layoutInflater, container, false)
        MyDataClass.threadId = threadId
        list1.clear()
        MyDataClass.pageForPosts = 1
        fetchDataFromApi(threadId)
        binding.showPostsOfThreadsNestedScrollView.setOnScrollChangeListener(object :
            NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int,
            ) {
                if (v != null) {
                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                        Log.d("TAG",
                            "${MyDataClass.paginationForPostsOfThreads.last_page} and ${MyDataClass.pageForPosts}")
                        if (MyDataClass.paginationForPostsOfThreads.last_page == MyDataClass.pageForPosts) {
                            binding.showPostsOfThreadsProgressBar.visibility = View.GONE
                        } else {
                            MyDataClass.pageForPosts++
                            binding.showPostsOfThreadsProgressBar.visibility = View.VISIBLE
                            fetchDataFromApi(MyDataClass.threadId, MyDataClass.pageForPosts)
                        }
                    }
                }
            }
        })
        binding.showPostsOfThreadsCategory.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(context, Home().javaClass))
            }
        })
        return binding.root
    }

    fun fetchDataFromApi(path: Int, page: Int = 1) {
        var retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getPostsOfThreadsResonse("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",MyDataClass.myUserId, path, page)
            .enqueue(object : retrofit2.Callback<ResponseThread> {
                override fun onResponse(
                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        var list = response.body()?.posts?.toMutableList()
                        MyDataClass.paginationForPostsOfThreads = response.body()!!.pagination
                        binding.showPostsOfThreadsProgressBar.visibility = View.GONE
                        list?.let { list1.addAll(it) }
                        binding.showPostsOfThreadsCategory.setText(title1)
                        binding.showPostsOfThreadsUserNameTv.setText(lastPostUsername)
                        binding.showPostsOfThreadsTitle.setText(category)
                        binding.showPostsOfThreadsPostDateTv.setText(lastPostDate)
                        var showPostsOfThreadsAdapterAdapter =
                            activity?.supportFragmentManager?.let {
                                ShowPostsOfThreadsAdapter(list1, context, response?.body()!!.pagination,
                                    it)
                            }
                        binding.showPostsOfThreadsRecyclerView.adapter =
                            showPostsOfThreadsAdapterAdapter
                        binding.showPostsOfThreadsRecyclerView.layoutManager =
                            LinearLayoutManager(context)
                    }
                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                }
            })
    }



}