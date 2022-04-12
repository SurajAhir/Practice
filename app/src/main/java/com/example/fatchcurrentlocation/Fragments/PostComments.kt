package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.databinding.FragmentPostCommentsBinding
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

    //    var  convertString:String="""<blockquote class=\"xfBb-quote\" data-name=\"$username\">${receivedText}</blockquote>"""
    var convertString: String =
        "[QUOTE=\\\"$username, post: $postId, member: $userId\\\"]$receivedText[/QUOTE]"
    lateinit var attachmentFileString: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPostCommentsBinding.inflate(layoutInflater, container, false)
        Log.d("TAG", convertString)
        progressBar = ProgressDialog(context)
        progressBar.setTitle("Wait...")
        binding.postCommentsMessageEt.setText(receivedText)
        binding.postCommentsAttachFileBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent = Intent.createChooser(intent, "Choose a file")
                startActivityForResult(intent, PICKFILE_REQUEST_CODE)
            }
        })
        binding.postCommentsBBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isChangedTextStyleBold) {
                    binding.postCommentsReplyMessageEt.setTypeface(null, Typeface.NORMAL)
                    isChangedTextStyleBold = false
                } else {
                    isChangedTextStyleBold = true
                    binding.postCommentsReplyMessageEt.setTypeface(null, Typeface.BOLD)
                }
            }
        })
        binding.postCommentsGoBackBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.onBack()
            }
        })
        binding.postCommentsIBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isChangedTextStyleItalic) {
                    binding.postCommentsReplyMessageEt.setTypeface(null, Typeface.NORMAL)
                    isChangedTextStyleItalic = false
                } else {
                    isChangedTextStyleItalic = true
                    binding.postCommentsReplyMessageEt.setTypeface(null, Typeface.ITALIC)
                }
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
                        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
                        var api: HitApi = retrofit.create(HitApi::class.java)
                        api.getResponseOfComments("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
                            MyDataClass.myUserId,
                            threadId,
                            convertString + binding.postCommentsReplyMessageEt.text.toString() + attachmentFileString)
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
                    } else {
                        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
                        var api: HitApi = retrofit.create(HitApi::class.java)
                        api.getResponseOfComments("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
                            MyDataClass.myUserId,
                            threadId,
                            convertString + binding.postCommentsReplyMessageEt.text.toString())
                            .enqueue(object : Callback<ResponseThread> {
                                override fun onResponse(
                                    call: Call<ResponseThread>,
                                    response: Response<ResponseThread>,
                                ) {
                                    if (response.isSuccessful) {
                                        progressBar.dismiss()
                                        Toast.makeText(context,
                                            response.body()?.success.toString(),
                                            Toast.LENGTH_LONG).show()
                                        MyDataClass.onBack()
                                    }else{
                                        Log.d("TAG","code${response.code()}")
                                        progressBar.dismiss()
                                    }
                                }

                                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                                    Log.d("TAG","errr ${t.localizedMessage}")
                                    progressBar.dismiss()
                                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                                        .show()
                                }
                            })
                    }


                }
            }
        })
        return binding.root
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
            api.generateAttachmentKey("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
                MyDataClass.myUserId,
                threadId,
                "post")
                .enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(
                        call: Call<Map<String, String>>,
                        response: Response<Map<String, String>>,
                    ) {
                        var gettedAttachmentKey = response.body()?.get("key")
                        var attachmentKey =
                            RequestBody.create(MediaType.parse("multipart/form-data"),
                                gettedAttachmentKey)
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
                                    binding.postCommentsAttachedFileNameTv.visibility = View.VISIBLE
                                    binding.postCommentsAttachedFileNameTv.setText("${response.body()!!.attachment.filename}")
                                    attachmentFileString =
                                        """[ATTACH type="full"]${attachmentId}[/ATTACH]"""
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

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        Log.d("TAG", "error ${t.localizedMessage}")
                        progressBar.dismiss()
                    }
                })

        }

    }


}