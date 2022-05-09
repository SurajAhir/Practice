package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.AdaptersClasses.ShowAttachmentFilesAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.UserProfileAdapter
import com.example.fatchcurrentlocation.DataClasses.*
import com.example.fatchcurrentlocation.databinding.FragmentUserProfileBinding
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

class UserProfile(val list: LinkedList<Posts>, val position: Int) : Fragment(),
    AdapterView.OnItemSelectedListener {
    lateinit var binding: FragmentUserProfileBinding
    var list1: LinkedList<ProfilePosts> = LinkedList()
    var isChangedTextStyleBold: Boolean = false
    var isChangedTextStyleItalic: Boolean = false
    lateinit var progressBar: ProgressDialog
    var spinneList = arrayOf("Find", "Find all threads by ${list.get(position).User.username}")
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
    var isLinkOpened=false
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        initializeData()
        putDetailsOnFields()
        getResponseFromApi()
        binding.fragmentUserProfileStartConversationBtn.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                var transaction=MyDataClass.getTransaction()
                if(transaction!=null){
                    MyDataClass.homeFragmentContainerView.visibility=View.VISIBLE
                    MyDataClass.homeNestedScrollView.visibility=View.GONE
                    transaction.replace(R.id.home_fragment_containerViewForShowDetails,StartNewConversation(list.get(position).User,102))
                    transaction.addToBackStack(null).commit()
                }
            }
        })
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
                MyDataClass.userFragmentRequestCode=100
                MyDataClass.userFragmentImage=list.get(position).User?.avatar_urls?.o
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    ShowUserImage())
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
        binding.fragmentUserProfileAttachFileBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent = Intent.createChooser(intent, "Choose a file")
                startActivityForResult(intent, PICKFILE_REQUEST_CODE)
            }
        })
        binding.fragmentUserProfilePostCommentBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.fragmentUserProfileMessageEt.text.toString().isEmpty()) {
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
                            binding.fragmentUserProfileMessageEt.text.toString(),
                            gettedAttachmentKey)
                    } else {
                        var retrofit = RetrofitManager.getRetrofit1()
                        var api = retrofit.create(HitApi::class.java)
                        hitApi(api,binding.fragmentUserProfileMessageEt.text.toString(), "")
                    }

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
        binding.fragmentUserProfileBold.setOnClickListener { binding.fragmentUserProfileRichEditor.setBold() }
        binding.fragmentUserProfileItalic.setOnClickListener { binding.fragmentUserProfileRichEditor.setItalic() }
        binding.fragmentUserProfileUndo.setOnClickListener { binding.fragmentUserProfileRichEditor.undo() }
        binding.fragmentUserProfileRedo.setOnClickListener { binding.fragmentUserProfileRichEditor.redo() }
        binding.fragmentUserProfileUnderline.setOnClickListener { binding.fragmentUserProfileRichEditor.setUnderline() }
//        binding.fragmentUserProfileIndent.setOnClickListener { binding.fragmentUserProfileRichEditor.setIndent() }
//        binding.fragmentUserProfileOutdent.setOnClickListener { binding.fragmentUserProfileRichEditor.setOutdent() }
        binding.fragmentUserProfileAlignLeft.setOnClickListener { binding.fragmentUserProfileRichEditor.setAlignLeft() }
        binding.fragmentUserProfileAlignRight.setOnClickListener { binding.fragmentUserProfileRichEditor.setAlignRight() }
        binding.fragmentUserProfileAlignCenter.setOnClickListener { binding.fragmentUserProfileRichEditor.setAlignCenter() }
        binding.fragmentUserProfileInsertBullets.setOnClickListener { binding.fragmentUserProfileRichEditor.setBullets() }
        binding.fragmentUserProfileInsertNumbers.setOnClickListener { binding.fragmentUserProfileRichEditor.setNumbers() }
