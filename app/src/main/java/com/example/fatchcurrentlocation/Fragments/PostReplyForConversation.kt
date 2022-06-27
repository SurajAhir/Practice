package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.ShowAttachmentFilesAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.FileUtils
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentPostReplyForConversationBinding
import jp.wasabeef.richeditor.RichEditor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

class PostReplyForConversation(
    val receivedText: String,
    val username: String,
    val conversation_id: Int,
    val first_message_id: Int,
    val userId: Int,
) : Fragment() {

    lateinit var binding: FragmentPostReplyForConversationBinding
    var isChangedTextStyleBold: Boolean = false
    var isChangedTextStyleItalic: Boolean = false
    lateinit var progressBar: ProgressDialog
    val PICKFILE_REQUEST_CODE: Int = 1001
    var attachmentId: Int = 0
    var isAttachedFile: Boolean = false
    var gettedAttachmentKey: String = ""
    var isGeneratedAttachmentKey = false
    lateinit var attachmentRequestBodyKey: RequestBody
    var gettedTextFromEditor: String = ""
    var alertDialog: AlertDialog.Builder? = null
    var alertDialog1: AlertDialog? = null
    var centername = ""
    var rightname = ""
    var sizes = arrayOf("9", "10", "12", "15", "18", "22", "26")
    var isLinkOpened=false
    //    var  convertString:String="""<blockquote class=\"xfBb-quote\" data-name=\"$username\">${receivedText}</blockquote>"""
    var convertString: String =
        "[QUOTE=\\\"$username, convMessage: $first_message_id, member: $userId\\\"]$receivedText[/QUOTE]"
    var attachmentFileString: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentPostReplyForConversationBinding.inflate(layoutInflater, container, false)
        initializeData()
        binding.postReplyForConversationAttachFileBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent = Intent.createChooser(intent, "Choose a file")
                startActivityForResult(intent, PICKFILE_REQUEST_CODE)
            }
        })
        binding.postReplyForConversationGoBackBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.onBack()
            }
        })
        binding.postReplyForConversationPostReplyBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.postReplyForConversationReplyMessageEt.text.toString().isEmpty()) {
                    return
                } else {
                    progressBar.show()
//                    Log.d("TAG", "thread $threadId")
                    if (isAttachedFile) {
                        isAttachedFile = false
                        if (MyDataClass.attachmentFileListAttachmentId.size > 0) {
                            for (i in MyDataClass.attachmentFileListAttachmentId) {
                                attachmentFileString = attachmentFileString +
                                        """[ATTACH type="full"]${i}[/ATTACH] """
                            }
                            hitApi(gettedAttachmentKey, attachmentFileString)
                        }
                    } else {
                        hitApi("", "")
                    }


                }
            }
        })
        binding.postReplyForConversationBold.setOnClickListener { binding.postReplyForConversationRichEditor.setBold() }
        binding.postReplyForConversationItalic.setOnClickListener { binding.postReplyForConversationRichEditor.setItalic() }
        binding.postReplyForConversationUndo.setOnClickListener { binding.postReplyForConversationRichEditor.undo() }
        binding.postReplyForConversationRedo.setOnClickListener { binding.postReplyForConversationRichEditor.redo() }
        binding.postReplyForConversationUnderline.setOnClickListener { binding.postReplyForConversationRichEditor.setUnderline() }
//        binding.postReplyForConversationIndent.setOnClickListener { binding.postReplyForConversationRichEditor.setIndent() }
//        binding.postReplyForConversationOutdent.setOnClickListener { binding.postReplyForConversationRichEditor.setOutdent() }
        binding.postReplyForConversationAlignLeft.setOnClickListener { binding.postReplyForConversationRichEditor.setAlignLeft() }
        binding.postReplyForConversationAlignRight.setOnClickListener { binding.postReplyForConversationRichEditor.setAlignRight() }
        binding.postReplyForConversationAlignCenter.setOnClickListener { binding.postReplyForConversationRichEditor.setAlignCenter() }
        binding.postReplyForConversationInsertBullets.setOnClickListener { binding.postReplyForConversationRichEditor.setBullets() }
        binding.postReplyForConversationInsertNumbers.setOnClickListener { binding.postReplyForConversationRichEditor.setNumbers() }
