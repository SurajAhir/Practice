package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.ShowAttachmentFilesAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.ShowSelectedUsersNameAdapter
import com.example.fatchcurrentlocation.DataClasses.*
import com.example.fatchcurrentlocation.FileUtils
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentStartNewConversationBinding
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import jp.wasabeef.richeditor.RichEditor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.util.*

class StartNewConversation() : Fragment() {
    constructor(userObject: User, requestCode: Int) : this() {
        this.userObject = userObject
        this.userRequestCode = requestCode
    }

    var userObject: User? = null
    var userRequestCode = 0
    lateinit var binding: FragmentStartNewConversationBinding
    var recommendedAndExactLinkedList: LinkedList<User> = LinkedList()
    var selectedUserLinkedList: LinkedList<User> = LinkedList()
    var attachmentId: Int = 0
    lateinit var progressBar: ProgressDialog
    var PICKFILE_REQUEST_CODE = 1001
    var isAttachedFile: Boolean = false
    var gettedAttachmentKey: String = ""
    var attachmentFileString = ""
    var isGeneratedAttachmentKey = false
    var conversation_open: Boolean = false
    var open_invite: Boolean = false
    lateinit var attachmentRequestBodyKey: RequestBody
    var gettedTextFromEditor: String = ""
    var alertDialog: AlertDialog.Builder? = null
    var alertDialog1: AlertDialog? = null
    var centername = ""
    var rightname = ""
    var sizes = arrayOf("9", "10", "12", "15", "18", "22", "26")
    var isLinkOpened=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentStartNewConversationBinding.inflate(layoutInflater, container, false)
        initializeData()
        checkIsUserRequestCodeMatch()
        binding.startNewConversationAllowAnyoneCheckbox.setOnClickListener {
            open_invite = binding.startNewConversationAllowAnyoneCheckbox.isChecked
        }
        binding.startNewConversationLockConversationCheckbox.setOnClickListener {
            conversation_open = binding.startNewConversationLockConversationCheckbox.isChecked
        }
        binding.startNewConversationFindRecipientEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                if (p0.toString().length > 2) {
                    api.findUserName(MyDataClass.api_key, MyDataClass.myUserId, p0.toString())
                        .enqueue(object : Callback<ResponseThread> {
                            override fun onResponse(
                                call: Call<ResponseThread>,
                                response: Response<ResponseThread>,
                            ) {
                                if (response.isSuccessful) {
                                    var recommendesList = response.body()?.recommendations
                                    var exactList = response.body()?.exact
                                    if (!recommendesList?.isEmpty()!!) {
                                        recommendedAndExactLinkedList.clear()
                                        recommendedAndExactLinkedList.addAll(recommendesList)
                                        binding.startNewConversationListView.visibility =
                                            View.VISIBLE
                                        binding.startNewConversationListView.adapter =
                                            context?.let {
                                                CustomList(it,
                                                    recommendedAndExactLinkedList)
                                            }

                                    } else if (exactList != null) {
                                        recommendedAndExactLinkedList.clear()
                                        recommendedAndExactLinkedList.add(exactList)
                                        binding.startNewConversationListView.visibility =
                                            View.VISIBLE
                                        binding.startNewConversationListView.adapter =
                                            context?.let {
                                                CustomList(it,
                                                    recommendedAndExactLinkedList)
                                            }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                                TODO("Not yet implemented")
                            }
                        })
                } else {
                    binding.startNewConversationListView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.startNewConversationListView.setOnItemClickListener(OnItemClickListener { adapterView, view, position, l ->
            selectedUserLinkedList.add(recommendedAndExactLinkedList.get(position))
            showUsersOnAdapter()
        })
        binding.startNewConversationGoBackBtn.setOnClickListener { MyDataClass.onBack() }
        binding.startNewConversationAttachFileBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent = Intent.createChooser(intent, "Choose a file")
                startActivityForResult(intent, PICKFILE_REQUEST_CODE)

            }
        }
        )
        binding.startNewConversationStartNewConversationBtn.setOnClickListener(object :
            View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                if (selectedUserLinkedList.isEmpty()) {
                    Toast.makeText(context,"Please select at least one recipient",Toast.LENGTH_LONG).show()
                } else
                    if (binding.startNewConversationMessageEt.text.toString().isEmpty()) {
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
                                binding.startNewConversationMessageEt.text.toString(),
                                gettedAttachmentKey)
                        } else {
                            var retrofit = RetrofitManager.getRetrofit1()
                            var api = retrofit.create(HitApi::class.java)
                            hitApi(api,binding.startNewConversationMessageEt.text.toString(), "")

                        }
                    }
            }
        })
        binding.startNewConversationBold.setOnClickListener { binding.startNewConversationRichEditor.setBold() }
        binding.startNewConversationItalic.setOnClickListener { binding.startNewConversationRichEditor.setItalic() }
        binding.startNewConversationUndo.setOnClickListener { binding.startNewConversationRichEditor.undo() }
        binding.startNewConversationRedo.setOnClickListener { binding.startNewConversationRichEditor.redo() }
        binding.startNewConversationUnderline.setOnClickListener { binding.startNewConversationRichEditor.setUnderline() }
