package com.example.fatchcurrentlocation

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.databinding.ActivityMainBinding
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit


class MainActivity : AppCompatActivity() {
    lateinit var twitterloginButton: TwitterLoginButton
    lateinit var mTwitterAuthClient: TwitterAuthClient
    lateinit var binding: ActivityMainBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var gso: GoogleSignInOptions
    lateinit var gsc: GoogleSignInClient
    lateinit var userId: EditText
    lateinit var userPassword: EditText
    lateinit var loginBtn: TextView
    lateinit var twitterloginBtn: TextView
    lateinit var googleLogin: TextView
    lateinit var jumpToSignUp: TextView
    var retrofit: Retrofit? = null
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Twitter.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                    getData(userId.text.toString(), userPassword.text.toString())
                }
            }
        })
        jumpToSignUp.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.technofino.in/community/register/")
                )
                startActivity(browserIntent)
            }
        })
        googleLogin.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    var intent = gsc.signInIntent
                    startActivityForResult(intent, 100)
                }
            })
        twitterloginBtn.setOnClickListener {
            mTwitterAuthClient.authorize(this, object : Callback<TwitterSession?>() {
                override fun success(twitterSessionResult: Result<TwitterSession?>?) {
                    val session = TwitterCore.getInstance().sessionManager.activeSession
                    val authToken = session.authToken
                    val token = authToken.token
                    val secret = authToken.secret
//                   var intent=Intent(this@MainActivity,JustPractice::class.java)
//                    intent.putExtra("userId",session.userId.toString())
//                    Log.d("TAG",session.userId.toString())
//                    startActivity(intent)
//                    getTwitterEmail(session)
                    getUserEmailFromTwitterId(session.userId)
                }

                override fun failure(e: TwitterException) {
                    e.printStackTrace()
                }
            })
        }


    }

    private fun getUserEmailFromTwitterId(userId: Long) {
        var retrofit=RetrofitManager.getRetrofitForUser()
        var api=retrofit.create(HitApi::class.java)
        progressDialog.show()
        api.getUserEmailFromTwitterId(userId).enqueue(object :retrofit2.Callback<Map<String,Any>>{
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                Log.d("TAGA", (response.body()?.get("status")).toString())
                Log.d("TAGA",userId.toString())
                if(response.isSuccessful&&response.body()?.get("status")==1||response.body()?.get("status")==1.0){

                    var email=response.body()?.get("email").toString()
                    var retrofit = RetrofitManager.getRetrofit1()
                    var api = retrofit.create(HitApi::class.java)
                    if (!email?.isEmpty()!!&&email!=null) {
                        api.findUserEmail(MyDataClass.api_key, 1, email).enqueue(object :
                            retrofit2.Callback<ResponseDataClass> {
                            override fun onResponse(
                                call: Call<ResponseDataClass>,
                                response: Response<ResponseDataClass>
                            ) {
                                Log.d("TAG", " $email and  ${MyDataClass.api_key}")
                                if (response.isSuccessful) {
                                    var user = response.body()?.user
                                    if (user != null) {
                                        MyDataClass.responseDataClass = response.body()
                                        MyDataClass.myUserId = response.body()?.user?.user_id!!
                                        var sharedPreferences =
                                            getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
                                        var editor = sharedPreferences.edit()
                                        editor.putString("email", email)
//                                        editor.putString("password", password)
                                        response.body()?.me?.user_id?.let {
                                            editor.putInt(
                                                "userId",
                                                it
                                            )
                                        }
                                        editor.commit()
//                   Toast.makeText(this@MainActivity, "Login Successfull", Toast.LENGTH_SHORT).show()
                                        var intent = Intent(this@MainActivity, Home().javaClass)
                                        startActivity(intent)
                                        finish()
                                        progressDialog.dismiss()
                                    } else {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Your're not registered! Please register to continue...",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return
                                    }
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Your're not registered! Please register to continue...",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return
                                }
                            }

                            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                                Log.d("TAG", t.localizedMessage)
                                progressDialog.dismiss()
                            }
                        })
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@MainActivity,
                            "You haven't linked your email account to the Twitter. Kindly link your account and then try again later!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }else if(response.body()?.get("status")==0.0||response.body()?.get("status")==0){
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@MainActivity,
                        "You haven't linked your email account to the Twitter. Kindly link your account and then try again later!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.d("TAG",t.localizedMessage)
                progressDialog.dismiss()
            }
        })

    }

    private fun getTwitterEmail(session: TwitterSession?) {
        val authClient = TwitterAuthClient()
        authClient.requestEmail(session, object : Callback<String?>() {
            override fun success(result: Result<String?>?) {
                progressDialog.show()
                Log.d("TAGA", result?.data.toString())
                var email = result?.data
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                if (!email?.isEmpty()!!&&email!=null) {
                    api.findUserEmail(MyDataClass.api_key, 1, email).enqueue(object :
                        retrofit2.Callback<ResponseDataClass> {
                        override fun onResponse(
                            call: Call<ResponseDataClass>,
                            response: Response<ResponseDataClass>
                        ) {
                            Log.d("TAG", " $email and  ${MyDataClass.api_key}")
                            if (response.isSuccessful) {
                                var user = response.body()?.user
                                if (user != null) {
                                    MyDataClass.responseDataClass = response.body()
                                    MyDataClass.myUserId = response.body()?.user?.user_id!!
                                    var sharedPreferences =
                                        getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
                                    var editor = sharedPreferences.edit()
                                    editor.putString("email", email)
//                                        editor.putString("password", password)
                                    response.body()?.me?.user_id?.let {
                                        editor.putInt(
                                            "userId",
                                            it
                                        )
                                    }
                                    editor.commit()
//                   Toast.makeText(this@MainActivity, "Login Successfull", Toast.LENGTH_SHORT).show()
                                    var intent = Intent(this@MainActivity, Home().javaClass)
                                    startActivity(intent)
                                    finish()
                                    progressDialog.dismiss()
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Your're not registered! Please register to continue...",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return
                                }
                            } else {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Your're not registered! Please register to continue...",
                                    Toast.LENGTH_LONG
                                ).show()
                                return
                            }
                        }

                        override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                            Log.d("TAG", t.localizedMessage)
                            progressDialog.dismiss()
                        }
                    })
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@MainActivity,
                        "You haven't linked your email account to the Twitter. Kindly link your account and then try again later!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun failure(exception: TwitterException?) {
                Log.d("Email", exception.toString())
                progressDialog.dismiss()
            }
        })

    }

    private fun getData(email: String, password: String) {
        progressDialog.show()
        retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit!!.create(HitApi::class.java)
        api.getResponseDataForLogin(MyDataClass.api_key, email, password)
            .enqueue(object : retrofit2.Callback<ResponseDataClass> {
                override fun onResponse(
                    call: Call<ResponseDataClass>,
                    response: Response<ResponseDataClass>,
                ) {
                    Log.d("TAG", "myUser id ${MyDataClass.api_key}")
                    if (response.code().equals(200)) {
                        var responseDataClass: ResponseDataClass? = response.body()
                        if (responseDataClass != null) {
                            MyDataClass.responseDataClass = responseDataClass
                            MyDataClass.myUserId = responseDataClass.user.user_id
                            var sharedPreferences =
                                getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
                            var editor = sharedPreferences.edit()
                            editor.putString("email", email)
//                            editor.putString("password", password)
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
//        twitterloginButton = findViewById(R.id.login_twiiter_btn)
        mTwitterAuthClient = TwitterAuthClient()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please Wait...")
        userId = findViewById(R.id.login_user_id)
        googleLogin = findViewById(R.id.login_google_btn)
//        twitterLogin=findViewById(R.id.login_twitter_btn)
        userPassword = findViewById(R.id.login_user_password)
        loginBtn = findViewById(R.id.login_btn_login)
        jumpToSignUp = findViewById(R.id.login_jumpToSignUp)
        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()
        twitterloginBtn = findViewById(R.id.login_twitter_btn)
    }

    //fun getReactionsDialog():DialogFragment{
//    var reactionDialog= ReactionDialogClass()
//    reactionDialog.show(supportFragmentManager, reactionDialog.javaClass.simpleName)
//    return reactionDialog
//}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            progressDialog.show()
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                task.getResult(ApiException::class.java)
                var account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
                if (account != null) {
                    var email: String? = account.email
                    var retrofit = RetrofitManager.getRetrofit1()
                    var api = retrofit.create(HitApi::class.java)
                    if (email != null) {

                        api.findUserEmail(MyDataClass.api_key, 1, email).enqueue(object :
                            retrofit2.Callback<ResponseDataClass> {
                            override fun onResponse(
                                call: Call<ResponseDataClass>,
                                response: Response<ResponseDataClass>
                            ) {
                                Log.d("TAG", "code ${response.code()}")
                                Log.d("TAG", " $email and  ${MyDataClass.api_key}")
                                if (response.isSuccessful) {
                                    var user = response.body()?.user
                                    if (user != null) {
                                        MyDataClass.responseDataClass = response.body()
                                        MyDataClass.myUserId = response.body()?.user?.user_id!!
                                        var sharedPreferences =
                                            getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
                                        var editor = sharedPreferences.edit()
                                        editor.putString("email", email)
//                                        editor.putString("password", password)
                                        response.body()?.me?.user_id?.let {
                                            editor.putInt(
                                                "userId",
                                                it
                                            )
                                        }
                                        editor.commit()
//                   Toast.makeText(this@MainActivity, "Login Successfull", Toast.LENGTH_SHORT).show()
                                        var intent = Intent(this@MainActivity, Home().javaClass)
                                        startActivity(intent)
                                        finish()
                                        progressDialog.dismiss()
                                    } else {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Your're not registered! Please register to continue...",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return
                                    }
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Your're not registered! Please register to continue...",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return
                                }
                            }

                            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                                Log.d("TAG", t.localizedMessage)
                                progressDialog.dismiss()
                            }
                        })
                    }
                } else {
                    progressDialog.dismiss()
                }
            } catch (e: ApiException) {
                progressDialog.dismiss()
                Log.e("TAG", e.localizedMessage)
            }
        }
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
    }
}


