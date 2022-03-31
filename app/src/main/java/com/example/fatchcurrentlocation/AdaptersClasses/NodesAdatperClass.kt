package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.NodesData1
import com.example.fatchcurrentlocation.Fragments.ShowDetails
import com.example.fatchcurrentlocation.R
import java.util.*

class NodesAdatperClass(
    val _context: Context,
    val _list: LinkedList<NodesData1>,
    val _parentNodeId: Int,
    val layout: LinearLayout,
    val homeFragmentContainerViewForShowDetails: FragmentContainerView,
    val btn_text: String,
    val homeScrollBar: NestedScrollView
) : RecyclerView.Adapter<NodesAdatperClass.NodesViewHolder>() {
    val context = _context
    val list = _list
    var parentNodeId = _parentNodeId

    class NodesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.custom_layout_general_discussion_title_tv)
        var threads: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_threads_tv)
        var message: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_messages_tv)
        var lastThreadTitle: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_last_thread_title_tv)
        var lastPostTime: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_last_post_date_tv)
        var lastPostUserName: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_last_post_UserName_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodesViewHolder {
        var view =
            LayoutInflater.from(context).inflate(R.layout.recyclerview_custom_layout, parent, false)
        return NodesViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NodesViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.title.setText(list?.get(position)?.title)
        holder.lastPostUserName.setText(list!![position].type_data.last_post_username)
        holder.lastPostTime.setText(list[position].type_data.last_post_date.toString())
        holder.lastThreadTitle.setText(list[position].type_data.last_thread_title)
        holder.message.append(list[position].type_data.message_count.toString())
        holder.threads.append(list[position].type_data.discussion_count.toString())
        holder.title.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                layout.visibility = View.GONE
                homeScrollBar.visibility = View.GONE
                homeFragmentContainerViewForShowDetails.visibility = View.VISIBLE
                var transaction=MyDataClass.getTransaction()
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    ShowDetails(list[position].node_id,
                        btn_text,
                        list[position].description,
                        list[position].title))
               transaction.commit()
            }
        })

    }

    override fun getItemCount(): Int {
        return list?.size!!
    }
}

