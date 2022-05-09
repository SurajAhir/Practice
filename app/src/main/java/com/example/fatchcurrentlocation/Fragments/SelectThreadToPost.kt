package com.example.fatchcurrentlocation.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.SelectThreadShowNodesAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Node
import com.example.fatchcurrentlocation.DataClasses.NodesData1
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.Home
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentSelectThreadToPostBinding
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class SelectThreadToPost : Fragment() {
lateinit var binding:FragmentSelectThreadToPostBinding
    var responseDataObject: ResponseDataClass? = null
    lateinit var goUserProfile: CircleImageView
    lateinit var goUserEmail: ImageButton
    lateinit var goUserNotification: ImageButton
    lateinit var goUserElectric: ImageButton
    lateinit var goUserSearch: ImageButton
    var isClickedList: ArrayList<Boolean> = ArrayList()
    lateinit var list: List<NodesData1>
    lateinit var treeMap: TreeMap<String, List<Int>>
    lateinit var listGeneral: LinkedList<NodesData1>
    lateinit var listBanking: LinkedList<NodesData1>
    lateinit var listOffer: LinkedList<NodesData1>
    lateinit var listPersonal: LinkedList<NodesData1>
    lateinit var listTravelling: LinkedList<NodesData1>
    lateinit var listForeign: LinkedList<NodesData1>
    var valid1: Boolean = false
    var valid2: Boolean = false
    var valid3: Boolean = false
    var valid4: Boolean = false
    var valid5: Boolean = false
    var valid6: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
      binding=FragmentSelectThreadToPostBinding.inflate(layoutInflater, container, false)
        initialize()
        fetchDatFromApi()
        binding.selectThreadToPostGoBackBtn.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
               startActivity(Intent(context,Home()::class.java))
            }
        })
        return binding.root
    }
    private fun fetchDatFromApi() {
        var list1ForChildNode: LinkedHashMap<Int, ArrayList<NodesData1>> = LinkedHashMap()
        var list1ForParentNode: LinkedHashMap<Int, ArrayList<NodesData1>> = LinkedHashMap()
        var list1ForChildSubNode: LinkedHashMap<Int, ArrayList<NodesData1>> = LinkedHashMap()
        var arrayListForChildNode: ArrayList<NodesData1>
        var arrayListForParentNode: ArrayList<NodesData1>
        var arrayListForChildSubNode: ArrayList<NodesData1>
        var index = 0
        var indexForSubNode = 0
        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getNodesResponse("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4").enqueue(object : Callback<Node> {
            override fun onResponse(call: Call<Node>, response: Response<Node>) {
                list = response.body()!!.nodes
                treeMap = response.body()!!.tree_map
                for (j in treeMap.get("0")!!) {
                    index = j
                    arrayListForChildNode = ArrayList()
                    arrayListForParentNode = ArrayList()
                    arrayListForChildSubNode = ArrayList()
                    var listForSubNodeId: ArrayList<Int> = ArrayList()
                    for (i in 0..list.size - 1) {
                        if (list.get(i).parent_node_id.equals(j)) {
                            arrayListForChildNode.add(list.get(i))
                            listForSubNodeId.add(list.get(i).node_id)
                        }
                        if (list.get(i).node_id.equals(j)) {
                            arrayListForParentNode.add(list.get(i))
                        }
                    }
                    for (id in listForSubNodeId) {
                        for (i in 0..list.size - 1) {
                            if (list.get(i).parent_node_id == id) {
                                arrayListForChildSubNode.add(list.get(i))
                                indexForSubNode = id
                                Log.d("TAG","matched")
                            }
                        }
                        if (!arrayListForChildSubNode.isEmpty()) {
                            list1ForChildSubNode.put(indexForSubNode, arrayListForChildSubNode)
                        }
                    }
                    list1ForChildNode.put(index, arrayListForChildNode)
                    list1ForParentNode.put(index, arrayListForParentNode)

                }
                Log.d("TAG","size ${list1ForChildSubNode.size}")
                var parentNodeId = treeMap.get("0")
                for (i in 0..(parentNodeId?.size!! - 1)) {
                    isClickedList.add(false)
                }
                binding.selectThreadToPostRecyclerView.adapter =
                    treeMap.get("0")?.let {
                        context?.let { it1 ->
                            SelectThreadShowNodesAdapter(it1,
                                it,
                                list1ForChildNode,
                                list1ForParentNode, isClickedList,list1ForChildSubNode)
                        }
                    }
                binding.selectThreadToPostRecyclerView.layoutManager = LinearLayoutManager(context)

            }

            override fun onFailure(call: Call<Node>, t: Throwable) {
                Log.d("TAG", t.localizedMessage)
                Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
    }
//    private fun fetchDataFromApi() {
//        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
//        var api: HitApi = retrofit.create(HitApi::class.java)
//        api.getNodesResponse("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4").enqueue(object : Callback<Node> {
//            override fun onResponse(call: Call<Node>, response: Response<Node>) {
//                Log.d("TAG", "Suraj$response.code().toString()")
//                list = response.body()!!.nodes
//                for (i in 0..list.size - 1) {
//                    if (list[i].parent_node_id.equals(15)) {
//                        listGeneral.add(list[i])
//                    }
//                    if (list[i].parent_node_id.equals(1)) {
//                        listBanking.add(list[i])
//                    }
//                    if (list[i].parent_node_id.equals(3)) {
//                        listOffer.add(list[i])
//                    }
//                    if (list[i].parent_node_id.equals(11)) {
//                        listPersonal.add(list[i])
//                    }
//                    if (list[i].parent_node_id.equals(18)) {
//                        listTravelling.add(list[i])
//                    }
//                    if (list[i].parent_node_id.equals(21)) {
//                        listForeign.add(list[i])
//                    }
//                }
//
//            }
//
//            override fun onFailure(call: Call<Node>, t: Throwable) {
//                Log.d("TAG", t.localizedMessage)
//                Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
//            }
//        })
//    }

    private fun initialize() {
        MyDataClass.isPostThread=true
        listGeneral = LinkedList()
        listBanking = LinkedList()
        listOffer = LinkedList()
        listPersonal = LinkedList()
        listTravelling = LinkedList()
        listForeign = LinkedList()
    }
}