package com.example.fatchcurrentlocation.AdaptersClasses

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.Fragments.ShowUserImage
import com.example.fatchcurrentlocation.R
import java.util.*

class ShowAttachmentImageAdapter(val list: LinkedList<Bitmap>, val context: Context,val position: Int) :
    RecyclerView.Adapter<ShowAttachmentImageAdapter.ShowAttachmentImageViewHolder>() {

    class ShowAttachmentImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.show_image_customLayout_imageview)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShowAttachmentImageViewHolder {
        var view =
            LayoutInflater.from(context).inflate(R.layout.show_image_custom_layout, parent, false)
        return ShowAttachmentImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowAttachmentImageViewHolder, position: Int) {
//        Picasso.get().load(list.get(position).toString()).placeholder(R.drawable.ic_no_image).into(holder.image)
        holder.image.setImageBitmap(list.get(position))
        holder.image.setOnClickListener { MyDataClass.homeNestedScrollView.visibility=View.GONE
        MyDataClass.homeFragmentContainerView.visibility=View.VISIBLE
        var transaction=MyDataClass.getTransaction()
        if(transaction!=null){
            MyDataClass.isJumpedToImage=true
            MyDataClass.JumpToImagePosition=this.position
            MyDataClass.userFragmentImage=list.get(position)
            MyDataClass.userFragmentRequestCode=101
            transaction.replace(R.id.home_fragment_containerViewForShowDetails,ShowUserImage())
            transaction.addToBackStack(null).commit()
        }}
//       holder.image.loadUrl(list.get(position))
//        holder.image.webViewClient=WebviewClient()
//        val settings: WebSettings = holder.image.getSettings()
//        settings.allowContentAccess = true
//        settings.javaScriptCanOpenWindowsAutomatically = true
//        settings.javaScriptEnabled = true
//        settings.setSupportMultipleWindows(true)
//        settings.setSupportZoom(true)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}