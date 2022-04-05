package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fatchcurrentlocation.DataClasses.AttachmentDataResponse
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.FindPath
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.ResponseObject
import com.example.fatchcurrentlocation.RetrofitManager
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
    var fileAttchFileUrl: String = ""
    var isGettedKey: Boolean = false
    var IMAGE_DIRECTORY = "/demonuts_upload_gallery"
    var BUFFER_SIZE = 1024 * 2

    //    var  convertString:String="""<blockquote class=\"xfBb-quote\" data-name=\"$username\">${receivedText}</blockquote>"""
    var convertString: String =
        "[QUOTE=\\\"$username, post: $postId, member: $userId\\\"]$receivedText[/QUOTE]"

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
                                    Log.d("TAG",
                                        convertString + binding.postCommentsReplyMessageEt.text.toString())
                                    Toast.makeText(context,
                                        response.body()?.success.toString(),
                                        Toast.LENGTH_LONG).show()
                                    MyDataClass.onBack()
                                }
                            }

                            override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                                Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                                    .show()
                            }
                        })

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
            var uri1 = FindPath.getFilePathFromURI(context, data?.data)
            Log.d("FILE",uri1)
            var file: File = File(uri1)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val fileToUpload =
                MultipartBody.Part.createFormData("attachment", file.getName(), requestBody)
            var key = RequestBody.create(MediaType.parse("multipart/form-data"),
                "1649135798-k7e0gGY9MBjy1IMWUmTF1")
            var map =mapOf<String, RequestBody>("key" to key)
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
//                       var  attachmentKey = response.body()?.get("key")!!
//                        Log.d("TAG","${requestBody} and userid ${MyDataClass.myUserId} and key=${attachmentKey}")
                        api.postAttachmentFile("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
                            MyDataClass.myUserId, fileToUpload, key
                        ).enqueue(object : Callback<ResponseObject> {
                            override fun onResponse(
                                call: Call<ResponseObject>,
                                response: Response<ResponseObject>,
                            ) {
                                progressBar.dismiss()
                                Log.d("TAG", response.code().toString())
                                Log.d("TAG", call.request().toString())
                                Log.d("TAG", response.body().toString())
                            }

                            override fun onFailure(
                                call: Call<ResponseObject>,
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