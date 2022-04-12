package com.example.fatchcurrentlocation.Fragments

import android.app.ProgressDialog
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.UserProfileAdapter
import com.example.fatchcurrentlocation.DataClasses.*
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentUserProfileBinding
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

class UserProfile(val list: LinkedList<Posts>, val position: Int) : Fragment(),AdapterView.OnItemSelectedListener {
    lateinit var binding: FragmentUserProfileBinding
    var list1: LinkedList<ProfilePosts> = LinkedList()
    var isChangedTextStyleBold: Boolean = false
    var isChangedTextStyleItalic: Boolean = false
    lateinit var progressBar: ProgressDialog
    var spinneList = arrayOf("Find","Find all threads by ${list.get(position).User.username}")
    var spinner: Spinner? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        initializeData()
        putDetailsOnFields()
        getResponseFromApi()
        binding.fragmentUserProfileGoBackBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.onBack()
            }
        })
        binding.fragmentUserProfileUserProfileImage.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                var transaction = MyDataClass.getTransaction()
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    ShowUserImage(list.get(position).User?.avatar_urls?.o ?: ""))
                transaction.addToBackStack(null).commit()
            }
        })
        binding.fragmentUserProfileWriteSomethingBtn.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.fragmentUserProfileWriteSomethingBtn.visibility = View.GONE
                binding.fragmentUserProfileWriteSomethingLayoutToShow.visibility = View.VISIBLE
            }
        })
        binding.fragmentUserProfileBBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isChangedTextStyleBold) {
                    binding.fragmentUserProfileReplyMessageEt.setTypeface(null, Typeface.NORMAL)
                    isChangedTextStyleBold = false
                } else {
                    isChangedTextStyleBold = true
                    binding.fragmentUserProfileReplyMessageEt.setTypeface(null, Typeface.BOLD)
                }
            }
        })
        binding.fragmentUserProfileIBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isChangedTextStyleItalic) {
                    binding.fragmentUserProfileReplyMessageEt.setTypeface(null, Typeface.NORMAL)
                    isChangedTextStyleItalic = false
                } else {
                    isChangedTextStyleItalic = true
                    binding.fragmentUserProfileReplyMessageEt.setTypeface(null, Typeface.ITALIC)
                }
            }
        })
        binding.fragmentUserProfilePostCommentBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.fragmentUserProfileReplyMessageEt.text.toString().isEmpty()) {
                    return
                } else {
                    progressBar.show()
                    var retrofit: Retrofit = RetrofitManager.getRetrofit1()
                    var api: HitApi = retrofit.create(HitApi::class.java)
                    api.getReaponseOfProfilePostsOfMessages(MyDataClass.api_key,
                        MyDataClass.myUserId,
                        list.get(position).user_id,
                        binding.fragmentUserProfileReplyMessageEt.text.toString())
                        .enqueue(object : Callback<ResponseDataClass> {
                            override fun onResponse(
                                call: Call<ResponseDataClass>,
                                response: Response<ResponseDataClass>,
                            ) {
                                Log.d("TAG", "${response.code()}")
                                if (response.body()?.success == true) {
                                    progressBar.dismiss()
                                    Log.d("TAG", "respon ${response.code()}")
                                    binding.fragmentUserProfileReplyMessageEt.setText("")
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

        spinner = binding.fragmentUserProfileSpinnerBtn
        spinner!!.setOnItemSelectedListener(this)
        val aa = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, spinneList) }
        // Set layout to use when the list of choices appear
        aa?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        binding.fragmentUserProfileSpinnerBtn!!.setAdapter(aa)

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
            list.get(position).user_id).enqueue(object : Callback<ResponseDataClass> {
            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>,
            ) {
                var listdata = response.body()?.profile_posts
                if (!listdata?.isEmpty()!!) {
                    binding.fragmentUserProfileNoMessageTv.visibility = View.GONE
                    listdata?.let { list1.addAll(it) }
                    binding.fragmentUserProfileProgressBar.visibility = View.GONE
                    binding.fragmentUserProfileRecyclerView.adapter =
                        context?.let { UserProfileAdapter(it, list1,list.get(position).user_id) }
                    binding.fragmentUserProfileRecyclerView.layoutManager =
                        LinearLayoutManager(context)
                } else {
                    binding.fragmentUserProfileRecyclerView.visibility = View.GONE
                    binding.fragmentUserProfileProgressBar.visibility = View.GONE
                    binding.fragmentUserProfileNoMessageTv.append("${list.get(position).User.username} profile yet.")
                }

            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun putDetailsOnFields() {
        Picasso.get().load(list.get(position).User.avatar_urls.o).placeholder(R.drawable.person)
            .into(binding.fragmentUserProfileUserProfileImage)
        binding.fragmentUserProfileUserNameTv.setText(list.get(position).User.username)
        binding.fragmentUserProfileMemberTv.setText(list.get(position).User.user_title)
        var registerDate = Date((list.get(position).User.register_date as Long) * 1000)
        var simpleFormat1 = SimpleDateFormat("dd yyyy")
        binding.fragmentUserProfileJoinedDateTv.setText("${DateFormatSymbols().getShortMonths()[registerDate.month]} ${
            simpleFormat1.format(registerDate)
        }")


        var lastActivityDate = Date((list.get(position).User.last_activity as Long) * 1000)
        var simpleFormat2 = SimpleDateFormat("HH:mm")
        if (lastActivityDate.minutes < 60) {
            binding.fragmentUserProfileLastSeenTv.setText("${lastActivityDate.minutes} minuts ago")
        } else if (lastActivityDate.date.equals(getCurrentDate())) {
            binding.fragmentUserProfileLastSeenTv.setText("Today at ${
                simpleFormat2.format(lastActivityDate)
            }")
        } else {
            binding.fragmentUserProfileLastSeenTv.setText("${(getCurrentDate() - lastActivityDate.date)} days before")
        }
        binding.fragmentUserProfileMessagesTv.setText(list.get(position).User.message_count.toString())
        binding.fragmentUserProfileReactionScoreTv.setText(list.get(position).reaction_score.toString())
        binding.fragmentUserProfilePointsTv.setText(list.get(position).User.trophy_points.toString())

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
            transaction.replace(R.id.home_fragment_containerViewForShowDetails,FindAllThreadsBySomeName(list.get(position).user_id))
            transaction.addToBackStack(null).commit()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}