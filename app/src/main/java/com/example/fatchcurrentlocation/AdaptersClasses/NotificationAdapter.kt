package com.example.fatchcurrentlocation.AdaptersClasses

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.Alerts
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
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
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        Picasso.get().load(list.get(position).User.avatar_urls.o)
            .placeholder(R.drawable.ic_no_image).into(holder.profileImage)
        holder.alertText.setText(list.get(position).alert_text)
        var lastActivityDate = Date((list.get(position).User.last_activity as Long) * 1000)
        var simpleFormat2 = SimpleDateFormat("HH:mm")
        if (lastActivityDate.minutes < 60) {
            holder.postDate.setText("${lastActivityDate.minutes} minuts ago")
        } else if (lastActivityDate.date.equals(getCurrentDate())) {
            holder.postDate.setText("Today at ${
                simpleFormat2.format(lastActivityDate)
            }")
        } else {
            holder.postDate.setText("${(getCurrentDate() - lastActivityDate.date)} days before")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView =
            itemView.findViewById(R.id.notification_custom_layout_UserProfileImage)
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