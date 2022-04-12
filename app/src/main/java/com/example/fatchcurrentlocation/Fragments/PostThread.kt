package com.example.fatchcurrentlocation.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.Home
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentPostThreadBinding
import com.example.fatchcurrentlocation.databinding.FragmentSelectThreadToPostBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostThread(val nodeId: Int,val title: String) : Fragment() {
lateinit var binding: FragmentPostThreadBinding
lateinit var progressBar:ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=
            FragmentPostThreadBinding.inflate(layoutInflater, container, false)
        initializeData()
binding.postThreadGoBackBtn.setOnClickListener(object :View.OnClickListener{
    override fun onClick(p0: View?) {
        MyDataClass.onBack()
    }
})
        binding.postThreadTitleTv.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                binding.postThreadCheckTv.visibility=View.GONE
                Log.d("TAG","enterd")
            }
        })
        binding.postThreadPostThreadBtn.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                var title=binding.postThreadTitleTv.text.toString()
                var message=binding.postThreadMessageEt.text.toString()
                if(title.isEmpty()){
                    binding.postThreadCheckTv.visibility=View.VISIBLE
                    binding.postThreadCheckTv.setText("Please enter a valid title")
                }else if(message.isEmpty()){
                    binding.postThreadCheckTv.visibility=View.VISIBLE
                    binding.postThreadCheckTv.setError("Please enter a valid message")
                }else{
                    progressBar.show()
                    var retrofit=RetrofitManager.getRetrofit1()
                    var api=retrofit.create(HitApi::class.java)
                    api.postThread(
                        MyDataClass.api_key,MyDataClass.myUserId,nodeId,title,message
                    ).enqueue(object :Callback<Map<String,Boolean>>{
                        override fun onResponse(
                            call: Call<Map<String, Boolean>>,
                            response: Response<Map<String, Boolean>>
                        ) {
                            Log.d("TAG","code${response.code()}")
                            if(response.isSuccessful&&response.body()?.get("success")==true){
                                progressBar.dismiss()
                                MyDataClass.onBack()
                            }else{
                                progressBar.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                           progressBar.dismiss()
                        }
                    })
                }
            }
        })
        return binding.root
    }

    private fun initializeData() {
        progressBar= ProgressDialog(context)
        binding.postThreadGoBackBtn.setText(title)
    }

}