package com.example.fatchcurrentlocation.AdaptersClasses

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.User
import com.example.fatchcurrentlocation.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ShowSelectedUsersNameAdapter(
    val context: Context,
    val list: LinkedList<User>,
    val kFunction0: () -> Unit
):
    RecyclerView.Adapter<ShowSelectedUsersNameAdapter.ShowSelectedUsersNameViewHolder>() {

    class ShowSelectedUsersNameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var username:TextView=itemView.findViewById(R.id.showSelectedUsersNameUserName)
        var ProfileImage:CircleImageView=itemView.findViewById(R.id.showSelectedUsersNameuserProfile)
        var profileImage_tv:TextView=itemView.findViewById(R.id.showSelectedUsersNameUserProfileImage_tv)
        var closeBtn:ImageButton=itemView.findViewById(R.id.showSelectedUsersNameCloseBtn)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShowSelectedUsersNameViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.show_selected_users_name_custom_layuot,parent,false)
        return ShowSelectedUsersNameViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowSelectedUsersNameViewHolder, position: Int) {
        holder.username.setText(list.get(position).username)
        if(list.get(position).avatar_urls.o==null){
            holder.profileImage_tv.visibility=View.VISIBLE
            holder.ProfileImage.visibility=View.GONE
            holder.profileImage_tv.gravity= Gravity.CENTER
            holder.profileImage_tv.setText(list.get(position).username.get(0).toString())
        }else{
            Picasso.get().load(list.get(position).avatar_urls.o).placeholder(R.drawable.person)
                .into(holder.ProfileImage)
        }
        holder.closeBtn.setOnClickListener { list.removeAt(position)
        kFunction0()}
    }

    override fun getItemCount(): Int {
        return list.size
    }
}