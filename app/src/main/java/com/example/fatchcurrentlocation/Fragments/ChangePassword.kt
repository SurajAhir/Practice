package com.example.fatchcurrentlocation.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStructure
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.SortedList
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.MainActivity
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : Fragment() {
    lateinit var binding:FragmentChangePasswordBinding
    lateinit var progressBar:ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentChangePasswordBinding.inflate(layoutInflater, container, false)
        progressBar= ProgressDialog(context)
        progressBar.setTitle("Wait...")
binding.changePasswordGoBackBtn.setOnClickListener(object :View.OnClickListener{
    override fun onClick(p0: View?) {
        MyDataClass.onBack()
    }
})
        binding.changePasswordSaveBtn.setOnClickListener(object :View.OnClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                var oldPassword=binding.changePasswordOldPasswordEt.text.toString()
                var newPassword=binding.changePasswordNewPasswordEt.text.toString()
                var confirmPassword=binding.changePasswordConfirmPasswordEt.text.toString()
                if(oldPassword.isEmpty()){
                    binding.changePasswordOldPasswordEt.setError("Field is Required")
                    binding.changePasswordOldPasswordEt.focusable
                }else if(newPassword.isEmpty()){
                    binding.changePasswordNewPasswordEt.setError("Field is Required")
                    binding.changePasswordNewPasswordEt.focusable
                }else if(confirmPassword.isEmpty()){
                    binding.changePasswordConfirmPasswordEt.setError("Field is Required")
                    binding.changePasswordConfirmPasswordEt.focusable
                }else if(!newPassword.equals(confirmPassword)){
                    binding.changePasswordConfirmPasswordEt.setError("Password not matched")
                    binding.changePasswordConfirmPasswordEt.focusable
                }
                else{
                    progressBar.show()
                    var retrofit=RetrofitManager.getRetrofit1()
                    var api:HitApi=retrofit.create(HitApi::class.java)
                    api.updateUserPassword(MyDataClass.api_key,MyDataClass.myUserId,oldPassword,confirmPassword).enqueue(object :Callback<Map<String,Boolean>>{
                        override fun onResponse(
                            call: Call<Map<String, Boolean>>,
                            response: Response<Map<String, Boolean>>,
                        ) {
                            Log.d("TAG","respoonse ${response.body()?.get("success")}")
                            if(response.isSuccessful){
                                progressBar.dismiss()
                                Toast.makeText(context,"Password changed successfully",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(context,MainActivity().javaClass))
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