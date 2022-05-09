package com.example.fatchcurrentlocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.ShowChildSubNodeAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.NodesData1
import com.example.fatchcurrentlocation.databinding.FragmentShowChildSubNodesBinding

class ShowChildSubNodes(
    val btn_text: String,
    val title: String,
    val description: String,
    val listOfChildSubNodes: ArrayList<NodesData1>,
    val requestCode: Int,
) : Fragment() {
    lateinit var binding: FragmentShowChildSubNodesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentShowChildSubNodesBinding.inflate(layoutInflater, container, false)
        initializeData()
        binding.showChildSubNodesCategory.setOnClickListener { MyDataClass.onBack() }
        return binding.root
    }

    private fun initializeData() {
        if (requestCode == 1002) {
            MyDataClass.isPostThread = false
        }
        if (requestCode == 1001) {
            MyDataClass.isEnteredInShowDetails = true
        }
        binding.showChildSubNodesCategory.setText(btn_text)
        binding.showChildSubNodesDescription.setText(description)
        binding.showChildSubNodesTitle.setText(title)
        if (!listOfChildSubNodes.isEmpty()) {
            binding.showChildSubNodesRecyclerView.adapter =
                context?.let {
                    ShowChildSubNodeAdapter(it,
                        listOfChildSubNodes,
                        btn_text,
                        requestCode)
                }
            binding.showChildSubNodesRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.showChildSubNodesProgressBar.visibility = View.GONE
        } else {
            binding.showChildSubNodesLinearLayoutForDetails.visibility = View.GONE
        }
    }

}