package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.NodesData1
import com.example.fatchcurrentlocation.Fragments.ShowDetails
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.ShowChildSubNodes
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class NodesAdatperClass(
    val _context: Context,
    val list:
    ArrayList<NodesData1>,
    val btn_text: String,
    val list1ForChildSubNode: LinkedHashMap<Int, ArrayList<NodesData1>>,
) : RecyclerView.Adapter<NodesAdatperClass.NodesViewHolder>() {
    val context = _context

    class NodesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.custom_layout_general_discussion_title_tv)
        var threads: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_threads_tv)
        var message: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_messages_tv)
        var lastThreadTitle: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_last_thread_title_tv)
        var postDate: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_last_post_date_tv)
        var lastPostUserName: TextView =
            itemView.findViewById(R.id.custom_layout_general_discussion_last_post_UserName_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodesViewHolder {
        var view =
            LayoutInflater.from(context).inflate(R.layout.recyclerview_custom_layout, parent, false)
        return NodesViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: NodesViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.title.setText(list?.get(position)?.title)

        if (list?.get(position)?.type_data.last_post_username != null) {
            holder.lastPostUserName.setText(list?.get(position)?.type_data.last_post_username)
        }
        if (!list?.get(position)?.type_data.last_post_date.toString().equals("0")) {
            var lastActivityDate =
                Date((list?.get(position)?.type_data.last_post_date as Long) * 1000)
            convertTimeIntoSimpleMinuts(lastActivityDate, holder)
        }
        if (list?.get(position)?.type_data.last_thread_title != null) {
            holder.lastThreadTitle.setText(list?.get(position)?.type_data.last_thread_title)
        }
        if (list?.get(position)?.type_data.message_count != null) {
            holder.message.setText("Messages: ${list?.get(position)?.type_data.message_count.toString()}")
        }
        if (list?.get(position)?.type_data.discussion_count != null) {
            holder.threads.setText("Threads: ${list?.get(position)?.type_data.discussion_count.toString()}")
        }

        if (list1ForChildSubNode.get(list.get(position).node_id) != null) {
            var arrayList = list1ForChildSubNode.get(list.get(position).node_id)
            if (arrayList != null) {
                var totalThread = 0
                var totalMessage = 0
                for (i in arrayList) {
                    totalThread += i.type_data.discussion_count
                    totalMessage += i.type_data.message_count
                }
                holder.threads.setText("thread $totalThread")
                holder.message.setText("Messages:${totalMessage}")
                holder.lastThreadTitle.setText(arrayList.get(position).type_data.last_thread_title)
                holder.lastPostUserName.setText(arrayList.get(position).type_data.last_post_username)
                var lastActivityDate =
                    Date((arrayList?.get(0)?.type_data.last_post_date as Long) * 1000)
                convertTimeIntoSimpleMinuts(lastActivityDate, holder)
            }
        }
        holder.title.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var transaction = MyDataClass.getTransaction()
                if (transaction != null) {
                    if (list1ForChildSubNode.get(list.get(position).node_id)
                            ?.get(0)?.parent_node_id != null && list1ForChildSubNode.get(list.get(
                            position).node_id)?.get(0)?.parent_node_id == list.get(position).node_id
                    ) {
                        MyDataClass.homeNestedScrollView.visibility = View.GONE
                        MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                        transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                            ShowChildSubNodes(btn_text,
                                list.get(position).title,
                                list.get(position).description,
                                list1ForChildSubNode.get(list.get(position).node_id)!!,
                                1001))
                        transaction.addToBackStack(null).commit()
                    }else{
                        MyDataClass.homeNestedScrollView.visibility = View.GONE
                        MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                        transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                            ShowDetails(list[position].node_id,
                                btn_text,
                                list[position].description,
                                list[position].title,
                                list.get(position).type_data,
                              ))
                        transaction.addToBackStack(null).commit()
                    }

                }
            }
        })

    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    private fun convertTimeIntoSimpleMinuts(lastActivityDate: Date, holder: NodesViewHolder) {
        var currentTime = Date(System.currentTimeMillis())
        if (currentTime.year == lastActivityDate.year) {
            if (lastActivityDate.month == currentTime.month) {
                if (lastActivityDate.date == currentTime.date) {
                    if (lastActivityDate.hours == currentTime.hours) {
                        if (currentTime.minutes - lastActivityDate.minutes < 2) {
                            holder.postDate.setText("a moment ago")
                        } else {
                            holder.postDate.setText("${currentTime.minutes - lastActivityDate.minutes} minuts ago")
                        }
                    } else if (lastActivityDate.hours < currentTime.hours) {
                        var timeInMinuts =
                            (currentTime.hours - lastActivityDate.hours) * 60 + currentTime.minutes
                        if (timeInMinuts - lastActivityDate.minutes < 60) {
                            holder.postDate.setText("${Math.abs(timeInMinuts - lastActivityDate.minutes)} minuts ago")
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): Int {
        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("d")
        val now: LocalDateTime = LocalDateTime.now()
        return dtf.format(now) as Int
    }
}

