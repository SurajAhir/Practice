package com.example.fatchcurrentlocation.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.databinding.FragmentYourAccountBinding
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File


class YourAccount(val responseDataObject: ResponseDataClass) : Fragment() {
    var responseDataObject1 = responseDataObject
    lateinit var gso: GoogleSignInOptions
    lateinit var gsc: GoogleSignInClient
    lateinit var binding: FragmentYourAccountBinding
    lateinit var progressBar: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentYourAccountBinding.inflate(layoutInflater, container, false)
        initializeData()
        binding.updateStatusEt.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.updateStatusBtn.visibility = View.VISIBLE
            }
        })
        binding.logout.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                var sharedPreferences=activity?.getSharedPreferences("LoginUserDetails",Context.MODE_PRIVATE)
                var editor=sharedPreferences?.edit()
                editor?.putBoolean("valid",false)
                editor?.clear()
                editor?.commit()
                Firebase.auth.signOut()
                gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
                gsc= activity?.let { GoogleSignIn.getClient(it,gso) }!!
                gsc.signOut()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(context,MainActivity()::class.java))
            }
        })
        binding.changeUserProfileBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Dexter.withActivity(context as Activity?)
                    .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            if(report?.areAllPermissionsGranted() == true){
                            var intent = Intent(Intent.ACTION_PICK)
                            intent.setType("image/*")
                            startActivityForResult(intent, 1001)
                            }else{
                                Toast.makeText(context,"Allow Storage Permission!",Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?,
                        ) {
                            token?.continuePermissionRequest()
                        }

                    }).check()


            }
        })
        binding.passwordAndSecurity.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var transaction = MyDataClass.getTransaction()
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    ChangePassword())
                transaction.addToBackStack(null).commit()
            }
        })
        binding.accountDetails.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                var transaction = MyDataClass.getTransaction()
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    AccountDetails())
                transaction.addToBackStack(null).commit()
            }
        })
        binding.userMessageCount.setText(responseDataObject1.user.message_count.toString())
        binding.userReactionScoreCount.setText(responseDataObject1.user.reaction_score.toString())
        binding.userTrophyPointsCount.setText(responseDataObject1.user.trophy_points.toString())
        binding.userName.setText(responseDataObject1.user.username)
        if(responseDataObject1.user.avatar_urls.o==null){
            binding.userAccountProfileTv.visibility=View.VISIBLE
            binding.userAccountProfile.visibility=View.GONE
            binding.userAccountProfileTv.gravity= Gravity.CENTER
            binding.userAccountProfileTv.setText(MyDataClass.responseDataClass!!.user.username.get(0).toString())
        }else{
            Picasso.get().load(responseDataObject1.user.avatar_urls.o).placeholder(R.drawable.person)
                .into(binding.userAccountProfile)
        }
        binding.userName.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                var transaction = MyDataClass.getTransaction()
                MyDataClass.responseDataClass?.let { MyProfile(it) }?.let {
                    transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                        it)
                }
                transaction.addToBackStack(null).commit()
            }
        })
        binding.privacy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                var transaction = MyDataClass.getTransaction()
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    Privacy())
                transaction.addToBackStack(null).commit()
            }
        })
        binding.updateStatusBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.updateStatusEt.text.toString().isEmpty()) {
                    return
                } else {
                    progressBar.show()
                    var retrofit: Retrofit = RetrofitManager.getRetrofit1()
                    var api: HitApi = retrofit.create(HitApi::class.java)
                    api.getReaponseOfProfilePostsOfMessages(MyDataClass.api_key,
                        MyDataClass.myUserId,
                        responseDataObject.user.user_id,
                        binding.updateStatusEt.text.toString(),"")
                        .enqueue(object : Callback<ResponseDataClass> {
                            override fun onResponse(
                                call: Call<ResponseDataClass>,
                                response: Response<ResponseDataClass>,
                            ) {
                                Log.d("TAG", "${response.code()}")
                                if (response.body()?.success == true) {
                                    progressBar.dismiss()
                                    Log.d("TAG", "respon ${response.code()}")
                                    binding.updateStatusEt.setText("")
                                    binding.updateStatusBtn.visibility = View.GONE
                                } else {
                                    progressBar.dismiss()
                                }
                            }

                            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                                progressBar.dismiss()
                                Log.d("TAG", "${t.localizedMessage} errro")
                            }
                        })
                }
            }
        })
        return binding.root
    }

    private fun initializeData() {
        progressBar = ProgressDialog(context)
        MyDataClass.isGoProfile=true

        MyDataClass.isGoNotification=false
        MyDataClass.isGoConversation=false
        MyDataClass.isGoForLatestPosts=false
        MyDataClass.isPostThread=false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            progressBar.show()
        if (requestCode == 1001) {
           if(data?.data!=null){
               var uri: Uri? = data.data
               var destination = RealPathUtil.getRealPath(context, uri)
               var file = File(destination)
               var requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
               var multiPartFile = MultipartBody.Part.createFormData("avatar", file.name, requestBody)
               var retrofit: Retrofit = RetrofitManager.getRetrofit1()
               var api: HitApi = retrofit.create(HitApi::class.java)
               api.updateUserProfileImage(MyDataClass.api_key, MyDataClass.myUserId, multiPartFile)
                   .enqueue(object : Callback<Map<String, Boolean>> {
                       override fun onResponse(
                           call: Call<Map<String, Boolean>>,
                           response: Response<Map<String, Boolean>>,
                       ) {
                           if (response.isSuccessful) {
                               api.getUsersProfileResponse(MyDataClass.api_key, MyDataClass.myUserId)
                                   .enqueue(object : Callback<ResponseDataClass> {
                                       override fun onResponse(
                                           call: Call<ResponseDataClass>,
                                           response: Response<ResponseDataClass>,
                                       ) {
                                           Log.d("TAG", "code${response.code()}")
                                           if (response.isSuccessful) {
                                               progressBar.dismiss()
                                               responseDataObject1.user.avatar_urls.o =
                                                   response.body()?.me?.avatar_urls?.o!!
                                               MyDataClass.responseDataClass!!.user.avatar_urls.o =
                                                   response.body()?.me?.avatar_urls?.o!!
                                               binding.userAccountProfileTv.visibility=View.GONE
                                               binding.userAccountProfile.visibility=View.VISIBLE
                                               Picasso.get().load(response.body()!!.me.avatar_urls.o)
                                                   .placeholder(R.drawable.ic_no_image)
                                                   .into(binding.userAccountProfile)
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
                       }

                       override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                           Log.d("TAG", "errror ${t.localizedMessage}")
                           progressBar.dismiss()
                       }
                   })
           }else{
               progressBar.dismiss()
           }
        }
    }

}