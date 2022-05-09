package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.NodesData1
import com.example.fatchcurrentlocation.R
import java.util.*
import kotlin.collections.ArrayList

class ShowNodesAdapter(
    val context: Context,
    val listOfParentNodes: List<Int>,
    val list1ForChildNodeData: LinkedHashMap<Int, ArrayList<NodesData1>>,
    val list1ForParentNodeData: LinkedHashMap<Int, ArrayList<NodesData1>>,
    val isClickedList: ArrayList<Boolean>,
    val list1ForChildSubNode: LinkedHashMap<Int, ArrayList<NodesData1>>,
) : RecyclerView.Adapter<ShowNodesAdapter.ShowNodesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowNodesViewHolder {

        var view =
            LayoutInflater.from(context).inflate(R.layout.show_nodes_custom_layout, parent, false)
        return ShowNodesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowNodesViewHolder, @SuppressLint("RecyclerView") position: Int) {
        var obj = list1ForParentNodeData.get(listOfParentNodes.get(position))
        holder.title.setText(obj?.get(0)?.title)
        if (!obj?.get(0)?.description?.isEmpty()!!) {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(obj?.get(0)?.description)

        }

        MyDataClass.isClickedValueForBtn=true
if(MyDataClass.isClickedValueForBtn) {
    if (MyDataClass.isClickedList.get(position)) {
        var obj = list1ForChildNodeData.get(listOfParentNodes.get(position))
        holder.rightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
        holder.recyclerView.visibility = View.VISIBLE
        holder.recyclerView.adapter = obj?.let {
            NodesAdatperClass(context,
                it,
                holder.title.text.toString(), list1ForChildSubNode)
        }
        holder.recyclerView.layoutManager = LinearLayoutManager(context)
    }
}
            holder.recyclerView.layoutManager=LinearLayoutManager(context)
        holder.rightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (MyDataClass.isClickedList.get(position)) {
                    isClickedList.set(position,false)
                    MyDataClass.isClickedList.set(position,false)
                    holder.recyclerView.visibility = View.GONE
                    holder.rightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                } else {
                    var obj=list1ForChildNodeData.get(listOfParentNodes.get(position))
                    holder.rightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    isClickedList.set(position,true)
                    MyDataClass.isClickedList.set(position,true)
                    holder.recyclerView.visibility = View.VISIBLE
                    holder.recyclerView.adapter = obj?.let {
                        NodesAdatperClass(context,
                            it,
                            holder.title.text.toString(),
                            list1ForChildSubNode)
                    }
                    holder.recyclerView.layoutManager=LinearLayoutManager(context)

                }
            }
        })

    }

    override fun getItemCount(): Int {
        return listOfParentNodes.size
    }

    class ShowNodesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.show_nodes_custom_layout_title_tv)
        var description: TextView =
            itemView.findViewById(R.id.show_nodes_custom_layout_description_tv)
        var rightArrowBtn: ImageButton =
            itemView.findViewById(R.id.show_nodes_custom_layout_rightArrow_btn)
        var recyclerView: RecyclerView =
            itemView.findViewById(R.id.show_nodes_custom_layout_recyclerView)
    }
}