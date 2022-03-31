package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Pagination
import com.example.fatchcurrentlocation.DataClasses.Threads
import com.example.fatchcurrentlocation.Fragments.ShowPostsOfThreads
import com.example.fatchcurrentlocation.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ShowDetailsAdapter(
    val _list: LinkedList<Threads>,
    val _context: Context?,
    val pagination: Pagination,
    val title: String,
) : RecyclerView.Adapter<ShowDetailsAdapter.ShowDetailsViewHolder>() {
    var list = _list
    var context = _context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowDetailsViewHolder {
        var view =
            LayoutInflater.from(context).inflate(R.layout.show_details_custom_layout, parent, false)
        return ShowDetailsViewHolder(view)
    }


    override fun onBindViewHolder(
        holder: ShowDetailsViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.title.setText(list?.get(position)?.title ?: "")
        holder.lastPostUserName.setText(list?.get(position)?.last_post_username ?: "")
        holder.replies.append(list?.get(position)?.reply_count.toString())
        holder.date.setText(list?.get(position)?.post_date.toString())
        Picasso.get().load(list?.get(position)?.User?.avatar_urls?.o).placeholder(R.drawable.person)
            .into(holder.ProfileImage)
        holder.title.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Log.d("TAG", "clicked ${list[position].title}")
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                var transaction=MyDataClass.getTransaction()
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    ShowPostsOfThreads(list[position].last_post_username, list.get(position).title,title,list.get(position).thread_id))
                transaction.addToBackStack(null).commit()
            }
        })
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    class ShowDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ProfileImage: CircleImageView =
            itemView.findViewById(R.id.show_details_custom_layout_UserProfileImage)
        var title: TextView = itemView.findViewById(R.id.show_details_custom_layout_title_Tv)
        var lastPostUserName: TextView =
            itemView.findViewById(R.id.show_details_custom_layout_lastPostUserName_Tv)
        var replies: TextView = itemView.findViewById(R.id.show_details_custom_layout_replies_tv)
        var date: TextView = itemView.findViewById(R.id.show_details_custom_layout_date_tv)

    }
}