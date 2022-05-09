package com.example.fatchcurrentlocation.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentPrivacyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Privacy : Fragment(), AdapterView.OnItemSelectedListener {
    lateinit var binding: FragmentPrivacyBinding
    var list1 = arrayOf("${MyDataClass.responseDataClass!!.user.allow_post_profile.capitalize()}", "Members only", "People you follow", "Nobody")
    var list2 = arrayOf("${MyDataClass.responseDataClass!!.user.allow_receive_news_feed.capitalize()}", "People you follow", "Nobody")
    var list3 = arrayOf("${MyDataClass.responseDataClass!!.user.allow_send_personal_conversation.capitalize()}", "Members only", "People you follow", "Nobody")
    var list4 = arrayOf("${MyDataClass.responseDataClass!!.user.allow_view_identities.capitalize()}", "People you follow", "Nobody")
    var list5 = arrayOf("${MyDataClass.responseDataClass!!.user.allow_view_profile.capitalize()}", "Members only", "People you follow", "Nobody")
    lateinit var progressBar: ProgressDialog
    var isShowYourOnlineStatus: Boolean = false
    var isShowYourCurrentActivity: Boolean = false
    var isShowDayAndMonth: Boolean = false
    var isShowYearOfBirth: Boolean = false
    var isReceiveNewsAndUpdate: Boolean = false
    var ViewYourDetails: String = ""
    var PostMessagesOnYour: String = ""
    var ReceivedYourNewsFeed: String = ""
    var StartConversationsWith: String = ""
    var ViewYourIdentities: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentPrivacyBinding.inflate(layoutInflater, container, false)
        initializeData()
        binding.privacyShowYourOnlineStatusCheckbox.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                isShowYourOnlineStatus = binding.privacyShowYourOnlineStatusCheckbox.isChecked
            }
        })
        binding.privacyShowYourCurrentActivityCheckbox.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                isShowYourCurrentActivity = binding.privacyShowYourCurrentActivityCheckbox.isChecked
            }
        })
        binding.privacyShowDayAndMonthOfBirthCheckbox.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                isShowDayAndMonth = binding.privacyShowDayAndMonthOfBirthCheckbox.isChecked
            }
        })
        binding.privacyShowYearOfBirthCheckbox.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                isShowYearOfBirth = binding.privacyShowYearOfBirthCheckbox.isChecked
            }
        })
        binding.privacyReceiveNewsAndUpdateEmailsCheckbox.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                isReceiveNewsAndUpdate = binding.privacyReceiveNewsAndUpdateEmailsCheckbox.isChecked
            }
        })
        binding.privacySaveBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                progressBar.show()
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                api.updatePrivacy(
                    MyDataClass.api_key,
                    MyDataClass.myUserId,
                    isShowYourOnlineStatus,
                    isShowYourCurrentActivity,
                    isReceiveNewsAndUpdate,
                    isShowDayAndMonth,
                    isShowYearOfBirth,
                    ViewYourDetails,
                    PostMessagesOnYour,
                    ReceivedYourNewsFeed,
                    StartConversationsWith,
                    ViewYourIdentities
                ).enqueue(object : Callback<Map<String, Boolean>> {
                    override fun onResponse(
                        call: Call<Map<String, Boolean>>,
                        response: Response<Map<String, Boolean>>,
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.get("success") == true) {
                                api.getUsersProfileResponse(MyDataClass.api_key,
                                    MyDataClass.myUserId)
                                    .enqueue(object : Callback<ResponseDataClass> {
                                        override fun onResponse(
                                            call: Call<ResponseDataClass>,
                                            response: Response<ResponseDataClass>,
                                        ) {
                                            Log.d("TAG", "code${response.code()}")
                                            if (response.isSuccessful) {
                                                progressBar.dismiss()
                                                Toast.makeText(context,
                                                    "Your profile updated successfully",
                                                    Toast.LENGTH_LONG).show()
                                                MyDataClass.responseDataClass!!.me =
                                                    response.body()?.me!!
                                                MyDataClass.responseDataClass!!.user.show_dob_year =
                                                    response.body()?.me!!.show_dob_year!!
                                                MyDataClass.responseDataClass!!.user.show_dob_date =
                                                    response.body()?.me!!.show_dob_date!!
                                                MyDataClass.responseDataClass!!.user.receive_admin_email =
                                                    response.body()?.me!!.receive_admin_email!!
                                                MyDataClass.responseDataClass!!.user.activity_visible =
                                                    response.body()!!.me.activity_visible
                                                MyDataClass.responseDataClass!!.user.visible =
                                                    response.body()!!.me.visible
                                                MyDataClass.responseDataClass!!.user.allow_post_profile =
                                                    response.body()!!.me.allow_post_profile
                                                MyDataClass.responseDataClass!!.user.allow_receive_news_feed =
                                                    response.body()!!.me.allow_receive_news_feed
                                                MyDataClass.responseDataClass!!.user.allow_send_personal_conversation =
                                                    response.body()!!.me.allow_send_personal_conversation
                                                MyDataClass.responseDataClass!!.user.allow_view_identities =
                                                    response.body()!!.me.allow_view_identities
                                                MyDataClass.onBack()
                                            } else {
                                                progressBar.dismiss()
                                            }

                                        }

                                        override fun onFailure(
                                            call: Call<ResponseDataClass>,
                                            t: Throwable,
                                        ) {
                                            progressBar.dismiss()
                                        }
                                    })
                            } else {
                                progressBar.dismiss()
                            }
                        } else {
                            progressBar.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                        progressBar.dismiss()
                    }
                })
            }
        })
