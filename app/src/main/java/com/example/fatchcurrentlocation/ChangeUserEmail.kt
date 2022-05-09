package com.example.fatchcurrentlocation

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.databinding.FragmentChangeUserEmailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeUserEmail : Fragment() {
    lateinit var binding: FragmentChangeUserEmailBinding
    lateinit var progressBar:ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding= FragmentChangeUserEmailBinding.inflate(layoutInflater, container, false)
progressBar=ProgressDialog(context)
        progressBar.setTitle("Wait...")

binding.changeUserEmailGoBackBtn.setOnClickListener(object :View.OnClickListener{
    override fun onClick(p0: View?) {
        MyDataClass.onBack()
    }
})
        binding.changeUserEmailSaveBtn.setOnClickListener(object :View.OnClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                var email=binding.changeUserEmailEmailEt.text.toString()
                var password=binding.changeUserEmailConfirmPasswordEt.text.toString()
                if(email.isEmpty()){
                    binding.changeUserEmailEmailEt.setError("Field is required")
                    binding.changeUserEmailEmailEt.focusable=View.FOCUSABLE
                }else if(password.isEmpty()){
                    binding.changeUserEmailConfirmPasswordEt.setError("Field is required")
                    binding.changeUserEmailConfirmPasswordEt.focusable=View.FOCUSABLE
                }else{
                    progressBar.show()
                    var retrofit=RetrofitManager.getRetrofit1()
                    var api=retrofit.create(HitApi::class.java)
                    api.updateUserEmailId(MyDataClass.api_key,MyDataClass.myUserId,password,email).enqueue(object :Callback<Map<String,Boolean>>{
                        override fun onResponse(
                            call: Call<Map<String, Boolean>>,
                            response: Response<Map<String, Boolean>>,
                        ) {
                            if(response.isSuccessful){
                                if (response.isSuccessful) {
                                    api.getUsersProfileResponse(MyDataClass.api_key, MyDataClass.myUserId)
                                        .enqueue(object : Callback<ResponseDataClass> {
                                            override fun onResponse(
                                                call: Call<ResponseDataClass>,
                                                response: Response<ResponseDataClass>,
                                            ) {
                                                Log.d("TAG","code${response.code()}")
                                                if (response.isSuccessful) {
                                                    progressBar.dismiss()
                                                    Log.d("TAG","${response.body()?.me?.avatar_urls?.o}")
                                                    MyDataClass.responseDataClass?.user?.email=
                                                        response.body()?.me?.email.toString()
                                                    progressBar.dismiss()
                                                    Toast.makeText(context,"Email changed successfully",Toast.LENGTH_LONG).show()
                                                    MyDataClass.onBack()
                                                }

                                            }

                                            override fun onFailure(
                                                call: Call<ResponseDataClass>,
                                                t: Throwable,
                                            ) {
                                                progressBar.dismiss()
                                            }
                                        })
                                }
                            }else{
                                progressBar.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                            Toast.makeText(context,t.localizedMessage,Toast.LENGTH_LONG).show()
                            progressBar.dismiss()
                        }
                    })
                }
            }
        })
        return binding.root
    }


}