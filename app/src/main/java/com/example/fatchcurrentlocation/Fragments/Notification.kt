package com.example.fatchcurrentlocation.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.NotificationAdapter
import com.example.fatchcurrentlocation.DataClasses.Alerts
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentNotificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Notification : Fragment() {
    lateinit var binding: FragmentNotificationBinding
    var notificationList: LinkedList<Alerts> = LinkedList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        initializeData()
        fetchDataFromApi()
        binding.notificationNestedScrollView.setOnScrollChangeListener(object :
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
                            binding.notificationProgressBar.visibility = View.GONE
                        } else {
                            MyDataClass.page++
                            binding.notificationProgressBar.visibility = View.VISIBLE
                            fetchDataFromApi(MyDataClass.page)
                        }
                    }
                }
            }
        })

        return binding.root
    }

    private fun initializeData() {
        MyDataClass.page = 1
        notificationList.clear()
        MyDataClass.isGoNotification=true
        MyDataClass.isGoProfile=false
        MyDataClass.isGoConversation=false
        MyDataClass.isGoForLatestPosts=false
        MyDataClass.isPostThread=false
    }

    private fun fetchDataFromApi(page: Int = 1) {
        var retrofit = RetrofitManager.getRetrofit1()
        var api = retrofit.create(HitApi::class.java)
        api.getAlerts(MyDataClass.api_key, MyDataClass.myUserId, page, "desc", "last_post_date")
            .enqueue(object : Callback<ResponseThread> {
                override fun onResponse(
                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    if (response.isSuccessful) {
                        var alertList = response.body()?.alerts
                        if (alertList!!.isEmpty()) {
                            binding.notificationProgressBar.visibility = View.GONE
                            binding.notificationNoAlertsTv.visibility = View.VISIBLE
                            binding.notificationRecyclerView.visibility = View.GONE
                            binding.notificationNoAlertsTv.setText("You have no new alerts.")
                        } else {
                            notificationList.addAll(alertList)
                            MyDataClass.paginationForShowDetails = response.body()?.pagination!!
                            binding.notificationNoAlertsTv.visibility = View.GONE
                            binding.notificationRecyclerView.visibility = View.VISIBLE
                            binding.notificationRecyclerView.adapter =
                                context?.let { NotificationAdapter(it, notificationList) }
                            binding.notificationRecyclerView.layoutManager =
                                LinearLayoutManager(context)
                            binding.notificationProgressBar.visibility = View.GONE
                        }
                    } else {
                        binding.notificationProgressBar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    binding.notificationProgressBar.visibility = View.GONE
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            })
    }

}