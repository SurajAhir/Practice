package com.example.fatchcurrentlocation.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.FindAllThreadsByAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.DataClasses.Threads
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentFindAllThreadsBySomeNameBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FindAllThreadsBySomeName(val profileUserId: Int) : Fragment() {
    lateinit var binding: FragmentFindAllThreadsBySomeNameBinding
var dataList:LinkedList<Threads> = LinkedList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentFindAllThreadsBySomeNameBinding.inflate(layoutInflater, container, false)
        fetchDataFromApi()
binding.fragmentFindAllThreadsByGoBackBtn.setOnClickListener(object :View.OnClickListener{
    override fun onClick(p0: View?) {
        MyDataClass.onBack()
    }
})
        return binding.root
    }

    private fun fetchDataFromApi() {
        var retrofit = RetrofitManager.getRetrofit1()
        var api = retrofit.create(HitApi::class.java)
        api.findAllThreadsBy(MyDataClass.api_key,
            MyDataClass.myUserId,
            profileUserId,
            "desc",
            "last_post_date").enqueue(object : Callback<ResponseThread> {
            override fun onResponse(
                call: Call<ResponseThread>,
                response: Response<ResponseThread>,
            ) {
                Log.d("TAG","code ${response.code()}")
                if(response.isSuccessful){
                    if(response.body()?.threads?.isEmpty()!!){
                        binding.fragmentFindAllThreadsBySearchResultsTv.setText("No results found!")
                        return
                    }else{
                        dataList.addAll(response.body()!!.threads)
                        binding.fragmentFindAllThreadsByRecyclerView.adapter=FindAllThreadsByAdapter(dataList,context)
                        binding.fragmentFindAllThreadsByRecyclerView.layoutManager=LinearLayoutManager(context)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                Log.d("TAG","error ${t.localizedMessage}")
            }
        })
    }

}