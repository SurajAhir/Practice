package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.ShowGridImageInFullView
import java.io.ByteArrayOutputStream


class ShowGridImageAdapter(val context: Context,var list: List<Bitmap>): RecyclerView.Adapter<ShowGridImageAdapter.ShowGridImageViewHolder>() {

    class ShowGridImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageView=itemView.findViewById<ImageView>(R.id.custom_show_grid_image_layout_imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowGridImageViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.custom_show_grid_image_layout,parent,false)
        return ShowGridImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowGridImageViewHolder, @SuppressLint("RecyclerView") position: Int) {
      holder.imageView.setImageBitmap(list.get(position))
        holder.imageView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                MyDataClass.gridImage=list.get(position)
                var intent=Intent(context,ShowGridImageInFullView::class.java)
                context.startActivity(intent)
            }
        })
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}