binding.privacyGoBackBtn.setOnClickListener { MyDataClass.onBack() }
        return binding.root
    }

    private fun initializeData() {
        MyDataClass.isGoProfile=false
        progressBar = ProgressDialog(context)
        if (MyDataClass.responseDataClass!!.user.visible == true) {
            binding.privacyShowYourOnlineStatusCheckbox.isChecked = true
        } else {
            binding.privacyShowYourOnlineStatusCheckbox.isChecked = false
        }
        if (MyDataClass.responseDataClass!!.user.activity_visible == true) {
            binding.privacyShowYourCurrentActivityCheckbox.isChecked = true
        } else {
            binding.privacyShowYourCurrentActivityCheckbox.isChecked  = false
        }
        if (MyDataClass.responseDataClass!!.user.receive_admin_email == true) {
            binding.privacyReceiveNewsAndUpdateEmailsCheckbox.isChecked = true
        } else {
            binding.privacyReceiveNewsAndUpdateEmailsCheckbox.isChecked  = false
        }

        if (MyDataClass.responseDataClass!!.user.show_dob_date == true) {
            binding.privacyShowDayAndMonthOfBirthCheckbox.isChecked = true
        } else {
            binding.privacyShowDayAndMonthOfBirthCheckbox.isChecked = false
        }
        if (MyDataClass.responseDataClass!!.user.receive_admin_email == true) {
            binding.privacyReceiveNewsAndUpdateEmailsCheckbox.isChecked = true
        } else {
            binding.privacyReceiveNewsAndUpdateEmailsCheckbox.isChecked=false
        }
            binding.privacyViewYourDetailsSpinner!!.setOnItemSelectedListener(this)
        val adapter1 =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, list1) }
        adapter1?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.privacyViewYourDetailsSpinner!!.setAdapter(adapter1)

        binding.privacyPostMessagesOnYourSpinner!!.setOnItemSelectedListener(this)
        val adapter2 =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, list2) }
        adapter2?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.privacyPostMessagesOnYourSpinner!!.setAdapter(adapter2)

        binding.privacyReceiveYourNewsFeedSpinner!!.setOnItemSelectedListener(this)
        val adapter3 =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, list3) }
        adapter3?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.privacyReceiveYourNewsFeedSpinner!!.setAdapter(adapter3)

        binding.privacyStartConversationsWithSpinner!!.setOnItemSelectedListener(this)
        val adapter4 =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, list4) }
        adapter4?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.privacyStartConversationsWithSpinner!!.setAdapter(adapter4)

        binding.privacyViewYourIdentitiesSpinner!!.setOnItemSelectedListener(this)
        val adapter5 =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, list5) }
        adapter5?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.privacyViewYourIdentitiesSpinner!!.setAdapter(adapter5)

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0?.id) {
            R.id.privacy_ViewYourDetails_spinner -> {
                ViewYourDetails = list1.get(p2)
            }
            R.id.privacy_PostMessagesOnYour_spinner -> {
                PostMessagesOnYour = list2.get(p2)
            }
            R.id.privacy_ReceiveYourNewsFeed_spinner -> {
                ReceivedYourNewsFeed = list3.get(p2)
            }
            R.id.privacy_StartConversationsWith_spinner -> {
                StartConversationsWith = list4.get(p2)
            }
            R.id.privacy_ViewYourIdentities_spinner -> {
                ViewYourIdentities = list5.get(p2)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}