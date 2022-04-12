package com.example.fatchcurrentlocation.Fragments

import android.app.ProgressDialog
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.MyProfileAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ProfilePosts
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentMyProfileBinding
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MyProfile(val responseDataClass: ResponseDataClass) : Fragment() ,AdapterView.OnItemSelectedListener{
    lateinit var binding: FragmentMyProfileBinding
    var list1: LinkedList<ProfilePosts> = LinkedList()
    var isChangedTextStyleBold: Boolean = false
    var isChangedTextStyleItalic: Boolean = false
    var spinneList = arrayOf("Find","Find all threads by ${responseDataClass.user.username}")
    lateinit var progressBar: ProgressDialog
    var spinner: Spinner? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentMyProfileBinding.inflate(layoutInflater, container, false)
        initializeData()
        putDetailsOnFields()
        getResponseFromApi()
        binding.fragmentMyProfileGoBackBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.onBack()
            }
        })
        binding.fragmentMyProfileUserProfileImage.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                var transaction = MyDataClass.getTransaction()
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    ShowUserImage(responseDataClass.user?.avatar_urls?.o ?: ""))
                transaction.addToBackStack(null).commit()
            }
        })
        binding.fragmentMyProfileWriteSomethingBtn.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.fragmentMyProfileWriteSomethingBtn.visibility = View.GONE
                binding.fragmentMyProfileWriteSomethingLayoutToShow.visibility = View.VISIBLE
            }
        })
        binding.fragmentMyProfileBBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isChangedTextStyleBold) {
                    binding.fragmentMyProfileReplyMessageEt.setTypeface(null, Typeface.NORMAL)
                    isChangedTextStyleBold = false
                } else {
                    isChangedTextStyleBold = true
                    binding.fragmentMyProfileReplyMessageEt.setTypeface(null, Typeface.BOLD)
                }
            }
        })
        binding.fragmentMyProfileIBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isChangedTextStyleItalic) {
                    binding.fragmentMyProfileReplyMessageEt.setTypeface(null, Typeface.NORMAL)
                    isChangedTextStyleItalic = false
                } else {
                    isChangedTextStyleItalic = true
                    binding.fragmentMyProfileReplyMessageEt.setTypeface(null, Typeface.ITALIC)
                }
            }
        })
        binding.fragmentMyProfilePostCommentBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.fragmentMyProfileReplyMessageEt.text.toString().isEmpty()) {
                    return
                } else {
                    progressBar.show()
                    var retrofit: Retrofit = RetrofitManager.getRetrofit1()
                    var api: HitApi = retrofit.create(HitApi::class.java)
                    api.getReaponseOfProfilePostsOfMessages(MyDataClass.api_key,
                        MyDataClass.myUserId,
                        responseDataClass.user.user_id,
                        binding.fragmentMyProfileReplyMessageEt.text.toString())
                        .enqueue(object : Callback<ResponseDataClass> {
                            override fun onResponse(
                                call: Call<ResponseDataClass>,
                                response: Response<ResponseDataClass>,
                            ) {
                                Log.d("TAG", "${response.code()}")
                                if (response.body()?.success == true) {
                                    progressBar.dismiss()
                                    Log.d("TAG", "respon ${response.code()}")
                                    binding.fragmentMyProfileReplyMessageEt.setText("")
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
        spinner = binding.fragmentMyProfileSpinnerBtn
        spinner!!.setOnItemSelectedListener(this)
        val aa = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, spinneList) }
        // Set layout to use when the list of choices appear
        aa?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        binding.fragmentMyProfileSpinnerBtn!!.setAdapter(aa)


        return binding.root
    }

    private fun initializeData() {
        progressBar = ProgressDialog(context)
    }

    private fun getResponseFromApi() {
        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getReaponseOfProfilePosts(MyDataClass.api_key,
            MyDataClass.myUserId,
            responseDataClass.user.user_id).enqueue(object : Callback<ResponseDataClass> {
            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>,
            ) {
                var listdata = response.body()?.profile_posts
                if (!listdata?.isEmpty()!!) {
                    binding.fragmentMyProfileNoMessageTv.visibility = View.GONE
                    listdata?.let { list1.addAll(it) }
                    binding.fragmentMyProfileProgressBar.visibility = View.GONE
                    binding.fragmentMyProfileRecyclerView.adapter =
                        context?.let { MyProfileAdapter(it, list1, responseDataClass.user.user_id) }
                    binding.fragmentMyProfileRecyclerView.layoutManager =
                        LinearLayoutManager(context)
                } else {
                    binding.fragmentMyProfileRecyclerView.visibility = View.GONE
                    binding.fragmentMyProfileProgressBar.visibility = View.GONE
                    binding.fragmentMyProfileNoMessageTv.append("${responseDataClass.user.username} profile yet.")
                }

            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun putDetailsOnFields() {
        Picasso.get().load(responseDataClass.user.avatar_urls.o).placeholder(R.drawable.person)
            .into(binding.fragmentMyProfileUserProfileImage)
        binding.fragmentMyProfileUserNameTv.setText(responseDataClass.user.username)
        binding.fragmentMyProfileMemberTv.setText(responseDataClass.user.user_title)
        var registerDate = Date((responseDataClass.user.register_date as Long) * 1000)
        var simpleFormat1 = SimpleDateFormat("dd yyyy")
        binding.fragmentMyProfileJoinedDateTv.setText("${DateFormatSymbols().getShortMonths()[registerDate.month]} ${
            simpleFormat1.format(registerDate)
        }")


        var lastActivityDate = Date((responseDataClass.user.last_activity as Long) * 1000)
        var simpleFormat2 = SimpleDateFormat("HH:mm")
        if (lastActivityDate.minutes < 60) {
            binding.fragmentMyProfileLastSeenTv.setText("${lastActivityDate.minutes} minuts ago")
        } else if (lastActivityDate.date.equals(getCurrentDate())) {
            binding.fragmentMyProfileLastSeenTv.setText("Today at ${
                simpleFormat2.format(lastActivityDate)
            }")
        } else {
            binding.fragmentMyProfileLastSeenTv.setText("${(getCurrentDate() - lastActivityDate.date)} days before")
        }
        binding.fragmentMyProfileMessagesTv.setText(responseDataClass.user.message_count.toString())
        binding.fragmentMyProfileReactionScoreTv.setText(responseDataClass.user.reaction_score.toString())
        binding.fragmentMyProfilePointsTv.setText(responseDataClass.user.trophy_points.toString())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): Int {
        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("d")
        val now: LocalDateTime = LocalDateTime.now()
        return dtf.format(now) as Int
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
     if(spinneList.get(p2).equals("Find")){
         return
     }else if(spinneList.get(p2).startsWith("Find all threads by")){
var transaction=MyDataClass.getTransaction()
         MyDataClass.homeFragmentContainerView.visibility=View.VISIBLE
         MyDataClass.homeNestedScrollView.visibility=View.GONE
         transaction.replace(R.id.home_fragment_containerViewForShowDetails,FindAllThreadsBySomeName(
             MyDataClass.myUserId))
         transaction.addToBackStack(null).commit()
     }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}