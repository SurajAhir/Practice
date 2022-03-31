package com.example.fatchcurrentlocation.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.ShowDetailsAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.DataClasses.Threads
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.Home
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentShowDetailsBinding
import retrofit2.Call
import retrofit2.Response
import java.util.*


class ShowDetails() : Fragment() {
    var path: Int = 0
    lateinit var btn_text: String
    lateinit var description: String
    lateinit var title: String
    var list1: LinkedList<Threads> = LinkedList()

    constructor(_path: Int, _btn_text: String, _description: String, _title: String) : this() {
        path = _path
        btn_text = _btn_text
        description = _description
        title = _title
        MyDataClass.path = _path
//        MyDataClass.description=_description
//        MyDataClass.title=_title
//        MyDataClass.btn_text=_btn_text
    }

    lateinit var binding: FragmentShowDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentShowDetailsBinding.inflate(layoutInflater, container, false)
        fetchDataFromApi(path)

        binding.showNestedScrollView.setOnScrollChangeListener(object :
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
                        if (MyDataClass.pagination.last_page == MyDataClass.page) {
                            binding.showProgressBar.visibility = View.GONE
                        } else {
                            MyDataClass.page++
                            Log.d("TAG", "true h")
                            binding.showProgressBar.visibility = View.VISIBLE
                            fetchDataFromApi(MyDataClass.path, MyDataClass.page)
                        }
                    }
                }
            }
        })

        binding.showCategory.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(context, Home().javaClass))
            }
        })
        return binding.root
    }

    fun fetchDataFromApi(path: Int, page: Int = 1) {
        var retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getForumsResponse("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4", path, page)
            .enqueue(object : retrofit2.Callback<ResponseThread> {
                override fun onResponse(
                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        var list = response.body()?.threads?.toMutableList()
                        var sticky = response.body()?.sticky?.toMutableList()
                        MyDataClass.pagination = response.body()!!.pagination
                        binding.showProgressBar.visibility = View.GONE
                        list?.let { list1.addAll(it) }
                        sticky?.let { list1.addAll(it) }
                        binding.showCategory.setText(btn_text)
                        binding.showDescription.setText(description)
                        binding.showTitle.setText(title)
                        var showDetailsAdapter =
                            ShowDetailsAdapter(list1, context, response?.body()!!.pagination,title)
                        binding.showRecyclerView.adapter = showDetailsAdapter
                        binding.showRecyclerView.layoutManager = LinearLayoutManager(context)
                    }
                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                }
            })
    }
}