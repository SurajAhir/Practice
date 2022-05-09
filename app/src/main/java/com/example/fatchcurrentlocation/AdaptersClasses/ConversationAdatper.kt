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
import com.example.fatchcurrentlocation.DataClasses.Conversations
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.Fragments.ShowAndReplyConversation
import com.example.fatchcurrentlocation.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ConversationAdatper(val context: Context, val list: List<Conversations>) :
    RecyclerView.Adapter<ConversationAdatper.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        var view =
            LayoutInflater.from(context)
                .inflate(R.layout.show_conversation_custom_layout, parent, false)
        return ConversationViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: ConversationViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        if (list.get(position).Starter.avatar_urls.o==null) {
            holder.profileImage_tv.visibility=View.VISIBLE
            holder.profileImage.visibility=View.GONE
            holder.profileImage_tv.gravity=Gravity.CENTER
            holder.profileImage_tv.setText(list.get(position).username.get(0).toString())
        } else {
            Picasso.get().load(list.get(position).Starter.avatar_urls.o)
                .placeholder(R.drawable.ic_no_image).into(holder.profileImage)
        }

        holder.recipient.setText("With: ${list.get(position).Starter.username},")
        var jsonObject = list.get(position).recipients
        var _set = jsonObject.keySet()
        var _iterator = _set.iterator()
        while (_iterator.hasNext()) {
            var key = _iterator.next()
            var value = jsonObject.get(key).asString
            holder.recipient.append("${value},")
        }
        holder.title.setText(list.get(position).title)
        var lastActivityDate = Date((list.get(position).start_date as Long) * 1000)
        var currentTime = Date(System.currentTimeMillis())
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
        holder.title.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var transaction = MyDataClass.getTransaction()
                if (transaction != null) {
                    Log.d("TAG", "con ${list.get(position).conversation_id}")
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                        ShowAndReplyConversation(list.get(position)))
                    transaction.addToBackStack(null).commit()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView =
            itemView.findViewById(R.id.show_conversations_custom_layout_UserProfileImage)
        var profileImage_tv: TextView =
            itemView.findViewById(R.id.show_conversations_custom_layout_UserProfileImage_tv)
        var title: TextView =
            itemView.findViewById(R.id.show_conversations_custom_layout_title_tv)
        var postDate: TextView =
            itemView.findViewById(R.id.show_conversations_custom_layout_postDate_tv)
        var recipient: TextView =
            itemView.findViewById(R.id.show_conversations_custom_layout_recipientName_tv)
    }

}