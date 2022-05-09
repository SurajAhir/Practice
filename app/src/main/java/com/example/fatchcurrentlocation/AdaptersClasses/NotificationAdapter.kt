package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.Alerts
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.Fragments.ShowPostsOfThreads
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NotificationAdapter(val context: Context, val list: List<Alerts>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        var view =
            LayoutInflater.from(context).inflate(R.layout.notification_custom_layout, parent, false)
        return NotificationViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        if (list.get(position).User.avatar_urls.o == null) {
            holder.profileImage_tv.visibility = View.VISIBLE
            holder.profileImage.visibility = View.GONE
            holder.profileImage_tv.gravity = Gravity.CENTER
            holder.profileImage_tv.setText(list.get(position).User.username.get(0).toString())
        } else {
            Picasso.get().load(list.get(position).User.avatar_urls.o).placeholder(R.drawable.person)
                .into(holder.profileImage)
        }
        Log.d("TAG", "${list.get(position).alert_id}")
        holder.alertText.setText(list.get(position).alert_text)
        var lastActivityDate = Date((list.get(position).event_date as Long) * 1000)
        var simpleFormat2 = SimpleDateFormat("HH:mm")
        convertTimeIntoSimpleMinuts(lastActivityDate, holder)
        holder.alertText.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                api.getPostOfAlerts(MyDataClass.api_key,
                    MyDataClass.myUserId,
                    list.get(position).content_id).enqueue(object : Callback<ResponseThread> {
                    override fun onResponse(
                        call: Call<ResponseThread>,
                        response: Response<ResponseThread>,
                    ) {
                        if (response.isSuccessful) {
                            MyDataClass.isGoNotification=false
                            var post = response.body()?.post
                            var transaction = MyDataClass.getTransaction()
                            if (transaction != null) {
                                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                                MyDataClass.homeNestedScrollView.visibility = View.GONE
                                var date = Date((post?.Thread?.last_post_date as Long) * 1000)
                                var simple = SimpleDateFormat("dd yyyy")
                           var time=    DateFormatSymbols().getShortMonths()[date.month]+" "+simple.format(date)
                                post?.Thread?.thread_id?.let {
                                    ShowPostsOfThreads(post.Thread.username,
                                       post.Thread.title, post.Thread.Forum.title, it,time,post.position,1001)
                                }?.let {
                                    transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                                        it)
                                }
                                transaction.addToBackStack(null).commit()
                            }
                        }

                    }

                    override fun onFailure(call: Call<ResponseThread>, t: Throwable) {

                    }
                })
            }
        })
    }

    private fun convertTimeIntoSimpleMinuts(
        lastActivityDate: Date,
        holder: NotificationViewHolder,
    ) {
        var currentTime = Date(System.currentTimeMillis())
        Log.d("TAG", "year ${currentTime.month} and ${lastActivityDate.month}")
        if (currentTime.year == lastActivityDate.year) {
            if (lastActivityDate.month == currentTime.month) {
                if (lastActivityDate.date == currentTime.date) {
                    if (lastActivityDate.hours == currentTime.hours) {
                        if (currentTime.minutes - lastActivityDate.minutes < 2) {
                            holder.postDate.setText("a moment ago")
                        } else {
                            holder.postDate.setText("${currentTime.minutes - lastActivityDate.minutes} minutes ago")
                        }
                    } else if (lastActivityDate.hours < currentTime.hours) {
                        var timeInMinuts =
                            (currentTime.hours - lastActivityDate.hours) * 60 + currentTime.minutes
                        if (timeInMinuts - lastActivityDate.minutes < 60) {
                            holder.postDate.setText("${Math.abs(timeInMinuts - lastActivityDate.minutes)} minutes ago")
                        } else {
                            holder.postDate.setText("${currentTime.hours - lastActivityDate.hours} hour ago")
                        }
                    }
                } else {
                    if (currentTime.date - lastActivityDate.date < 2) {
                        holder.postDate.setText("${currentTime.date - lastActivityDate.date} day ago")
                    } else {
                        holder.postDate.setText("${currentTime.date - lastActivityDate.date} days ago")
                    }
                }
            } else {
                if (currentTime.month - lastActivityDate.month < 2) {
                    holder.postDate.setText("${currentTime.month - lastActivityDate.month} month ago")
                } else {
                    holder.postDate.setText("${currentTime.month - lastActivityDate.month} months ago")
                }
            }
        } else {
            if ((currentTime.year - 100) - (lastActivityDate.year - 100) < 2) {
                holder.postDate.setText("${(currentTime.year - 100) - (lastActivityDate.year - 100)} year ago")
            } else {
                holder.postDate.setText("${(currentTime.year - 100) - (lastActivityDate.year - 100)} years ago")
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView =
            itemView.findViewById(R.id.notification_custom_layout_UserProfileImage)
        var profileImage_tv: TextView =
            itemView.findViewById(R.id.notification_custom_layout_UserProfileImage_tv)
        var alertText: TextView =
            itemView.findViewById(R.id.notification_custom_layout_alertTest_tv)
        var postDate: TextView = itemView.findViewById(R.id.notification_custom_layout_postDate_tv)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): Int {
        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("d")
        val now: LocalDateTime = LocalDateTime.now()
        return dtf.format(now) as Int
    }
}