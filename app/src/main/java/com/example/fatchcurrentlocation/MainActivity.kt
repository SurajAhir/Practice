package com.example.fatchcurrentlocation

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class MainActivity : AppCompatActivity() {
    lateinit var userId: EditText
    lateinit var userPassword: EditText
    lateinit var loginBtn: TextView
    lateinit var googleLogin:TextView
    lateinit var jumpToSignUp: TextView
    var retrofit: Retrofit? = null
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
        loginBtn.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                if (userId.text.toString().isEmpty()) {
                    userId.setError("Email Cann't be Empty")
                    userId.focusable
                } else if (userPassword.text.toString().isEmpty()) {
                    userPassword.setError("Password required")
                    userPassword.focusable
                } else {
                    Log.d("TAG", userId.text.toString() + "" + userPassword.text.toString())
                    getData(userId.text.toString(), userPassword.text.toString())
                }
            }
        })
        jumpToSignUp.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val browserIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.technofino.in/community/register/"))
                startActivity(browserIntent)
            }
        })
        googleLogin.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                startActivity(Intent(this@MainActivity,GoogleSignUp()::class.java))
            }
        })
    }


    private fun getData(email: String, password: String) {
        progressDialog.show()
        retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit!!.create(HitApi::class.java)
        api.getResponseDataForLogin("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4", email, password)
            .enqueue(object : Callback<ResponseDataClass> {
                override fun onResponse(
                    call: Call<ResponseDataClass>,
                    response: Response<ResponseDataClass>,
                ) {
                    if (response.code().equals(200)) {
                        var responseDataClass: ResponseDataClass? = response.body()
                        if (responseDataClass != null) {
                            MyDataClass.responseDataClass = responseDataClass
                            MyDataClass.myUserId = responseDataClass.user.user_id
                            Log.d("TAG", "myUser id ${responseDataClass.user.user_id}")
                            var sharedPreferences =
                                getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
                            var editor = sharedPreferences.edit()
                            editor.putString("email", email)
                            editor.putString("password", password)
                            response.body()?.me?.user_id?.let { editor.putInt("userId", it) }
//                       val gson = Gson()
//                       val json = gson.toJson(responseDataClass)
//                       editor.putString("MyObject", json)
                            editor.commit()
//                   Toast.makeText(this@MainActivity, "Login Successfull", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this@MainActivity, Home().javaClass)
                            startActivity(intent)
                            finish()
                            progressDialog.dismiss()
                        } else {
                            progressDialog.dismiss()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Invalid Credentials", Toast.LENGTH_SHORT)
                            .show()
                        progressDialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.localizedMessage, Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            })
    }

    private fun initialize() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please Wait...")
        userId = findViewById(R.id.login_user_id)
        googleLogin=findViewById(R.id.login_google_btn)
        userPassword = findViewById(R.id.login_user_password)
        loginBtn = findViewById(R.id.login_btn_login)
        jumpToSignUp = findViewById(R.id.login_jumpToSignUp)
    }
//fun getReactionsDialog():DialogFragment{
//    var reactionDialog= ReactionDialogClass()
//    reactionDialog.show(supportFragmentManager, reactionDialog.javaClass.simpleName)
//    return reactionDialog
//}
}

