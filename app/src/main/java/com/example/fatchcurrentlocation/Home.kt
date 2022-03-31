package com.example.fatchcurrentlocation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.AdaptersClasses.NodesAdatperClass
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Node
import com.example.fatchcurrentlocation.DataClasses.NodesData1
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.Fragments.YourAccount
import com.example.fatchcurrentlocation.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    var responseDataObject: ResponseDataClass? = null
    lateinit var goUserProfile: CircleImageView
    lateinit var goUserEmail: ImageButton
    lateinit var goUserNotification: ImageButton
    lateinit var goUserElectric: ImageButton
    lateinit var goUserSearch: ImageButton
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var listGeneral: LinkedList<NodesData1>
    lateinit var list: List<NodesData1>
    lateinit var listBanking: LinkedList<NodesData1>
    lateinit var listOffer: LinkedList<NodesData1>
    lateinit var listPersonal: LinkedList<NodesData1>
    lateinit var listTravelling: LinkedList<NodesData1>
    lateinit var listForeign: LinkedList<NodesData1>
    lateinit var recyclerViewGeneral: RecyclerView
    lateinit var recyclerViewBanking: RecyclerView
    lateinit var recyclerViewOffers: RecyclerView
    lateinit var recyclerViewPersonalFinance: RecyclerView
    lateinit var recyclerViewTravelling: RecyclerView
    lateinit var recyclerViewForeignCredit: RecyclerView
    var goProfile: Boolean = false
    var valid1: Boolean = false
    var valid2: Boolean = false
    var valid3: Boolean = false
    var valid4: Boolean = false
    var valid5: Boolean = false
    var valid6: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initialize()
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
                Toast.makeText(this@Home, t.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
        goUserProfile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (goProfile) {
                    goProfile = false
                    binding.homeFragmentContainerViewForShowDetails.visibility = View.GONE
                    binding.homeScrollBar.visibility=View.VISIBLE
                } else {
                    goProfile = true
                    binding.homeFragmentContainerViewForShowDetails.visibility = View.VISIBLE
                    binding.homeScrollBar.visibility=View.GONE
                    var fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                  fragmentTransaction.replace(R.id.home_fragment_containerViewForShowDetails,YourAccount(MyDataClass.responseDataClass))
                    fragmentTransaction.commit()
                }
            }
        })
        binding.homeGeneralRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid1) {
                    recyclerViewGeneral.visibility = View.GONE
                    binding.homeGeneralRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                    valid1 = false
                } else {
                    binding.homeGeneralRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    valid1 = true
                    recyclerViewGeneral.visibility = View.VISIBLE
                    recyclerViewGeneral.adapter = NodesAdatperClass(this@Home,
                        listGeneral,
                        15,
                        binding.linearLayout,
                        binding.homeFragmentContainerViewForShowDetails,
                        binding.homeGeneralTv.text.toString(),
                        binding.homeScrollBar)
                    recyclerViewGeneral.layoutManager = LinearLayoutManager(this@Home)
                }
            }
        })
        binding.homeBankingRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid2) {
                    valid2 = false
                    binding.homeBankingRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                    recyclerViewBanking.visibility = View.GONE
                } else {
                    valid2 = true
                    binding.homeBankingRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    recyclerViewBanking.visibility = View.VISIBLE
                    recyclerViewBanking.adapter = NodesAdatperClass(this@Home,
                        listBanking,
                        1,
                        binding.linearLayout,
                        binding.homeFragmentContainerViewForShowDetails,
                        binding.homeGeneralTv.text.toString(),
                        binding.homeScrollBar
                    )
                    recyclerViewBanking.layoutManager = LinearLayoutManager(this@Home)
                }
            }
        })
        binding.homeOffersRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid3) {
                    valid3 = false
                    binding.homeOffersRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                    recyclerViewOffers.visibility = View.GONE
                } else {
                    valid3 = true
                    binding.homeOffersRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    recyclerViewOffers.visibility = View.VISIBLE
                    recyclerViewOffers.adapter = NodesAdatperClass(this@Home,
                        listOffer,
                        3,
                        binding.linearLayout,
                        binding.homeFragmentContainerViewForShowDetails,
                        binding.homeGeneralTv.text.toString(),
                        binding.homeScrollBar)
                    recyclerViewOffers.layoutManager = LinearLayoutManager(this@Home)
                }
            }
        })
        binding.homePersonalFinaceRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid4) {
                    valid4 = false
                    recyclerViewPersonalFinance.visibility = View.GONE
                    binding.homePersonalFinaceRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                } else {
                    valid4 = true
                    binding.homePersonalFinaceRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    recyclerViewPersonalFinance.visibility = View.VISIBLE
                    recyclerViewPersonalFinance.adapter =
                        NodesAdatperClass(this@Home,
                            listPersonal,
                            11,
                            binding.linearLayout,
                            binding.homeFragmentContainerViewForShowDetails,
                            binding.homeGeneralTv.text.toString(),
                            binding.homeScrollBar)
                    recyclerViewPersonalFinance.layoutManager = LinearLayoutManager(this@Home)
                }
            }
        })
        binding.homeTravellingRightArrowBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid5) {
                    valid5 = false
                    recyclerViewTravelling.visibility = View.GONE
                    binding.homeTravellingRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                } else {
                    valid5 = true
                    binding.homeTravellingRightArrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    recyclerViewTravelling.visibility = View.VISIBLE
                    recyclerViewTravelling.adapter =
                        NodesAdatperClass(this@Home,
                            listTravelling,
                            18,
                            binding.linearLayout,
                            binding.homeFragmentContainerViewForShowDetails,
                            binding.homeGeneralTv.text.toString(),
                            binding.homeScrollBar)
                    recyclerViewTravelling.layoutManager = LinearLayoutManager(this@Home)
                }
            }
        })
        binding.homeForeignCreditArrowbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (valid6) {
                    valid6 = false
                    recyclerViewForeignCredit.visibility = View.GONE
                    binding.homeForeignCreditArrowbtn.setBackgroundResource(R.drawable.ic_arrow_right_24)
                } else {
                    valid6 = true
                    binding.homeForeignCreditArrowbtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_24)
                    recyclerViewForeignCredit.visibility = View.VISIBLE
                    recyclerViewForeignCredit.adapter =
                        NodesAdatperClass(this@Home,
                            listForeign,
                            21,
                            binding.linearLayout,
                            binding.homeFragmentContainerViewForShowDetails,
                            binding.homeGeneralTv.text.toString(), binding.homeScrollBar)
                    recyclerViewForeignCredit.layoutManager = LinearLayoutManager(this@Home)
                }
            }
        })


    }

    private fun initialize() {
        MyDataClass.homeFragmentContainerView = binding.homeFragmentContainerViewForShowDetails
        MyDataClass.homeNestedScrollView = binding.homeScrollBar
        MyDataClass.getTransaction = ::getFragmentTransaction
        responseDataObject = intent.getSerializableExtra("responseDataObject") as ResponseDataClass?
        goUserElectric = findViewById(R.id.home_electric)
        goUserEmail = findViewById(R.id.home_email)
        goUserNotification = findViewById(R.id.home_notification)
        goUserSearch = findViewById(R.id.home_search)
        goUserProfile = findViewById(R.id.home_userAccountProfile)
        drawerLayout = findViewById(R.id.home_drawer)
        recyclerViewGeneral = findViewById(R.id.home_general_recyclerView)
        recyclerViewBanking = findViewById(R.id.home_banking_recyclerView)
        recyclerViewOffers = findViewById(R.id.home_offers_recyclerView)
        recyclerViewPersonalFinance = findViewById(R.id.home_personalFinace_recyclerView)
        recyclerViewTravelling = findViewById(R.id.home_travelling_recyclerView)
        recyclerViewForeignCredit = findViewById(R.id.home_foreign_credit_recyclerView)
        navigationView = findViewById(R.id.home_navigation_view)
        listGeneral = LinkedList()
        listBanking = LinkedList()
        listOffer = LinkedList()
        listPersonal = LinkedList()
        listTravelling = LinkedList()
        listForeign = LinkedList()
        toolbar = findViewById(R.id.home_toolbar)
        toggle = ActionBarDrawerToggle(this@Home,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if(goProfile){
            goProfile=false
            startActivity(Intent(this, Home().javaClass))
                finish()
        }else if (count == 0) {
            if (MyDataClass.countFrag == -1) {
                super.onBackPressed()
            } else {
                MyDataClass.countFrag--
                startActivity(Intent(this, Home().javaClass))
                finish()
            }

        } else {
            supportFragmentManager.popBackStack()
        }
    }

    fun getFragmentTransaction(): FragmentTransaction {
        return supportFragmentManager.beginTransaction()
    }
}