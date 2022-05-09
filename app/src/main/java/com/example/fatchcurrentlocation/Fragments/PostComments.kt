package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.AdaptersClasses.ShowAttachmentFilesAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.databinding.FragmentPostCommentsBinding
import jp.wasabeef.richeditor.RichEditor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*


class PostComments(
    val receivedText: String,
    val username: String,
    val threadId: Int,
    val postId: Int,
    val userId: Int,
) : Fragment() {
    lateinit var binding: FragmentPostCommentsBinding
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
        "[QUOTE=\\\"$username, post: $postId, member: $userId\\\"]$receivedText[/QUOTE]"
    var attachmentFileString: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPostCommentsBinding.inflate(layoutInflater, container, false)
        Log.d("TAG", "$threadId $postId $userId")
        initializeData()
        binding.postCommentsAttachFileBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent = Intent.createChooser(intent, "Choose a file")
                startActivityForResult(intent, PICKFILE_REQUEST_CODE)
            }
        })
        binding.postCommentsGoBackBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.onBack()
            }
        })

        binding.postCommentsPostReplyBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.postCommentsReplyMessageEt.text.toString().isEmpty()) {
                    return
                } else {
                    progressBar.show()
                    Log.d("TAG", "thread $threadId")
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
        binding.postCommentsBold.setOnClickListener { binding.postCommentsRichEditor.setBold() }
        binding.postCommentsItalic.setOnClickListener { binding.postCommentsRichEditor.setItalic() }
        binding.postCommentsUndo.setOnClickListener { binding.postCommentsRichEditor.undo() }
        binding.postCommentsRedo.setOnClickListener { binding.postCommentsRichEditor.redo() }
        binding.postCommentsUnderline.setOnClickListener { binding.postCommentsRichEditor.setUnderline() }
//        binding.postCommentsIndent.setOnClickListener { binding.postCommentsRichEditor.setIndent() }
//        binding.postCommentsOutdent.setOnClickListener { binding.postCommentsRichEditor.setOutdent() }
        binding.postCommentsAlignLeft.setOnClickListener { binding.postCommentsRichEditor.setAlignLeft() }
        binding.postCommentsAlignRight.setOnClickListener { binding.postCommentsRichEditor.setAlignRight() }
        binding.postCommentsAlignCenter.setOnClickListener { binding.postCommentsRichEditor.setAlignCenter() }
        binding.postCommentsInsertBullets.setOnClickListener { binding.postCommentsRichEditor.setBullets() }
        binding.postCommentsInsertNumbers.setOnClickListener { binding.postCommentsRichEditor.setNumbers() }
//        binding.postCommentsIndent.setOnClickListener { binding.postCommentsRichEditor.setIndent() }
//        binding.postCommentsOutdent.setOnClickListener { binding.postCommentsRichEditor.setOutdent() }
        binding.postCommentsRichEditor.setOnTextChangeListener(RichEditor.OnTextChangeListener { text -> //                Log.d("TAG", text);
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
        binding.postCommentsInsertLink.setOnClickListener {
            if (isLinkOpened) {
                isLinkOpened = false
                binding.postCommentsInsertLinkLayout.visibility = View.GONE
            } else {
                isLinkOpened = true
                binding.postCommentsInsertLinkLayout.visibility = View.VISIBLE
            }
        }
        binding.postCommentsInsertLinkBtn.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                if (binding.postCommentsUrlEt.text.isEmpty()) {
                    binding.postCommentsUrlEt.setError("Please enter a valid URL")
                    binding.postCommentsUrlEt.focusable = View.FOCUSABLE
                } else if (binding.postCommentsTextEt.text.toString().isEmpty()) {
                    binding.postCommentsTextEt.setError("Please enter a valid text")
                    binding.postCommentsTextEt.focusable = View.FOCUSABLE
                } else {
                    binding.postCommentsRichEditor.insertLink(binding.postCommentsUrlEt.text.toString(),
                        binding.postCommentsTextEt.text.toString())
                    binding.postCommentsUrlEt.setText("")
                    binding.postCommentsTextEt.setText("")
                    binding.postCommentsInsertLinkLayout.visibility = View.GONE
                }
            }
        })
        binding.postCommentsFontSizeBtn.setOnClickListener(object :
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
                    binding.postCommentsRichEditor.setFontSize(1)
                    Log.d("TAG", "clicked$i")
                }
                1 -> {
                    binding.postCommentsRichEditor.setFontSize(2)
                    Log.d("TAG", "clicked$i")
                }
                2 -> {
                    binding.postCommentsRichEditor.setFontSize(3)
                    Log.d("TAG", "clicked$i")
                }
                3 -> {
                    binding.postCommentsRichEditor.setFontSize(4)
                    Log.d("TAG", "clicked$i")
                }
                4 -> {
                    binding.postCommentsRichEditor.setFontSize(5)
                    Log.d("TAG", "clicked$i")
                }
                5 -> {
                    binding.postCommentsRichEditor.setFontSize(6)
                    Log.d("TAG", "clicked$i")
                }
                6 -> {
                    binding.postCommentsRichEditor.setFontSize(7)
                    Log.d("TAG", "clicked$i")
                }
            }
        }
        return binding.root
    }
    private fun initializeData() {
        alertDialog = context?.let { AlertDialog.Builder(it) }
//        binding.postCommentsRichEditor.setEditorHeight(100)
        binding.postCommentsRichEditor.setEditorFontSize(15)
        binding.postCommentsRichEditor.setEditorFontColor(Color.BLACK)
        binding.postCommentsRichEditor.setPadding(4, 4, 4, 4)
        binding.postCommentsRichEditor.setPlaceholder("Write comment...")
        binding.postCommentsRichEditor.setEditorHeight(100)
        MyDataClass.attachmentFileListItem.clear()
        MyDataClass.attachmentFileListAttachmentId.clear()
        progressBar = ProgressDialog(context)
        progressBar.setTitle("Wait...")
        binding.postCommentsMessageEt.setText(receivedText)
    }
    private fun hitApi(gettedAttachmentKey: String, attachmentFileString: String) {
        Log.d("TAG", "string ${attachmentFileString}")
        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getResponseOfComments("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
            MyDataClass.myUserId,
            threadId,
            convertString + binding.postCommentsReplyMessageEt.text.toString() + attachmentFileString,
            gettedAttachmentKey)
            .enqueue(object : Callback<ResponseThread> {
                override fun onResponse(
                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    if (response.isSuccessful) {
                        progressBar.dismiss()
                        isAttachedFile = false
                        MyDataClass.onBack()
                    }
                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
            })
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

    private fun funAdapter() {
        if (MyDataClass.attachmentFileListItem.size > 0) {
            binding.postCommentsRecyclerView.visibility = View.VISIBLE
            binding.postCommentsRecyclerView.adapter =
                context?.let {
                    ShowAttachmentFilesAdapter(it,
                        MyDataClass.attachmentFileListItem,
                        MyDataClass.attachmentFileListAttachmentId)
                }
            binding.postCommentsRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.postCommentsRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            binding.postCommentsRecyclerView.visibility = View.GONE
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
                Log.d("TAG","CODE ${response.code()} and ${fileToUpload} and ${attachmentKey}")
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
        api.generateAttachmentKey("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
            MyDataClass.myUserId,
            threadId,
            "post")
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>,
                ) {
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
}