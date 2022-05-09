package com.example.fatchcurrentlocation.AdaptersClasses

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.User
import com.example.fatchcurrentlocation.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ShowParticipantsAdapter(val context: Context, val listOfUsers: LinkedList<User>) :
    RecyclerView.Adapter<ShowParticipantsAdapter.ShowParticipantsViewHoloder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowParticipantsViewHoloder {
        var view = LayoutInflater.from(context)
            .inflate(R.layout.show_participants_custom_layout, parent, false)
        return ShowParticipantsViewHoloder(view)
    }

    override fun onBindViewHolder(holder: ShowParticipantsViewHoloder, position: Int) {
        holder.userName.setText(listOfUsers.get(position).username)
        holder.userTitle.setText(listOfUsers.get(position).user_title)
        if(listOfUsers.get(position).avatar_urls.o==null){
            holder.profileImage_tv.visibility=View.VISIBLE
            holder.userProfile.visibility=View.GONE
            holder.profileImage_tv.gravity= Gravity.CENTER
            holder.profileImage_tv.setText(listOfUsers.get(position).username.get(0).toString())
        }else{
            Picasso.get().load(listOfUsers.get(position).avatar_urls.o).placeholder(R.drawable.person)
                .into(holder.userProfile)
        }
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    class ShowParticipantsViewHoloder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userProfile: CircleImageView =
            itemView.findViewById(R.id.show_participants_custom_layout_UserProfileImage)
        var profileImage_tv: TextView =
            itemView.findViewById(R.id.show_participants_custom_layout_UserProfileImage_tv)
        var userName: TextView =
            itemView.findViewById(R.id.show_participants_custom_layout_Username_tv)
        var userTitle: TextView =
            itemView.findViewById(R.id.show_participants_custom_layout_title_tv)
    }
}