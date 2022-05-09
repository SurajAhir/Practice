package com.example.fatchcurrentlocation.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.ShowDetailsAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.DataClasses.Threads
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentShowLatestPostsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ShowLatestPosts : Fragment() {
    lateinit var binding: FragmentShowLatestPostsBinding
    var list1: LinkedList<Threads> = LinkedList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentShowLatestPostsBinding.inflate(layoutInflater, container, false)
        initializeData()
        fetchDataFromApi()
        binding.showLatestPostsNesttedScrollView.setOnScrollChangeListener(object :
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
                        if (MyDataClass.paginationForShowDetails.last_page == MyDataClass.page) {
                            binding.showLatestPostsProgressBar.visibility = View.GONE
                        } else {
                            MyDataClass.page++
                            Log.d("TAG", "true h")
                            binding.showLatestPostsProgressBar.visibility = View.VISIBLE
                            fetchDataFromApi(MyDataClass.page)
                        }
                    }
                }
            }
        })
        return binding.root
    }

    private fun fetchDataFromApi(page: Int = 1) {
        var retrofit = RetrofitManager.getRetrofit1()
        var api = retrofit.create(HitApi::class.java)
        api.getLatestPosts(MyDataClass.api_key, MyDataClass.myUserId, page, "post_date", "desc")
            .enqueue(object : Callback<ResponseThread> {
                override fun onResponse(
                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        var list = response.body()?.threads?.toMutableList()
                        var sticky = response.body()?.sticky?.toMutableList()
                        MyDataClass.paginationForShowDetails = response.body()!!.pagination
                        list?.let { list1.addAll(it) }
                        sticky?.let { list1.addAll(it) }
                        var showDetailsAdapter =
                            ShowDetailsAdapter(list1,
                                context,
                                response?.body()!!.pagination,
                                "",
                                1002)
                        binding.showLatestPostsRecyclerView.adapter = showDetailsAdapter
                        binding.showLatestPostsRecyclerView.layoutManager =
                            LinearLayoutManager(context)
                    } else {
                        binding.showLatestPostsProgressBar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    binding.showLatestPostsProgressBar.visibility = View.GONE
                }
            })
    }

    private fun initializeData() {
MyDataClass.page=1
        MyDataClass.isGoForLatestPosts=true
    }


}