package com.example.fatchcurrentlocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.services.RetrofitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkIfAlreadyLogined()
    }
    private fun checkIfAlreadyLogined() {
        var sharedPreferences=getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
        var valid=sharedPreferences.getBoolean("valid",false)
        if(valid){
//            getData(sharedPreferences.getString("email","").toString(),sharedPreferences.getString("password","").toString())
            getData(sharedPreferences.getString("email","").toString())
//            startActivity(Intent(this,Home().javaClass))
//            finish()
        }else{
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }
    private fun getData(email:String) {
        var retrofit= RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit!!.create(HitApi::class.java)
        api.findUserEmail(MyDataClass.api_key,1,email).enqueue(object :Callback<ResponseDataClass>{
            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>
            ) {
                if(response.code().equals(200)) {
                    var responseDataClass: ResponseDataClass? =response.body()
                    if (responseDataClass != null) {
                        MyDataClass.responseDataClass=responseDataClass
                        MyDataClass.myUserId=responseDataClass.user.user_id
                        Log.d("TAG","myUser id ${responseDataClass.user.user_id}")
                        var sharedPreferences=getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
                        var editor=sharedPreferences.edit()
                        editor.putString("email",email)
//                       val gson = Gson()
//                       val json = gson.toJson(responseDataClass)
//                       editor.putString("MyObject", json)
                        editor.commit()
//                   Toast.makeText(this@MainActivity, "Login Successfull", Toast.LENGTH_SHORT).show()
                        var intent= Intent(this@SplashActivity,Home().javaClass)
                        startActivity(intent)
                        finish()
                    }else{
                    }
                }else{
                    Toast.makeText(this@SplashActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                Log.d("TAG",t.localizedMessage)
            }
        })
//        api.getResponseDataForLogin(MyDataClass.api_key,email,password).enqueue(object:
//            Callback<ResponseDataClass> {
//            override fun onResponse(
//                call: Call<ResponseDataClass>,
//                response: Response<ResponseDataClass>,
//            ) {
//                if(response.code().equals(200)) {
//                    var responseDataClass: ResponseDataClass? =response.body()
//                    if (responseDataClass != null) {
//                        MyDataClass.responseDataClass=responseDataClass
//                        MyDataClass.myUserId=responseDataClass.user.user_id
//                        Log.d("TAG","myUser id ${responseDataClass.user.user_id}")
//                        var sharedPreferences=getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
//                        var editor=sharedPreferences.edit()
//                        editor.putString("email",email)
//                        editor.putString("password",password)
////                       val gson = Gson()
////                       val json = gson.toJson(responseDataClass)
////                       editor.putString("MyObject", json)
//                        editor.commit()
////                   Toast.makeText(this@MainActivity, "Login Successfull", Toast.LENGTH_SHORT).show()
//                        var intent= Intent(this@SplashActivity,Home().javaClass)
//                        startActivity(intent)
//                        finish()
//                    }else{
//                    }
//                }else{
//                    Toast.makeText(this@SplashActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
//                Toast.makeText(this@SplashActivity,t.localizedMessage, Toast.LENGTH_LONG).show()
//            }
//        })
    }
}