//        binding.startNewConversationIndent.setOnClickListener { binding.startNewConversationRichEditor.setIndent() }
//        binding.startNewConversationOutdent.setOnClickListener { binding.startNewConversationRichEditor.setOutdent() }
        binding.startNewConversationAlignLeft.setOnClickListener { binding.startNewConversationRichEditor.setAlignLeft() }
        binding.startNewConversationAlignRight.setOnClickListener { binding.startNewConversationRichEditor.setAlignRight() }
        binding.startNewConversationAlignCenter.setOnClickListener { binding.startNewConversationRichEditor.setAlignCenter() }
        binding.startNewConversationInsertBullets.setOnClickListener { binding.startNewConversationRichEditor.setBullets() }
        binding.startNewConversationInsertNumbers.setOnClickListener { binding.startNewConversationRichEditor.setNumbers() }
//        binding.startNewConversationIndent.setOnClickListener { binding.startNewConversationRichEditor.setIndent() }
//        binding.startNewConversationOutdent.setOnClickListener { binding.startNewConversationRichEditor.setOutdent() }
        binding.startNewConversationRichEditor.setOnTextChangeListener(RichEditor.OnTextChangeListener { text -> //                Log.d("TAG", text);
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
        binding.startNewConversationFontSizeBtn.setOnClickListener(object :
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
                    binding.startNewConversationRichEditor.setFontSize(1)
                    Log.d("TAG", "clicked$i")
                }
                1 -> {
                    binding.startNewConversationRichEditor.setFontSize(2)
                    Log.d("TAG", "clicked$i")
                }
                2 -> {
                    binding.startNewConversationRichEditor.setFontSize(3)
                    Log.d("TAG", "clicked$i")
                }
                3 -> {
                    binding.startNewConversationRichEditor.setFontSize(4)
                    Log.d("TAG", "clicked$i")
                }
                4 -> {
                    binding.startNewConversationRichEditor.setFontSize(5)
                    Log.d("TAG", "clicked$i")
                }
                5 -> {
                    binding.startNewConversationRichEditor.setFontSize(6)
                    Log.d("TAG", "clicked$i")
                }
                6 -> {
                    binding.startNewConversationRichEditor.setFontSize(7)
                    Log.d("TAG", "clicked$i")
                }
            }
        }
        binding.startNewConversationInsertLink.setOnClickListener {
            if (isLinkOpened) {
                isLinkOpened = false
                binding.startNewConversationInsertLinkLayout.visibility = View.GONE
            } else {
                isLinkOpened = true
                binding.startNewConversationInsertLinkLayout.visibility = View.VISIBLE
            }
        }
        binding.startNewConversationInsertLinkBtn.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                if (binding.startNewConversationUrlEt.text.isEmpty()) {
                    binding.startNewConversationUrlEt.setError("Please enter a valid URL")
                    binding.startNewConversationUrlEt.focusable = View.FOCUSABLE
                } else if (binding.startNewConversationTextEt.text.toString().isEmpty()) {
                    binding.startNewConversationTextEt.setError("Please enter a valid text")
                    binding.startNewConversationTextEt.focusable = View.FOCUSABLE
                } else {
                    binding.startNewConversationRichEditor.insertLink(binding.startNewConversationUrlEt.text.toString(),
                        binding.startNewConversationTextEt.text.toString())
                    binding.startNewConversationUrlEt.setText("")
                    binding.startNewConversationTextEt.setText("")
                    binding.startNewConversationInsertLinkLayout.visibility = View.GONE
                }
            }
        })
        return binding.root
    }

    private fun checkIsUserRequestCodeMatch() {
        if(userRequestCode==102){
            userObject?.let { selectedUserLinkedList.add(it) }
            showUsersOnAdapter()
        }
    }

    private fun showUsersOnAdapter() {
        binding.startNewConversationRecyclerViewForShowSelectUsersName.adapter =
            context?.let {
                ShowSelectedUsersNameAdapter(it,
                    selectedUserLinkedList,
                    ::showUsersOnAdapter)
            }
        binding.startNewConversationRecyclerViewForShowSelectUsersName.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressBar.show()
        if (requestCode == PICKFILE_REQUEST_CODE) {
          if(data?.data!=null){
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
          }else{
              progressBar.dismiss()
          }

        }


    }

    private fun postAttachmentFile(
        fileToUpload: MultipartBody.Part,
        attachmentKey: RequestBody,
        api: HitApi,
    ) {
        api.postAttachmentFile(MyDataClass.api_key,
            MyDataClass.myUserId, fileToUpload, attachmentKey
        ).enqueue(object : Callback<ResponseThread> {
            override fun onResponse(
                call: Call<ResponseThread>,
                response: Response<ResponseThread>,
            ) {
                Log.d("TAG", "code ${response.code()}")
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

    private fun initializeData() {
        MyDataClass.isGoConversation=false
        alertDialog = context?.let { AlertDialog.Builder(it) }
        binding.startNewConversationRichEditor.setEditorHeight(100)
        binding.startNewConversationRichEditor.setEditorFontSize(15)
        binding.startNewConversationRichEditor.setEditorFontColor(Color.BLACK)
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        binding.startNewConversationRichEditor.setPadding(4,4,4,4)
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        binding.startNewConversationRichEditor.setPlaceholder("Write reply...")
        MyDataClass.attachmentFileListAttachmentId.clear()
        MyDataClass.attachmentFileListItem.clear()
        progressBar = ProgressDialog(context)
    }

    private fun generateAttachmentKey(api: HitApi, fileToUpload: MultipartBody.Part) {
        isGeneratedAttachmentKey = true
        api.generateAttachmentKeyForConversation(MyDataClass.api_key,
            MyDataClass.myUserId,
            "conversation_message")
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>,
                ) {
                    Log.d("TAG", "code ${response.code()}")
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

    private fun funAdapter() {
        if (MyDataClass.attachmentFileListItem.size > 0) {
            binding.startNewConversationRecyclerView.visibility = View.VISIBLE
            binding.startNewConversationRecyclerView.adapter =
                context?.let {
                    ShowAttachmentFilesAdapter(it,
                        MyDataClass.attachmentFileListItem,
                        MyDataClass.attachmentFileListAttachmentId)
                }
            binding.startNewConversationRecyclerView.layoutManager =
                LinearLayoutManager(context)
            binding.startNewConversationRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            binding.startNewConversationRecyclerView.visibility = View.GONE
        }
    }

    private fun hitApi(api: HitApi, message: String, gettedAttachmentKey: String) {
        var list: LinkedList<Int> = LinkedList<Int>()
        for (i in selectedUserLinkedList) {
            list.add(i.user_id)
        }
        api.startNewConversation(MyDataClass.api_key,
            MyDataClass.myUserId,
            list,
            binding.startNewConversationTitleEt.text.toString(),
            (message + attachmentFileString),
            gettedAttachmentKey, conversation_open, open_invite)
            .enqueue(object : Callback<ResponseThread> {
                override fun onResponse(
                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    Log.d("TAG", "code ${response.code()}")
                    if (response.body()?.success == true) {
                        progressBar.dismiss()
                        var transaction = MyDataClass.getTransaction()
                        if (transaction != null) {
                            MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                            MyDataClass.homeNestedScrollView.visibility = View.GONE
                            transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                                ShowAndReplyConversation(
                                    response.body()!!.conversation))
                            transaction.addToBackStack(null).commit()
                        }
                    } else {
                        progressBar.dismiss()
                    }
                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    progressBar.dismiss()
                    Log.d("TAG", "${t.localizedMessage} errro")
                }
            })
    }

    class CustomList(val context: Context, val recommendedAndExactLinkedList: LinkedList<User>) :
        BaseAdapter() {
        override fun getCount(): Int {
            return recommendedAndExactLinkedList.size
        }

        override fun getItem(i: Int): Any {
            return Any()
        }

        override fun getItemId(i: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view1: View = LayoutInflater.from(context)
                .inflate(R.layout.show_recipients_user_name_custom_layout, parent, false)
            val userName = view1.findViewById<TextView>(R.id.showRecipientsUserName)
            val userProfile = view1.findViewById<CircleImageView>(R.id.showRecipientsUserProfile)
            userName.isSelected = true
            userName.setText(recommendedAndExactLinkedList.get(position).username)
            Picasso.get().load(recommendedAndExactLinkedList.get(position).avatar_urls.o)
                .placeholder(R.drawable.ic_no_image).into(userProfile)
            return view1
        }
    }

}