//        binding.postReplyForConversationIndent.setOnClickListener { binding.postReplyForConversationRichEditor.setIndent() }
//        binding.postReplyForConversationOutdent.setOnClickListener { binding.postReplyForConversationRichEditor.setOutdent() }
        binding.postReplyForConversationRichEditor.setOnTextChangeListener(RichEditor.OnTextChangeListener { text -> //                Log.d("TAG", text);
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
        binding.postReplyForConversationInsertLink.setOnClickListener {
            if (isLinkOpened) {
                isLinkOpened = false
                binding.postReplyForConversationInsertLinkLayout.visibility = View.GONE
            } else {
                isLinkOpened = true
                binding.postReplyForConversationInsertLinkLayout.visibility = View.VISIBLE
            }
        }
        binding.postReplyForConversationInsertLinkBtn.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                if (binding.postReplyForConversationUrlEt.text.isEmpty()) {
                    binding.postReplyForConversationUrlEt.setError("Please enter a valid URL")
                    binding.postReplyForConversationUrlEt.focusable = View.FOCUSABLE
                } else if (binding.postReplyForConversationTextEt.text.toString().isEmpty()) {
                    binding.postReplyForConversationTextEt.setError("Please enter a valid text")
                    binding.postReplyForConversationTextEt.focusable = View.FOCUSABLE
                } else {
                    binding.postReplyForConversationRichEditor.insertLink(binding.postReplyForConversationUrlEt.text.toString(),
                        binding.postReplyForConversationTextEt.text.toString())
                    binding.postReplyForConversationUrlEt.setText("")
                    binding.postReplyForConversationTextEt.setText("")
                    binding.postReplyForConversationInsertLinkLayout.visibility = View.GONE
                }
            }
        })
        binding.postReplyForConversationFontSizeBtn.setOnClickListener(object :
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
                    binding.postReplyForConversationRichEditor.setFontSize(1)
                    Log.d("TAG", "clicked$i")
                }
                1 -> {
                    binding.postReplyForConversationRichEditor.setFontSize(2)
                    Log.d("TAG", "clicked$i")
                }
                2 -> {
                    binding.postReplyForConversationRichEditor.setFontSize(3)
                    Log.d("TAG", "clicked$i")
                }
                3 -> {
                    binding.postReplyForConversationRichEditor.setFontSize(4)
                    Log.d("TAG", "clicked$i")
                }
                4 -> {
                    binding.postReplyForConversationRichEditor.setFontSize(5)
                    Log.d("TAG", "clicked$i")
                }
                5 -> {
                    binding.postReplyForConversationRichEditor.setFontSize(6)
                    Log.d("TAG", "clicked$i")
                }
                6 -> {
                    binding.postReplyForConversationRichEditor.setFontSize(7)
                    Log.d("TAG", "clicked$i")
                }
            }
        }
        return binding.root
    }
    private fun hitApi(gettedAttachmentKey: String, attachmentFileString: String) {
        Log.d("TAG", "string ${attachmentFileString}")
        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.postConversationReply(MyDataClass.api_key,
            MyDataClass.myUserId,
            conversation_id,
            convertString + binding.postReplyForConversationReplyMessageEt.text.toString() + attachmentFileString,
            gettedAttachmentKey)
            .enqueue(object : Callback<ResponseThread> {
                override fun onResponse(
                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    Log.d("TAG","CODE ${response.code()}")
                    if (response.isSuccessful) {
                        progressBar.dismiss()
                        isAttachedFile = false
                        MyDataClass.onBack()
                    }else{
                        progressBar.dismiss()
                    }
                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                    progressBar.dismiss()
                }
            })
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

    private fun funAdapter() {
        if (MyDataClass.attachmentFileListItem.size > 0) {
            binding.postReplyForConversationRecyclerView.visibility = View.VISIBLE
            binding.postReplyForConversationRecyclerView.adapter =
                context?.let {
                    ShowAttachmentFilesAdapter(it,
                        MyDataClass.attachmentFileListItem,
                        MyDataClass.attachmentFileListAttachmentId)
                }
            binding.postReplyForConversationRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.postReplyForConversationRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            binding.postReplyForConversationRecyclerView.visibility = View.GONE
        }
    }
    private fun initializeData() {
        alertDialog = context?.let { AlertDialog.Builder(it) }
        binding.postReplyForConversationRichEditor.setEditorHeight(100)
        binding.postReplyForConversationRichEditor.setEditorFontSize(15)
        binding.postReplyForConversationRichEditor.setEditorFontColor(Color.BLACK)
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        binding.postReplyForConversationRichEditor.setPadding(4,4,4,4)
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        binding.postReplyForConversationRichEditor.setPlaceholder("Write reply...")
        MyDataClass.attachmentFileListItem.clear()
        MyDataClass.attachmentFileListAttachmentId.clear()
        progressBar = ProgressDialog(context)
        progressBar.setTitle("Wait...")
        binding.postReplyForConversationMessageEt.setText(receivedText)
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
                Log.d("TAG","code ${response.code()}")
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
        api.generateAttachmentKeyForPostReplyForConversation(MyDataClass.api_key,
            MyDataClass.myUserId,
            conversation_id,
            "conversation_message")
            .enqueue(object : Callback<Map<String,String>> {
                override fun onResponse(
                    call: Call<Map<String,String>>,
                    response: Response<Map<String,String>>,
                ) {
                    Log.d("TAG","CODE ${response.code()} and messageid ${conversation_id}")
                    if (response.isSuccessful) {
                        gettedAttachmentKey = response.body()?.get("key").toString()
                        Log.d("TAG","key $gettedAttachmentKey")
                        attachmentRequestBodyKey =
                            RequestBody.create(MediaType.parse("multipart/form-data"),
                                gettedAttachmentKey)
                        postAttachmentFile(fileToUpload, attachmentRequestBodyKey, api)
                    } else {
                        progressBar.dismiss()
                    }

                }

                override fun onFailure(call: Call<Map<String,String>>, t: Throwable) {
                    Log.d("TAG", "error ${t.localizedMessage}")
                    progressBar.dismiss()
                }
            })
    }

}