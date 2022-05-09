package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.AdaptersClasses.MyProfileAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.ShowAttachmentFilesAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ProfilePosts
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.databinding.FragmentMyProfileBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import jp.wasabeef.richeditor.RichEditor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MyProfile(val responseDataClass: ResponseDataClass) : Fragment(),
    AdapterView.OnItemSelectedListener {
    lateinit var binding: FragmentMyProfileBinding
    var list1: LinkedList<ProfilePosts> = LinkedList()
    var isChangedTextStyleBold: Boolean = false
    var isChangedTextStyleItalic: Boolean = false
    var spinneList = arrayOf("Find", "Find all threads by ${responseDataClass.user.username}")
    lateinit var progressBar: ProgressDialog
    var spinner: Spinner? = null
    var attachmentId: Int = 0
    var PICKFILE_REQUEST_CODE = 1001
    var isAttachedFile: Boolean = false
    var gettedAttachmentKey: String = ""
    var attachmentFileString = ""
    var isGeneratedAttachmentKey = false
    lateinit var attachmentRequestBodyKey: RequestBody
    var gettedTextFromEditor: String = ""
    var alertDialog: AlertDialog.Builder? = null
    var alertDialog1: AlertDialog? = null
    var centername = ""
    var rightname = ""
    var sizes = arrayOf("9", "10", "12", "15", "18", "22", "26")
    var isLinkOpened = false

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
                MyDataClass.userFragmentImage=responseDataClass.user?.avatar_urls?.o
                MyDataClass.userFragmentRequestCode=100
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    ShowUserImage())
                transaction.addToBackStack(null).commit()
            }
        })
        binding.fragmentMyProfileAttachFileBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Dexter.withActivity(context as Activity?)
                    .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            var intent = Intent(Intent.ACTION_PICK)
                            intent.setType("image/*")
                            startActivityForResult(intent, 1001)
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
        binding.fragmentMyProfileWriteSomethingBtn.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.fragmentMyProfileWriteSomethingBtn.visibility = View.GONE
                binding.fragmentMyProfileWriteSomethingLayoutToShow.visibility = View.VISIBLE
            }
        })

        binding.fragmentMyProfilePostCommentBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.fragmentMyProfileWriteMessage.text.toString().isEmpty()) {
                    return
                } else {
                    progressBar.show()
                    if (isAttachedFile) {
                        isAttachedFile = false
                        var retrofit = RetrofitManager.getRetrofit1()
                        var api = retrofit.create(HitApi::class.java)
                        if (MyDataClass.attachmentFileListAttachmentId.size > 0) {
                            for (i in MyDataClass.attachmentFileListAttachmentId) {
                                attachmentFileString = attachmentFileString +
                                        """[ATTACH type="full"]${i}[/ATTACH] """
                            }
                        }
                        hitApi(api,
                            binding.fragmentMyProfileWriteMessage.text.toString(),
                            gettedAttachmentKey)
                    } else {
                        var retrofit = RetrofitManager.getRetrofit1()
                        var api = retrofit.create(HitApi::class.java)
                        hitApi(api, binding.fragmentMyProfileWriteMessage.text.toString(), "")
                    }
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
        binding.fragmentMyProfileBold.setOnClickListener { binding.fragmentMyProfileRichEditor.setBold() }
        binding.fragmentMyProfileItalic.setOnClickListener { binding.fragmentMyProfileRichEditor.setItalic() }
        binding.fragmentMyProfileUndo.setOnClickListener { binding.fragmentMyProfileRichEditor.undo() }
        binding.fragmentMyProfileRedo.setOnClickListener { binding.fragmentMyProfileRichEditor.redo() }
        binding.fragmentMyProfileUnderline.setOnClickListener { binding.fragmentMyProfileRichEditor.setUnderline() }
//        binding.fragmentMyProfileIndent.setOnClickListener { binding.fragmentMyProfileRichEditor.setIndent() }
//        binding.fragmentMyProfileOutdent.setOnClickListener { binding.fragmentMyProfileRichEditor.setOutdent() }
        binding.fragmentMyProfileAlignLeft.setOnClickListener { binding.fragmentMyProfileRichEditor.setAlignLeft() }
        binding.fragmentMyProfileAlignRight.setOnClickListener { binding.fragmentMyProfileRichEditor.setAlignRight() }
        binding.fragmentMyProfileAlignCenter.setOnClickListener { binding.fragmentMyProfileRichEditor.setAlignCenter() }
        binding.fragmentMyProfileInsertBullets.setOnClickListener { binding.fragmentMyProfileRichEditor.setBullets() }
        binding.fragmentMyProfileInsertNumbers.setOnClickListener { binding.fragmentMyProfileRichEditor.setNumbers() }
//        binding.fragmentMyProfileIndent.setOnClickListener { binding.fragmentMyProfileRichEditor.setIndent() }
//        binding.fragmentMyProfileOutdent.setOnClickListener { binding.fragmentMyProfileRichEditor.setOutdent() }
        binding.fragmentMyProfileRichEditor.setOnTextChangeListener(RichEditor.OnTextChangeListener { text -> //                Log.d("TAG", text);
            val center = ""
            var right = ""
            gettedTextFromEditor = text.replace("<", "[")
            gettedTextFromEditor = gettedTextFromEditor.replace(">", "]")
            gettedTextFromEditor =
                gettedTextFromEditor.replace("&nbsp;", "").replace("[/li]", "")
                    .replace("[ol", "[List=1")
                    .replace("[li", "[*")
                    .replace("/ol", "/List").replace(" style=\"\"", "").replace(" style=\"\"", "")
                    .replace("[br]", "").replace("[i style=\"font-weight: bold;\"]", "[i]")
                    .replace("[ul", "[List").replace("[/ul", "[/List")
                    .replace("[span style=\"font-size: 22px;\"]", "[SIZE=6]")
                    .replace("[/span]", "[/SIZE]").replace("[font size=\"7\"]", "[SIZE=26]")
                    .replace("[font size=\"6\"]", "[SIZE=22]")
                    .replace("[font size=\"5\"]", "[SIZE=18]")
                    .replace("[font size=\"4\"]", "[SIZE=15]")
                    .replace("[font size=\"3\"]", "[SIZE=12]")
                    .replace("[font size=\"2\"]", "[SIZE=10]")
                    .replace("[font size=\"1\"]", "[SIZE=9]").replace("[/font]", "[/SIZE]")
                    .replace("[a href=", "[URL=").replace("[/a]", "[/URL]")
                    .replace("[blockquote style=\"margin: 0 0 0 40px; border: none; padding: 0px;\"]", "[INDENT=2]")
                    .replace("[/blockquote]", "[/INDENT]").replace("[span style=\"font-size: 15px;\"]","[SIZE=12]")
            if (gettedTextFromEditor.contains("[div style=\"text-align: center;\"]")) {
                while (gettedTextFromEditor.contains("[div style=\"text-align: center;\"]")) {
                    centername = gettedTextFromEditor
                    centername = centername.substring(centername.indexOf("center;\"]") + 9)
                    if (centername.contains("[/div]")) {
                        centername = centername.substring(0, centername.indexOf("[/div]"))
                        gettedTextFromEditor =
                            gettedTextFromEditor.replace("[div style=\"text-align: center;\"]$centername",
                                "[CENTER]$centername")
                                .replace(centername + "[/div]", centername + "[/CENTER]")
                                .replace("[CENTER][/CENTER]", "")
                    } else {
                        Log.d("TAG", "center $centername")
                        if (gettedTextFromEditor.contains("[div style=\"text-align: center;\"]")) {
                            centername = centername.substring(0, centername.indexOf("[/RIGHT]"))
                            gettedTextFromEditor =
                                gettedTextFromEditor.replace("[div style=\"text-align: center;\"]$centername",
                                    "[CENTER]$centername")
                                    .replace(centername + "[/RIGHT]", centername + "[/CENTER]")
                                    .replace("[RIGHT][/RIGHT]", "")
                        } else {
                            break
                        }
                        break
                    }
                }
            }
            if (gettedTextFromEditor.contains("[div style=\"text-align: right;\"]")) {
                while (gettedTextFromEditor.contains("[div style=\"text-align: right;\"]")) {
                    rightname = gettedTextFromEditor
                    rightname = rightname.substring(rightname.indexOf("right;\"]") + 8)
                    if (rightname.contains("[/div]")) {
                        rightname = rightname.substring(0, rightname.indexOf("[/div]"))
                        right = "[div style=\"text-align: right;\"]$rightname[/div]"
                        gettedTextFromEditor =
                            gettedTextFromEditor.replace("[div style=\"text-align: right;\"]$rightname",
                                "[RIGHT]$rightname")
                                .replace(rightname + "[/div]", rightname + "[/RIGHT]")
                                .replace("[RIGHT][/RIGHT]", "")
                    } else {
                        Log.d("TAG", "right $rightname")
                        if (gettedTextFromEditor.contains("[div style=\"text-align: right;\"]")) {
                            rightname = rightname.substring(0, rightname.indexOf("[/CENTER]"))
                            gettedTextFromEditor =
                                gettedTextFromEditor.replace("[div style=\"text-align: right;\"]$rightname",
                                    "[RIGHT]$rightname")
                                    .replace(rightname + "[/CENTER]", rightname + "[/RIGHT]")
                                    .replace("[RIGHT][/RIGHT]", "")
                        } else {
                            break
                        }
                    }
                    //                        gettedText = gettedText + "[RIGHT]" + rightname + "[/RIGHT]";
                }
            }
            if (gettedTextFromEditor.contains("[div style=\"text-align: left;\"]")) {
                gettedTextFromEditor =
                    gettedTextFromEditor.replace("[div style=\"text-align: left;\"]", "")
                        .replace("[/div]", "")
            }
            Log.d("TAG", gettedTextFromEditor)
        })
        binding.fragmentMyProfileFontSizeBtn.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                alertDialog1 = alertDialog!!.create()
                alertDialog1!!.show()
                alertDialog1!!.window!!.setLayout(300, 600)
                alertDialog1!!.show()
            }
        })
        alertDialog!!.setItems(sizes
        ) { dialogInterface, i ->
            when (i) {
                0 -> {
                    binding.fragmentMyProfileRichEditor.setFontSize(1)
                    Log.d("TAG", "clicked$i")
                }
                1 -> {
                    binding.fragmentMyProfileRichEditor.setFontSize(2)
                    Log.d("TAG", "clicked$i")
                }
                2 -> {
                    binding.fragmentMyProfileRichEditor.setFontSize(3)
                    Log.d("TAG", "clicked$i")
                }
                3 -> {
                    binding.fragmentMyProfileRichEditor.setFontSize(4)
                    Log.d("TAG", "clicked$i")
                }
                4 -> {
                    binding.fragmentMyProfileRichEditor.setFontSize(5)
                    Log.d("TAG", "clicked$i")
                }
                5 -> {
                    binding.fragmentMyProfileRichEditor.setFontSize(6)
                    Log.d("TAG", "clicked$i")
                }
                6 -> {
                    binding.fragmentMyProfileRichEditor.setFontSize(7)
                    Log.d("TAG", "clicked$i")
                }
            }
        }
        binding.fragmentMyProfileInsertLink.setOnClickListener {
            if (isLinkOpened) {
                isLinkOpened = false
                binding.fragmentMyProfileInsertLinkLayout.visibility = View.GONE
            } else {
                isLinkOpened = true
                binding.fragmentMyProfileInsertLinkLayout.visibility = View.VISIBLE
            }
        }
        binding.fragmentMyProfileInsertLinkBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.fragmentMyProfileUrlEt.text.isEmpty()) {
                    binding.fragmentMyProfileUrlEt.setError("Please enter a valid URL")
                    binding.fragmentMyProfileUrlEt.focusable = View.FOCUSABLE
                } else if (binding.fragmentMyProfileTextEt.text.toString().isEmpty()) {
                    binding.fragmentMyProfileTextEt.setError("Please enter a valid text")
                    binding.fragmentMyProfileTextEt.focusable = View.FOCUSABLE
                } else {
                    binding.fragmentMyProfileRichEditor.insertLink(binding.fragmentMyProfileUrlEt.text.toString(),
                        binding.fragmentMyProfileTextEt.text.toString())
                    binding.fragmentMyProfileUrlEt.setText("")
                    binding.fragmentMyProfileTextEt.setText("")
                    binding.fragmentMyProfileInsertLinkLayout.visibility = View.GONE
                }
            }
        })
        return binding.root
    }

    private fun initializeData() {
        MyDataClass.isGoProfile=false
        alertDialog = context?.let { AlertDialog.Builder(it) }
        binding.fragmentMyProfileRichEditor.setEditorHeight(100)
        binding.fragmentMyProfileRichEditor.focusEditor()
        binding.fragmentMyProfileRichEditor.fitsSystemWindows
        var setting=binding.fragmentMyProfileRichEditor.settings
        setting.setEnableSmoothTransition(true)
        binding.fragmentMyProfileRichEditor.setEditorFontSize(15)
        binding.fragmentMyProfileRichEditor.setEditorFontColor(Color.BLACK)
        binding.fragmentMyProfileRichEditor.setPadding(4, 4, 4, 4)
        binding.fragmentMyProfileRichEditor.setPlaceholder("Update your status...")
        MyDataClass.attachmentFileListItem.clear()
        MyDataClass.attachmentFileListAttachmentId.clear()
        progressBar = ProgressDialog(context)
    }

    private fun convertTimeIntoSimpleMinuts(lastActivityDate: Date) {
        var currentTime = Date(System.currentTimeMillis())
        Log.d("TAG", "year ${currentTime.month} and ${lastActivityDate.month}")
        if (currentTime.year == lastActivityDate.year) {
            if (lastActivityDate.month == currentTime.month) {
                if (lastActivityDate.date == currentTime.date) {
                    if (lastActivityDate.hours == currentTime.hours) {
                        if (currentTime.minutes - lastActivityDate.minutes < 2) {
                            binding.fragmentMyProfileLastSeenTv.setText("a moment ago")
                        } else {
                            binding.fragmentMyProfileLastSeenTv.setText("${currentTime.minutes - lastActivityDate.minutes} minutes ago")
                        }
                    } else if (lastActivityDate.hours < currentTime.hours) {
                        var timeInMinuts =
                            (currentTime.hours - lastActivityDate.hours) * 60 + currentTime.minutes
                        if (timeInMinuts - lastActivityDate.minutes < 60) {
                            binding.fragmentMyProfileLastSeenTv.setText("${Math.abs(timeInMinuts - lastActivityDate.minutes)} minutes ago")
                        } else {
                            binding.fragmentMyProfileLastSeenTv.setText("${currentTime.hours - lastActivityDate.hours} hour ago")
                        }
                    }
                } else {
                    if (currentTime.date - lastActivityDate.date < 2) {
                        binding.fragmentMyProfileLastSeenTv.setText("${currentTime.date - lastActivityDate.date} day ago")
                    } else {
                        binding.fragmentMyProfileLastSeenTv.setText("${currentTime.date - lastActivityDate.date} days ago")
                    }
                }
            } else {
                if (currentTime.month - lastActivityDate.month < 2) {
                    binding.fragmentMyProfileLastSeenTv.setText("${currentTime.month - lastActivityDate.month} month ago")
                } else {
                    binding.fragmentMyProfileLastSeenTv.setText("${currentTime.month - lastActivityDate.month} months ago")
                }
            }
        } else {
            if ((currentTime.year - 100) - (lastActivityDate.year - 100) < 2) {
                binding.fragmentMyProfileLastSeenTv.setText("${(currentTime.year - 100) - (lastActivityDate.year - 100)} year ago")
            } else {
                binding.fragmentMyProfileLastSeenTv.setText("${(currentTime.year - 100) - (lastActivityDate.year - 100)} years ago")
            }
        }
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
                    binding.fragmentMyProfileProgressBar.visibility = View.GONE
                    binding.fragmentMyProfileNoMessageTv.visibility = View.GONE
                    listdata?.let { list1.addAll(it) }
                    binding.fragmentMyProfileProgressBar.visibility = View.GONE
                    binding.fragmentMyProfileRecyclerView.adapter =
                        context?.let {
                            MyProfileAdapter(it,
                                list1,
                                responseDataClass.user.user_id,
                                activity)
                        }
                    binding.fragmentMyProfileRecyclerView.layoutManager =
                        LinearLayoutManager(context)
                } else {
                    progressBar.dismiss()
                    binding.fragmentMyProfileProgressBar.visibility = View.GONE
                    binding.fragmentMyProfileRecyclerView.visibility = View.GONE
                    binding.fragmentMyProfileProgressBar.visibility = View.GONE
                    binding.fragmentMyProfileNoMessageTv.append("${responseDataClass.user.username} profile yet.")
                }

            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                progressBar.dismiss()
                binding.fragmentMyProfileProgressBar.visibility = View.GONE
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun putDetailsOnFields() {
        if (responseDataClass.user.avatar_urls.o == null) {
            binding.fragmentMyProfileUserProfileImageTv.visibility = View.VISIBLE
            binding.fragmentMyProfileUserProfileImage.visibility = View.GONE
            binding.fragmentMyProfileUserProfileImageTv.gravity = Gravity.CENTER
            binding.fragmentMyProfileUserProfileImageTv.setText(MyDataClass.responseDataClass?.user!!.username.get(
                0).toString())
        } else {
            Picasso.get().load(responseDataClass.user.avatar_urls.o).placeholder(R.drawable.person)
                .into(binding.fragmentMyProfileUserProfileImage)
        }
        binding.fragmentMyProfileUserNameTv.setText(responseDataClass.user.username)
        binding.fragmentMyProfileMemberTv.setText(responseDataClass.user.user_title)
        var registerDate = Date((responseDataClass.user.register_date as Long) * 1000)
        var simpleFormat1 = SimpleDateFormat("dd yyyy")
        binding.fragmentMyProfileJoinedDateTv.setText("${DateFormatSymbols().getShortMonths()[registerDate.month]} ${
            simpleFormat1.format(registerDate)
        }")


        var lastActivityDate = Date((responseDataClass.user.last_activity as Long) * 1000)
        convertTimeIntoSimpleMinuts(lastActivityDate)
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
        if (spinneList.get(p2).equals("Find")) {
            return
        } else if (spinneList.get(p2).startsWith("Find all threads by")) {
            var transaction = MyDataClass.getTransaction()
            MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
            MyDataClass.homeNestedScrollView.visibility = View.GONE
            transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                FindAllThreadsBySomeName(
                    MyDataClass.myUserId))
            transaction.addToBackStack(null).commit()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun funAdapter() {
        if (MyDataClass.attachmentFileListItem.size > 0) {
            binding.fragmentMyProfileShowAttachmentsRecyclerView.visibility = View.VISIBLE
            binding.fragmentMyProfileShowAttachmentsRecyclerView.adapter =
                context?.let {
                    ShowAttachmentFilesAdapter(it,
                        MyDataClass.attachmentFileListItem,
                        MyDataClass.attachmentFileListAttachmentId)
                }
            binding.fragmentMyProfileShowAttachmentsRecyclerView.layoutManager =
                LinearLayoutManager(context)
            binding.fragmentMyProfileShowAttachmentsRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            binding.fragmentMyProfileShowAttachmentsRecyclerView.visibility = View.GONE
        }
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressBar.show()
        if (requestCode == PICKFILE_REQUEST_CODE) {
            var uri1 = FileUtils.getPath(context, data?.data)
            Log.d("TAG", uri1)
            var file: File = File(uri1)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val fileToUpload =
                MultipartBody.Part.createFormData("attachment", file.getName(), requestBody)
            var retrofit: Retrofit = RetrofitManager.getRetrofit1()
            var api: HitApi = retrofit.create(HitApi::class.java)
            if (isGeneratedAttachmentKey) {
                postAttachmentFile(fileToUpload, attachmentRequestBodyKey, api)
            } else {
                generateAttachmentKey(api, fileToUpload)
            }

        }

    }

    private fun postAttachmentFile(
        fileToUpload: MultipartBody.Part,
        attachmentKey: RequestBody,
        api: HitApi,
    ) {
        api.postAttachmentFile("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
            responseDataClass.user.user_id, fileToUpload, attachmentKey
        ).enqueue(object : Callback<ResponseThread> {
            override fun onResponse(
                call: Call<ResponseThread>,
                response: Response<ResponseThread>,
            ) {
                if (response.isSuccessful) {
                    progressBar.dismiss()
                    attachmentId = response.body()?.attachment?.attachment_id!!
                    MyDataClass.attachmentFileListItem.add(response.body()!!.attachment.filename)
                    MyDataClass.attachmentFileListAttachmentId.add(attachmentId)
                    MyDataClass.funAdapter = ::funAdapter
                    funAdapter()
                    isAttachedFile = true
                } else {
                    progressBar.dismiss()
                }

            }

            override fun onFailure(
                call: Call<ResponseThread>,
                t: Throwable,
            ) {
                progressBar.dismiss()
                Log.d("TAG", t.localizedMessage)
            }
        })
    }

    private fun generateAttachmentKey(api: HitApi, fileToUpload: MultipartBody.Part) {
        isGeneratedAttachmentKey = true
        api.generateAttachmentKeyForUserProfile("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
            MyDataClass.myUserId,
            responseDataClass.user.user_id,
            "profile_post")
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>,
                ) {
                    Log.d("TAG", "coded ${response.code()}")
                    if (response.isSuccessful) {
                        gettedAttachmentKey = response.body()?.get("key").toString()
                        attachmentRequestBodyKey =
                            RequestBody.create(MediaType.parse("multipart/form-data"),
                                gettedAttachmentKey)
                        postAttachmentFile(fileToUpload, attachmentRequestBodyKey, api)
                    } else {
                        progressBar.dismiss()
                    }

                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Log.d("TAG", "error ${t.localizedMessage}")
                    progressBar.dismiss()
                }
            })
    }

    private fun hitApi(api: HitApi, message: String, gettedAttachmentKey: String) {
        api.getReaponseOfProfilePostsOfMessages(MyDataClass.api_key,
            MyDataClass.myUserId,
            responseDataClass.user.user_id, (message + attachmentFileString), gettedAttachmentKey)
            .enqueue(object : Callback<ResponseDataClass> {
                override fun onResponse(
                    call: Call<ResponseDataClass>,
                    response: Response<ResponseDataClass>,
                ) {
                    Log.d("TAG", "${response.code()}")
                    if (response.body()?.success == true) {
                        progressBar.dismiss()
                        Log.d("TAG", "respon ${response.code()}")
                        MyDataClass.attachmentFileListAttachmentId.clear()
                        MyDataClass.attachmentFileListItem.clear()
                        binding.fragmentMyProfileRichEditor.clearFocusEditor()
                        binding.fragmentMyProfileWriteSomethingLayoutToShow.visibility=View.GONE
                        binding.fragmentMyProfileWriteSomethingBtn.visibility=View.VISIBLE
                        funAdapter()
                        list1.clear()
                       getResponseFromApi()
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