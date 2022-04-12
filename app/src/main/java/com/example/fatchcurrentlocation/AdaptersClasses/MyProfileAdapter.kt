package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ProfilePosts
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class MyProfileAdapter(
    val context: Context,
    val list: LinkedList<ProfilePosts>,
    val userId: Int,
) : RecyclerView.Adapter<MyProfileAdapter.MyProfileHolder>() {
    var isChangedTextStyleBold = false
    var isChangedTextStyleItalic = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProfileHolder {
        var view = LayoutInflater.from(context)
            .inflate(R.layout.fragment_my_profile_custom_layout, parent, false)
        return MyProfileHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyProfileHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        Picasso.get().load(list.get(position).User.avatar_urls.o).placeholder(R.drawable.person)
            .into(holder.ProfileImage)
        holder.userName.setText(list.get(position).username)
        holder.message.setText(list.get(position).message_parsed)
        var date = Date((list.get(position).post_date as Long) * 1000)
        var simple = SimpleDateFormat("dd yyyy")
        holder.postDate.setText("${DateFormatSymbols().getShortMonths()[date.month]} ${
            simple.format(date)
        }")
        Log.d("TAG", "postid${list.get(position).profile_post_id}")

//        isReactedTo(holder, position)
        Log.d("TAG", "reaction ${list.get(position).profile_user_id}")
        holder.postComment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                holder.postCommentLayout.visibility = View.VISIBLE
                holder.postComment.visibility = View.GONE
            }
        })
        holder.postBtn.setOnClickListener(object : View.OnClickListener {
            var progressBar = ProgressDialog(context)
            override fun onClick(p0: View?) {
                if (holder.writenMessage.text.toString().isEmpty()) {
                    return
                } else {
                    progressBar.show()
                    var retrofit: Retrofit = RetrofitManager.getRetrofit1()
                    var api: HitApi = retrofit.create(HitApi::class.java)
                    api.getReaponseOfProfilePostsOfMessages(MyDataClass.api_key,
                        MyDataClass.myUserId,
                        userId,
                        holder.writenMessage.text.toString())
                        .enqueue(object : Callback<ResponseDataClass> {
                            override fun onResponse(
                                call: Call<ResponseDataClass>,
                                response: Response<ResponseDataClass>,
                            ) {
                                Log.d("TAG", "${response.code()}")
                                if (response.body()?.success == true) {
                                    progressBar.dismiss()
                                    Log.d("TAG", "respon ${response.code()}")
                                    holder.writenMessage.setText("")
                                    holder.postComment.visibility = View.VISIBLE
                                    holder.postCommentLayout.visibility = View.GONE
                                } else {
                                    progressBar.dismiss()
                                }
                            }

                            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                                Log.d("TAG", "${t.localizedMessage} errro")
                                progressBar.dismiss()
                            }
                        })
                }
            }
        })
        holder.textItalicBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isChangedTextStyleBold) {
                    holder.writenMessage.setTypeface(null, Typeface.NORMAL)
                    isChangedTextStyleBold = false
                } else {
                    isChangedTextStyleBold = true
                    holder.writenMessage.setTypeface(null, Typeface.BOLD)
                }
            }
        })
        holder.textBoldBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isChangedTextStyleItalic) {
                    holder.writenMessage.setTypeface(null, Typeface.NORMAL)
                    isChangedTextStyleItalic = false
                } else {
                    isChangedTextStyleItalic = true
                    holder.writenMessage.setTypeface(null, Typeface.ITALIC)
                }
            }
        })
        holder.deleteBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                var alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
                alertDialog.setTitle("Delete")
                    .setMessage("Are you sure you want to delete this post")
                    .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            api.deleteSpecificPost(MyDataClass.api_key,
                                MyDataClass.myUserId,
                                list.get(position).profile_post_id)
                                .enqueue(object : Callback<Map<String, Boolean>> {
                                    override fun onResponse(
                                        call: Call<Map<String, Boolean>>,
                                        response: Response<Map<String, Boolean>>,
                                    ) {
                                        if (response.isSuccessful) {
                                            if (response.body()?.get("success") == true) {
                                                MyDataClass.onBack()
                                            }
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<Map<String, Boolean>>,
                                        t: Throwable,
                                    ) {
                                        MyDataClass.onBack()
                                    }
                                })

                        }
                    }).setNegativeButton("No",object :DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            p0?.dismiss()
                        }
                    }).show()
            }
        })
    }

//    private fun isReactedTo(holder: MyProfileHolder, position: Int) {
//        if (list.get(position).is_reacted_to) {
//            if (list.get(position).reaction_score > 0) {
//                if ((list.get(position).reaction_score - 1) != 0) {
//                    holder.likeCounts.visibility=View.VISIBLE
//                    holder.likeCounts.setText("You,${list.get(position).reaction_score - 1} other")
//                } else {
//                    holder.likeCounts.visibility=View.VISIBLE
//                    holder.likeCounts.setText("You")
//                }
//            } else {
//                holder.likeCounts.visibility = View.GONE
//            }
//            when (list.get(position).visitor_reaction_id) {
//                1 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#0B18CC"))
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setText("Like")
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_like,
//                        0,
//                        0,
//                        0)
//                }
//                2 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#BF0404"))
//                    holder.likeBtn.setText("Love")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_love_icon,
//                        0,
//                        0,
//                        0)
//                }
//                3 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
//                    holder.likeBtn.setText("haha")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_haha_icon,
//                        0,
//                        0,
//                        0)
//                }
//                4 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
//                    holder.likeBtn.setText("Wow")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wow_icon,
//                        0,
//                        0,
//                        0)
//                }
//                5 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
//                    holder.likeBtn.setText("Sad")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sad_icon,
//                        0,
//                        0,
//                        0)
//                }
//                6 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#FB2707"))
//                    holder.likeBtn.setText("Angery")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_angry_icon,
//                        0,
//                        0,
//                        0)
//                }
//            }
//
//        } else {
//            holder.likeBtn.setTextColor(Color.parseColor("#FF000000"))
//            holder.likeBtn.setTypeface(null, Typeface.NORMAL)
//            holder.likeBtn.setText("Like")
//            holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,
//                0,
//                0,
//                0)
//            if(list.get(position).reaction_score>0){
//                holder.likeCounts.visibility=View.VISIBLE
//                holder.likeCounts.setText("${list.get(position).reaction_score}")
//            }else{
//                holder.likeCounts.visibility=View.GONE
//            }
//        }
//
//    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyProfileHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ProfileImage: CircleImageView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_UserProfileImage)
        var userName: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_userName_tv)
        var postDate: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_postDate_tv)
        var message: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_message_tv)
        var postComment: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_writeSomething_tv)
        var postCommentLayout: LinearLayout =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_writeSomething_LayoutToShow)
        var textBoldBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_b_btn)
        var textItalicBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_i_btn)
        var postBtn: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_post_Reply_btn)
        var writenMessage: EditText =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_reply_message_et)
        var reportBtn: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_report_btn)
        var deleteBtn: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_delete_btn)

    }

}