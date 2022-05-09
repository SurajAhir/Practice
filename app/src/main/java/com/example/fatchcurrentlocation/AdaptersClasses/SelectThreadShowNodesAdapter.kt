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
import com.example.fatchcurrentlocation.DataClasses.NodesData1
import com.example.fatchcurrentlocation.R
import java.util.*
import kotlin.collections.ArrayList

class SelectThreadShowNodesAdapter(
    val context: Context,
    val listOfParentNodes: List<Int>,
    val list1ForChildNodeData: LinkedHashMap<Int, ArrayList<NodesData1>>,
    val list1ForParentNodeData: LinkedHashMap<Int, ArrayList<NodesData1>>,
    val isClickedList: ArrayList<Boolean>,
    val list1ForChildSubNode: LinkedHashMap<Int, java.util.ArrayList<NodesData1>>,
) : RecyclerView.Adapter<SelectThreadShowNodesAdapter.SelectThreadToPostViewHolder>() {
    var listOfAll:ArrayList<NodesData1> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SelectThreadToPostViewHolder {

        var view =
            LayoutInflater.from(context).inflate(R.layout.show_nodes_custom_layout, parent, false)
        return SelectThreadToPostViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SelectThreadToPostViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {

//        listOfAll.clear()
        var obj = list1ForParentNodeData.get(listOfParentNodes.get(position))
        holder.title.setText(obj?.get(0)?.title)
        if (!obj?.get(0)?.description?.isEmpty()!!) {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(obj?.get(0)?.description)

        }
        var obj1 = list1ForChildNodeData.get(listOfParentNodes.get(position))
//        if (obj1 != null) {
//            listOfAll.addAll(obj1)
//            for (id in obj1) {
//                var listOfSubNode = list1ForChildSubNode.get(id.node_id)
//                if (listOfSubNode!= null) {
//                    listOfAll.addAll(listOfSubNode)
////                    holder.rightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
////                    isClickedList.set(position, true)
////                    holder.recyclerView.visibility = View.VISIBLE
////                    holder.recyclerView.adapter = listOfSubNode?.let {
////                        SelectThreadToPostAdapter(context,
////                            it,
////                            holder.title.text.toString())
////                    }
////                    holder.recyclerView.layoutManager = LinearLayoutManager(context)
//                }
//
//            }
//        }
        holder.rightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
        isClickedList.set(position, true)
        holder.recyclerView.visibility = View.VISIBLE
        holder.recyclerView.adapter = obj1?.let {
            SelectThreadToPostAdapter(context,
                it,
                holder.title.text.toString(),list1ForChildSubNode)
        }
        holder.recyclerView.layoutManager = LinearLayoutManager(context)

        holder.rightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isClickedList.get(position)) {
                    isClickedList.set(position, false)
                    holder.recyclerView.visibility = View.GONE
                    holder.rightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                } else {
                    var obj = list1ForChildNodeData.get(listOfParentNodes.get(position))
                    holder.rightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    isClickedList.set(position, true)
                    holder.recyclerView.visibility = View.VISIBLE
                    holder.recyclerView.adapter = obj?.let {
                        SelectThreadToPostAdapter(context,
                            it,
                            holder.title.text.toString(),list1ForChildSubNode)
                    }
                    holder.recyclerView.layoutManager = LinearLayoutManager(context)

                }
            }
        })

    }

    override fun getItemCount(): Int {
        return listOfParentNodes.size
    }

    class SelectThreadToPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.show_nodes_custom_layout_title_tv)
        var description: TextView =
            itemView.findViewById(R.id.show_nodes_custom_layout_description_tv)
        var rightArrowBtn: ImageButton =
            itemView.findViewById(R.id.show_nodes_custom_layout_rightArrow_btn)
        var recyclerView: RecyclerView =
            itemView.findViewById(R.id.show_nodes_custom_layout_recyclerView)
    }
}