//        binding.fragmentUserProfileIndent.setOnClickListener { binding.fragmentUserProfileRichEditor.setIndent() }
//        binding.fragmentUserProfileOutdent.setOnClickListener { binding.fragmentUserProfileRichEditor.setOutdent() }
        binding.fragmentUserProfileRichEditor.setOnTextChangeListener(RichEditor.OnTextChangeListener { text -> //                Log.d("TAG", text);
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
                    .replace("[a href=","[URL=").replace("[/a]","[/URL]")
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
        binding.fragmentUserProfileInsertLink.setOnClickListener {
            if (isLinkOpened) {
                isLinkOpened = false
                binding.fragmentUserProfileInsertLinkLayout.visibility = View.GONE
            } else {
                isLinkOpened = true
                binding.fragmentUserProfileInsertLinkLayout.visibility = View.VISIBLE
            }
        }
        binding.fragmentUserProfileInsertLinkBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.fragmentUserProfileUrlEt.text.isEmpty()) {
                    binding.fragmentUserProfileUrlEt.setError("Please enter a valid URL")
                    binding.fragmentUserProfileUrlEt.focusable = View.FOCUSABLE
                } else if (binding.fragmentUserProfileTextEt.text.toString().isEmpty()) {
                    binding.fragmentUserProfileTextEt.setError("Please enter a valid text")
                    binding.fragmentUserProfileTextEt.focusable = View.FOCUSABLE
                } else {
                    binding.fragmentUserProfileRichEditor.insertLink(binding.fragmentUserProfileUrlEt.text.toString(),
                        binding.fragmentUserProfileTextEt.text.toString())
                    binding.fragmentUserProfileUrlEt.setText("")
                    binding.fragmentUserProfileTextEt.setText("")
                    binding.fragmentUserProfileInsertLinkLayout.visibility = View.GONE
                }
            }
        })
        binding.fragmentUserProfileFontSizeBtn.setOnClickListener(object :
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
                    binding.fragmentUserProfileRichEditor.setFontSize(1)
                    Log.d("TAG", "clicked$i")
                }
                1 -> {
                    binding.fragmentUserProfileRichEditor.setFontSize(2)
                    Log.d("TAG", "clicked$i")
                }
                2 -> {
                    binding.fragmentUserProfileRichEditor.setFontSize(3)
                    Log.d("TAG", "clicked$i")
                }
                3 -> {
                    binding.fragmentUserProfileRichEditor.setFontSize(4)
                    Log.d("TAG", "clicked$i")
                }
                4 -> {
                    binding.fragmentUserProfileRichEditor.setFontSize(5)
                    Log.d("TAG", "clicked$i")
                }
                5 -> {
                    binding.fragmentUserProfileRichEditor.setFontSize(6)
                    Log.d("TAG", "clicked$i")
                }
                6 -> {
                    binding.fragmentUserProfileRichEditor.setFontSize(7)
                    Log.d("TAG", "clicked$i")
                }
            }
        }
        return binding.root
    }

    private fun initializeData() {
        alertDialog = context?.let { AlertDialog.Builder(it) }
        binding.fragmentUserProfileRichEditor.setEditorHeight(100)
        binding.fragmentUserProfileRichEditor.setEditorFontSize(15)
        binding.fragmentUserProfileRichEditor.setEditorFontColor(Color.BLACK)
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        binding.fragmentUserProfileRichEditor.setPadding(4,4,4,4)
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        binding.fragmentUserProfileRichEditor.setPlaceholder("Write reply...")
        MyDataClass.attachmentFileListAttachmentId.clear()
        MyDataClass.attachmentFileListItem.clear()
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
                    binding.fragmentUserProfileProgressBar.visibility = View.GONE
                    Log.d("TAG", "profileId ${listdata.get(0).profile_user_id}")
                    MyDataClass.attachmentFileListAttachmentId.clear()
                    MyDataClass.attachmentFileListItem.clear()
                    binding.fragmentUserProfileNoMessageTv.visibility = View.GONE
                    listdata?.let { list1.addAll(it) }
                    binding.fragmentUserProfileProgressBar.visibility = View.GONE
                    binding.fragmentUserProfileRecyclerView.adapter =
                        context?.let {
                            UserProfileAdapter(it,
                                list1,
                                list.get(position).user_id,
                                activity,
                                list.get(position).thread_id)
                        }
                    binding.fragmentUserProfileRecyclerView.layoutManager =
                        LinearLayoutManager(context)
                } else {
                    progressBar.dismiss()
                    binding.fragmentUserProfileProgressBar.visibility = View.GONE
                    binding.fragmentUserProfileRecyclerView.visibility = View.GONE
                    binding.fragmentUserProfileProgressBar.visibility = View.GONE
                    binding.fragmentUserProfileNoMessageTv.append("${list.get(position).User.username} profile yet.")
                }

            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                progressBar.dismiss()
                binding.fragmentUserProfileProgressBar.visibility = View.GONE
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun putDetailsOnFields() {
        if (list.get(position).User.avatar_urls.o == null) {
            binding.fragmentUserProfileUserProfileImageTv.visibility = View.VISIBLE
            binding.fragmentUserProfileUserProfileImage.visibility = View.GONE
            binding.fragmentUserProfileUserProfileImageTv.gravity = Gravity.CENTER
            binding.fragmentUserProfileUserProfileImageTv.setText(MyDataClass.responseDataClass!!.user.username.get(
                0).toString())
        } else {
            Picasso.get().load(list.get(position).User.avatar_urls.o).placeholder(R.drawable.person)
                .into(binding.fragmentUserProfileUserProfileImage)
        }
        binding.fragmentUserProfileUserNameTv.setText(list.get(position).User.username)
        binding.fragmentUserProfileMemberTv.setText(list.get(position).User.user_title)
        var registerDate = Date((list.get(position).User.register_date as Long) * 1000)
        var simpleFormat1 = SimpleDateFormat("dd yyyy")
        binding.fragmentUserProfileJoinedDateTv.setText("${DateFormatSymbols().getShortMonths()[registerDate.month]} ${
            simpleFormat1.format(registerDate)
        }")


        var lastActivityDate = Date((list.get(position).User.last_activity as Long) * 1000)
        convertTimeIntoSimpleMinuts(lastActivityDate)
        binding.fragmentUserProfileMessagesTv.setText(list.get(position).User.message_count.toString())
        binding.fragmentUserProfileReactionScoreTv.setText(list.get(position).reaction_score.toString())
        binding.fragmentUserProfilePointsTv.setText(list.get(position).User.trophy_points.toString())

    }

    private fun convertTimeIntoSimpleMinuts(lastActivityDate: Date) {
        var currentTime = Date(System.currentTimeMillis())
        Log.d("TAG", "year ${currentTime.month} and ${lastActivityDate.month}")
        if (currentTime.year == lastActivityDate.year) {
            if (lastActivityDate.month == currentTime.month) {
                if (lastActivityDate.date == currentTime.date) {
                    if (lastActivityDate.hours == currentTime.hours) {
                        if (currentTime.minutes - lastActivityDate.minutes < 2) {
                            binding.fragmentUserProfileLastSeenTv.setText("a moment ago")
                        } else {
                            binding.fragmentUserProfileLastSeenTv.setText("${currentTime.minutes - lastActivityDate.minutes} minutes ago")
                        }
                    } else if (lastActivityDate.hours < currentTime.hours) {
                        var timeInMinuts =
                            (currentTime.hours - lastActivityDate.hours) * 60 + currentTime.minutes
                        if (timeInMinuts - lastActivityDate.minutes < 60) {
                            binding.fragmentUserProfileLastSeenTv.setText("${Math.abs(timeInMinuts - lastActivityDate.minutes)} minutes ago")
                        } else {
                            binding.fragmentUserProfileLastSeenTv.setText("${currentTime.hours - lastActivityDate.hours} hour ago")
                        }
                    }
                } else {
                    if (currentTime.date - lastActivityDate.date < 2) {
                        binding.fragmentUserProfileLastSeenTv.setText("${currentTime.date - lastActivityDate.date} day ago")
                    } else {
                        binding.fragmentUserProfileLastSeenTv.setText("${currentTime.date - lastActivityDate.date} days ago")
                    }
                }
            } else {
                if (currentTime.month - lastActivityDate.month < 2) {
                    binding.fragmentUserProfileLastSeenTv.setText("${currentTime.month - lastActivityDate.month} month ago")
                } else {
                    binding.fragmentUserProfileLastSeenTv.setText("${currentTime.month - lastActivityDate.month} months ago")
                }
            }
        } else {
            if ((currentTime.year - 100) - (lastActivityDate.year - 100) < 2) {
                binding.fragmentUserProfileLastSeenTv.setText("${(currentTime.year - 100) - (lastActivityDate.year - 100)} year ago")
            } else {
                binding.fragmentUserProfileLastSeenTv.setText("${(currentTime.year - 100) - (lastActivityDate.year - 100)} years ago")
            }
        }
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
                FindAllThreadsBySomeName(list.get(position).user_id))
            transaction.addToBackStack(null).commit()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun funAdapter() {
        if (MyDataClass.attachmentFileListItem.size > 0) {
            binding.fragmentUserProfileShowAttachmentsRecyclerView.visibility = View.VISIBLE
            binding.fragmentUserProfileShowAttachmentsRecyclerView.adapter =
                context?.let {
                    ShowAttachmentFilesAdapter(it,
                        MyDataClass.attachmentFileListItem,
                        MyDataClass.attachmentFileListAttachmentId)
                }
            binding.fragmentUserProfileShowAttachmentsRecyclerView.layoutManager =
                LinearLayoutManager(context)
            binding.fragmentUserProfileShowAttachmentsRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            binding.fragmentUserProfileShowAttachmentsRecyclerView.visibility = View.GONE
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
            MyDataClass.myUserId, fileToUpload, attachmentKey
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
            list.get(position).user_id,
            "profile_post")
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>,
                ) {
                    Log.d("TAG",
                        "response ${response.code()} ${list.get(position).user_id} ${
                            list.get(position).thread_id
                        }")
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
            list.get(position).user_id, (message + attachmentFileString), gettedAttachmentKey)
            .enqueue(object : Callback<ResponseDataClass> {
                override fun onResponse(
                    call: Call<ResponseDataClass>,
                    response: Response<ResponseDataClass>,
                ) {
                    Log.d("TAG",
                        "${response.code()} ${message}  ${gettedAttachmentKey}  ${attachmentFileString} ${
                            list.get(position).user_id
                        }")
                    if (response.body()?.success == true) {
                        progressBar.dismiss()
                        Log.d("TAG", "respon ${response.code()}")
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