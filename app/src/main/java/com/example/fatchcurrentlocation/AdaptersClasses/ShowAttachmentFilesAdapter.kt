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
import com.example.fatchcurrentlocation.R

class ShowAttachmentFilesAdapter(
    val context: Context,
    val listItem: ArrayList<String>,
    val listItemId: ArrayList<Int>,
) :
    RecyclerView.Adapter<ShowAttachmentFilesAdapter.ShowAttachmentFilesViewHolder>() {

    class ShowAttachmentFilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView =
            itemView.findViewById(R.id.post_comments_attachmentFileShow_customLayout_tv)
        var itemCounting: TextView =
            itemView.findViewById(R.id.post_comments_attachmentFileShowCounting_customLayout_tv)
        var itemRemoveBtn: TextView =
            itemView.findViewById(R.id.post_comments_attachmentFileRemove_customLayout_btn)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShowAttachmentFilesViewHolder {
        var view = LayoutInflater.from(context)
            .inflate(R.layout.show_attachments_file_custom_layout, parent, false)
        return ShowAttachmentFilesViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ShowAttachmentFilesViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.itemCounting.setText("${position + 1}.")
        Log.d("TAG", "helo hti si ahir")
        holder.itemName.setText(listItem.get(position))
        holder.itemRemoveBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                listItem.removeAt(position)
                listItemId.removeAt(position)
                MyDataClass.funAdapter()

            }
        })
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}