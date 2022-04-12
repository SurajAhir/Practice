package com.example.fatchcurrentlocation.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.NotificationAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentNotificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Notification : Fragment() {
   lateinit var binding:FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentNotificationBinding.inflate(layoutInflater, container, false)
fetchDataFromApi()


        return binding.root
    }

    private fun fetchDataFromApi() {
        var retrofit=RetrofitManager.getRetrofit1()
        var api=retrofit.create(HitApi::class.java)
        api.getAlerts(MyDataClass.api_key,MyDataClass.myUserId).enqueue(object :Callback<ResponseThread>{
            override fun onResponse(
                call: Call<ResponseThread>,
                response: Response<ResponseThread>,
            ) {
                if(response.isSuccessful){
                    var alertList=response.body()?.alerts
                    if(alertList!!.isEmpty()){
                        binding.notificationNoAlertsTv.visibility=View.VISIBLE
                        binding.notificationRecyclerView.visibility=View.GONE
                        binding.notificationNoAlertsTv.setText("You have no new alerts.")
                    }else{
                        binding.notificationNoAlertsTv.visibility=View.GONE
                        binding.notificationRecyclerView.visibility=View.VISIBLE
                        binding.notificationRecyclerView.adapter=
                            context?.let { NotificationAdapter(it,alertList) }
                        binding.notificationRecyclerView.layoutManager=LinearLayoutManager(context)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
              Toast.makeText(context,t.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        })
    }

}