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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.AdaptersClasses.NodesAdatperClass
import com.example.fatchcurrentlocation.AdaptersClasses.SelectThreadToPostAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Node
import com.example.fatchcurrentlocation.DataClasses.NodesData1
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.Home
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.ActivityHomeBinding
import com.example.fatchcurrentlocation.databinding.FragmentSelectThreadToPostBinding
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
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
    lateinit var listGeneral: LinkedList<NodesData1>
    lateinit var list: List<NodesData1>
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
        fetchDataFromApi()
        binding.selectThreadToPostGeneralRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid1) {
                    binding.selectThreadToPostGeneralRecyclerView.visibility = View.GONE
                    binding.selectThreadToPostGeneralRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                    valid1 = false
                } else {
                    binding.selectThreadToPostGeneralRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    valid1 = true
                    binding.selectThreadToPostGeneralRecyclerView.visibility = View.VISIBLE
                    binding.selectThreadToPostGeneralRecyclerView.adapter = context?.let {
                        SelectThreadToPostAdapter(
                            it,
                            listGeneral,
                        )
                    }
                    binding.selectThreadToPostGeneralRecyclerView.layoutManager = LinearLayoutManager(context)
                }
            }
        })
        binding.selectThreadToPostBankingRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid2) {
                    valid2 = false
                    binding.selectThreadToPostBankingRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                    binding.selectThreadToPostBankingRecyclerView.visibility = View.GONE
                } else {
                    valid2 = true
                    binding.selectThreadToPostBankingRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    binding.selectThreadToPostBankingRecyclerView.visibility = View.VISIBLE
                   binding.selectThreadToPostBankingRecyclerView.adapter = context?.let {
                       SelectThreadToPostAdapter(
                           it,
                           listBanking,
                           )
                   }
                   binding.selectThreadToPostBankingRecyclerView.layoutManager = LinearLayoutManager(context)
                }
            }
        })
        binding.selectThreadToPostOffersRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid3) {
                    valid3 = false
                    binding.selectThreadToPostOffersRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                    binding.selectThreadToPostOffersRecyclerView.visibility = View.GONE
                } else {
                    valid3 = true
                    binding.selectThreadToPostOffersRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                     binding.selectThreadToPostOffersRecyclerView.visibility = View.VISIBLE
                     binding.selectThreadToPostOffersRecyclerView.adapter = context?.let {
                         SelectThreadToPostAdapter(
                             it,
                             listOffer,
                         )
                     }
                    binding.selectThreadToPostOffersRecyclerView.layoutManager = LinearLayoutManager(context)
                }
            }
        })
        binding.selectThreadToPostPersonalFinaceRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid4) {
                    valid4 = false
                    binding.selectThreadToPostPersonalFinaceRecyclerView.visibility = View.GONE
                    binding.selectThreadToPostPersonalFinaceRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                } else {
                    valid4 = true
                    binding.selectThreadToPostPersonalFinaceRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    binding.selectThreadToPostPersonalFinaceRecyclerView.visibility = View.VISIBLE
                    binding.selectThreadToPostPersonalFinaceRecyclerView.adapter =
                        context?.let {
                            SelectThreadToPostAdapter(
                                it,
                                listPersonal,
                            )
                        }
                    binding.selectThreadToPostPersonalFinaceRecyclerView.layoutManager = LinearLayoutManager(context)
                }
            }
        })
        binding.selectThreadToPostTravellingRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid5) {
                    valid5 = false
                    binding.selectThreadToPostTravellingRecyclerView.visibility = View.GONE
                    binding.selectThreadToPostTravellingRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                } else {
                    valid5 = true
                    binding.selectThreadToPostTravellingRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    binding.selectThreadToPostTravellingRecyclerView.visibility = View.VISIBLE
                    binding.selectThreadToPostTravellingRecyclerView.adapter =
                        context?.let {
                            SelectThreadToPostAdapter(
                                it,
                                listTravelling,
                            )
                        }
                    binding.selectThreadToPostTravellingRecyclerView.layoutManager = LinearLayoutManager(context)
                }
            }
        })
        binding.selectThreadToPostForeignCreditArrowbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid6) {
                    valid6 = false
                    binding.selectThreadToPostForeignCreditRecyclerView.visibility = View.GONE
                    binding.selectThreadToPostForeignCreditArrowbtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                } else {
                    valid6 = true
                    binding.selectThreadToPostForeignCreditArrowbtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    binding.selectThreadToPostForeignCreditRecyclerView.visibility = View.VISIBLE
                    binding.selectThreadToPostForeignCreditRecyclerView.adapter =
                        context?.let {
                            SelectThreadToPostAdapter(
                                it,
                                listForeign,
                            )
                        }
                    binding.selectThreadToPostForeignCreditRecyclerView.layoutManager = LinearLayoutManager(context)
                }
            }
        })
        binding.selectThreadToPostGoBackBtn.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
               startActivity(Intent(context,Home()::class.java))
            }
        })
        return binding.root
    }

    private fun fetchDataFromApi() {
        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getNodesResponse("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4").enqueue(object : Callback<Node> {
            override fun onResponse(call: Call<Node>, response: Response<Node>) {
                Log.d("TAG", "Suraj$response.code().toString()")
                list = response.body()!!.nodes
                for (i in 0..list.size - 1) {
                    if (list[i].parent_node_id.equals(15)) {
                        listGeneral.add(list[i])
                    }
                    if (list[i].parent_node_id.equals(1)) {
                        listBanking.add(list[i])
                    }
                    if (list[i].parent_node_id.equals(3)) {
                        listOffer.add(list[i])
                    }
                    if (list[i].parent_node_id.equals(11)) {
                        listPersonal.add(list[i])
                    }
                    if (list[i].parent_node_id.equals(18)) {
                        listTravelling.add(list[i])
                    }
                    if (list[i].parent_node_id.equals(21)) {
                        listForeign.add(list[i])
                    }
                }

            }

            override fun onFailure(call: Call<Node>, t: Throwable) {
                Log.d("TAG", t.localizedMessage)
                Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initialize() {
        listGeneral = LinkedList()
        listBanking = LinkedList()
        listOffer = LinkedList()
        listPersonal = LinkedList()
        listTravelling = LinkedList()
        listForeign = LinkedList()
    }
}