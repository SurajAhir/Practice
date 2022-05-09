package com.example.fatchcurrentlocation.Fragments

import android.app.ProgressDialog
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
import com.example.fatchcurrentlocation.databinding.FragmentAccountDetailsBinding
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.util.*


class AccountDetails : Fragment() {

    lateinit var binding: FragmentAccountDetailsBinding
    lateinit var progressBar: ProgressDialog
    var dateOfBirthList: LinkedList<Int> = LinkedList()
    var isShowYearOfBirth: Boolean = false
    var isShowDayAndMonth: Boolean = false
    var isReceiveNewsAndUpdate: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentAccountDetailsBinding.inflate(layoutInflater, container, false)
        initializeData()
        binding.accountDetailsGoBackBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.onBack()
            }
        })
        binding.accountDetailsChangeUserEmailBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var transaction = MyDataClass.getTransaction()
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    ChangeUserEmail())
                transaction.addToBackStack(null).commit()
            }
        })
        binding.accountDetailsUserProfileImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(Intent.ACTION_PICK)
                intent.setType("image/*")
                startActivityForResult(intent, 1001)
            }
        })
        binding.accountDetailsUserProfileImageTv.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                var intent = Intent(Intent.ACTION_PICK)
                intent.setType("image/*")
                startActivityForResult(intent, 1001)
            }
        })
        binding.accountDetailsSelectDateOfBirthTv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var datePicker: DatePickerDialogFragment = DatePickerDialogFragment()
                activity?.supportFragmentManager?.let { datePicker.show(it, "date picker") }

            }
        })
        binding.accountDetailsShowYearOfBirthCheckbox.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                isShowYearOfBirth = binding.accountDetailsShowYearOfBirthCheckbox.isChecked
            }
        })
        binding.accountDetailsShowDayAndMonthOfBirthCheckbox.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                isShowDayAndMonth = binding.accountDetailsShowDayAndMonthOfBirthCheckbox.isChecked
            }
        })
        binding.accountDetailsReceiveNewsAndUpdateEmailsCheckbox.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                isReceiveNewsAndUpdate =
                    binding.accountDetailsReceiveNewsAndUpdateEmailsCheckbox.isChecked
            }
        })
        binding.accountDetailsSaveBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (dateOfBirthList.isEmpty()) {
                    Toast.makeText(context, "Please select your date of birth!!", Toast.LENGTH_LONG)
                        .show()
                    return
                }
                progressBar.show()
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                api.updateProfile(MyDataClass.api_key,
                    MyDataClass.myUserId,
                    isReceiveNewsAndUpdate,
                    isShowDayAndMonth,
                    isShowYearOfBirth,
                    binding.accountDetailsLocationEt.text.toString(),
                    binding.accountDetailsWebsiteEt.text.toString(),
                    binding.accountDetailsAboutMessageEt.text.toString(),
                    dateOfBirthList.get(0),
                    dateOfBirthList.get(1),
                    dateOfBirthList.get(2)).enqueue(object : Callback<Map<String, Boolean>> {
                    override fun onResponse(
                        call: Call<Map<String, Boolean>>,
                        response: Response<Map<String, Boolean>>,
                    ) {
                        Log.d("TAG",
                            "code ${response.code()} ${dateOfBirthList.get(0)} ${
                                dateOfBirthList.get(1)
                            } ${dateOfBirthList.get(2)} ${MyDataClass.api_key} ${MyDataClass.myUserId} " +
                                    "${isReceiveNewsAndUpdate} ${isShowYearOfBirth} ${isShowDayAndMonth} ${binding.accountDetailsLocationEt.text.toString()}" +
                                    "${binding.accountDetailsWebsiteEt.text.toString()}" +
                                    "${binding.accountDetailsAboutMessageEt.text.toString()}")
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
                                                MyDataClass.responseDataClass?.me =
                                                    response.body()?.me!!
                                                MyDataClass.responseDataClass?.me?.show_dob_year =
                                                    response.body()?.me!!.show_dob_year!!
                                                MyDataClass.responseDataClass?.me?.show_dob_date =
                                                    response.body()?.me!!.show_dob_date!!
                                                MyDataClass.responseDataClass?.me?.receive_admin_email =
                                                    response.body()?.me!!.receive_admin_email!!
                                                MyDataClass.responseDataClass?.me?.location =
                                                    response.body()?.me!!.location!!
                                                MyDataClass.responseDataClass?.me?.website =
                                                    response.body()?.me!!.website!!
                                                MyDataClass.responseDataClass?.me?.about =
                                                    response.body()?.me!!.about!!
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
        return binding.root
    }

    private fun initializeData() {
        MyDataClass.isGoProfile=false
        MyDataClass.datePick = ::pickDate
        progressBar = ProgressDialog(context)
        binding.accountDetailsUserNameTv.setText(MyDataClass.responseDataClass?.user?.username)
        binding.accountDetailsUserEmailTv.setText(MyDataClass.responseDataClass?.user?.email)
        if(MyDataClass.responseDataClass!!.user.avatar_urls.o==null){
            binding.accountDetailsUserProfileImageTv.visibility=View.VISIBLE
           binding.accountDetailsUserProfileImage.visibility=View.GONE
            binding.accountDetailsUserProfileImageTv.gravity= Gravity.CENTER
            binding.accountDetailsUserProfileImageTv.setText(MyDataClass.responseDataClass!!.user.username.get(0).toString())
        }else{
            Picasso.get().load(MyDataClass.responseDataClass!!.user.avatar_urls.o).placeholder(R.drawable.person)
                .into(binding.accountDetailsUserProfileImage)
        }
        binding.accountDetailsLocationEt.setText(MyDataClass.responseDataClass?.user?.location)
        binding.accountDetailsWebsiteEt.setText(MyDataClass.responseDataClass!!.user.website)
        binding.accountDetailsAboutMessageEt.setText(MyDataClass.responseDataClass!!.user.about)
        if (MyDataClass.responseDataClass!!.user.show_dob_year == true) {
            binding.accountDetailsShowYearOfBirthCheckbox.isChecked = true
        } else {
            binding.accountDetailsShowYearOfBirthCheckbox.isChecked = false
        }
        if (MyDataClass.responseDataClass!!.user.show_dob_date == true) {
            binding.accountDetailsShowDayAndMonthOfBirthCheckbox.isChecked = true
        } else {
            binding.accountDetailsShowDayAndMonthOfBirthCheckbox.isChecked = false
        }
        if (MyDataClass.responseDataClass!!.user.receive_admin_email == true) {
            binding.accountDetailsReceiveNewsAndUpdateEmailsCheckbox.isChecked = true
        } else {
            binding.accountDetailsReceiveNewsAndUpdateEmailsCheckbox.isChecked = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && data != null) {
            progressBar.show()
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
                                            Log.d("TAG", "${response.body()?.me?.avatar_urls?.o}")
                                            MyDataClass.responseDataClass!!.user.avatar_urls.o =
                                                response.body()?.me?.avatar_urls?.o!!
                                            binding.accountDetailsUserProfileImageTv.visibility=View.GONE
                                            binding.accountDetailsUserProfileImage.visibility=View.VISIBLE
                                            Picasso.get().load(response.body()!!.me.avatar_urls.o)
                                                .placeholder(R.drawable.ic_no_image)
                                                .into(binding.accountDetailsUserProfileImage)
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
        }

    }

    fun pickDate(list: LinkedList<Int>) {
        binding.accountDetailsSelectDateOfBirthTv.setText("${list.get(0)} ${list.get(1)} ${
            list.get(2)
        }")
        dateOfBirthList.add(list.get(0))
        dateOfBirthList.add(list.get(1))
        dateOfBirthList.add(list.get(2))
    }


}