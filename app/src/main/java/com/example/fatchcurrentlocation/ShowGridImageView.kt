package com.example.fatchcurrentlocation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.AdaptersClasses.ShowGridImageAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.ShowPostsOfThreadsAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.services.RetrofitManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ShowGridImageView : AppCompatActivity() {
    var listOfAttachment:ArrayList<Int> = ArrayList()
    var listOfAttachmentPics: LinkedList<Bitmap> = LinkedList()
    lateinit var progressBar:ProgressBar
    lateinit var recyclerView:RecyclerView
    lateinit var backBtn:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_grid_image_view)
        intent.getIntegerArrayListExtra("listOfAttachment")?.let { listOfAttachment.addAll(it) }
        recyclerView=findViewById(R.id.showGridImageViewRecyclerView)
        progressBar=findViewById(R.id.showGridImageViewProgressBar)
        backBtn=findViewById(R.id.showGridImageViewBackBtn)
        callGetAttachmentPicApi()
        backBtn.setOnClickListener { onBackPressed() }
    }

    private fun callGetAttachmentPicApi() {
        var retrofit = RetrofitManager.getRetrofit1()
        var api = retrofit.create(HitApi::class.java)
        for (i in listOfAttachment) {
            api.getAttachments(MyDataClass.api_key, MyDataClass.myUserId, i)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        if (response.isSuccessful) {
                            MyDataClass.JumpedToImageList.clear()
//                            MyDataClass.JumpedToImageList.addAll(list)
                            var byteArray = response.body()?.bytes()
                            var base64 = android.util.Base64.encodeToString(
                                byteArray,
                                android.util.Base64.DEFAULT
                            )
                            var decodedString =
                                android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                            var bitmap = BitmapFactory.decodeByteArray(
                                decodedString,
                                0,
                                decodedString.size
                            )
                            progressBar.visibility=View.GONE
                            listOfAttachmentPics.add(bitmap)
                            recyclerView.adapter=ShowGridImageAdapter(this@ShowGridImageView,listOfAttachmentPics)
                            recyclerView.layoutManager=GridLayoutManager(this@ShowGridImageView,3)

                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("TAG", "${t.localizedMessage}")
                        progressBar.visibility=View.GONE
                    }
                })
//                Log.d("TAG","${i.thumbnail_url}")
//                list1.add(i.thumbnail_url)
        }
    }
}