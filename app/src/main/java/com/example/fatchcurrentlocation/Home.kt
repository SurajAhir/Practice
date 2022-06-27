package com.example.fatchcurrentlocation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.ShowNodesAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.ShowPostsOfThreadsAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.UserProfileAdapter
import com.example.fatchcurrentlocation.DataClasses.*
import com.example.fatchcurrentlocation.Fragments.*
import com.example.fatchcurrentlocation.ReactionDialogWork.ReactionDialogClass
import com.example.fatchcurrentlocation.ReactionDialogWork.ReactionListener
import com.example.fatchcurrentlocation.databinding.ActivityHomeBinding
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.DateFormat
import java.util.*
import kotlin.concurrent.thread


class Home : AppCompatActivity(),
    ReactionListener, DatePickerDialog.OnDateSetListener {
    private var mInterstitialAd: InterstitialAd? = null
    var adRequest: AdRequest? = null
    var TAG = "TAG"
    lateinit var mInterstitialAdLoadCallback: InterstitialAdLoadCallback
    lateinit var binding: ActivityHomeBinding
    var responseDataObject: ResponseDataClass? = null
    lateinit var goUserProfile: CircleImageView
    lateinit var goUserNotification: ImageButton
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var toggle: ActionBarDrawerToggle
    var isClickedList: ArrayList<Boolean> = ArrayList()
    lateinit var list: List<NodesData1>
    lateinit var treeMap: TreeMap<String, List<Int>>
    var goProfile: Boolean = false
    var goNotification: Boolean = false
    var goConversation: Boolean = false

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.clear()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkDevicePermission()
        initialize()
        mobileAdInitialize()
        initializeAddObject()
        fetchDatFromApi()
        fetchTotalAlertsCounts()
        fetchTotalConversationsCounts()
        binding.homeUserAccountProfileTv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (goProfile) {
                    goProfile = false
                    MyDataClass.homeFragmentContainerView.visibility = View.GONE
                    MyDataClass.homeNestedScrollView.visibility = View.VISIBLE
                    startActivity(Intent(this@Home, Home().javaClass))
                } else {
                    goProfile = true
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    var fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    MyDataClass.responseDataClass?.let { YourAccount(it) }?.let {
                        fragmentTransaction.replace(R.id.home_fragment_containerViewForShowDetails,
                            it)
                    }
                    fragmentTransaction.addToBackStack(null).commit()
                }
            }
        })
        goUserProfile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (goProfile) {
                    goProfile = false
                    MyDataClass.homeFragmentContainerView.visibility = View.GONE
                    MyDataClass.homeNestedScrollView.visibility = View.VISIBLE
                    startActivity(Intent(this@Home, Home().javaClass))
                } else {
                    goProfile = true
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    var fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    MyDataClass.responseDataClass?.let { YourAccount(it) }?.let {
                        fragmentTransaction.replace(R.id.home_fragment_containerViewForShowDetails,
                            it)
                    }
                    fragmentTransaction.addToBackStack(null).commit()
                }
            }
        })
        binding.homePostThread.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                Log.d("TAG", "POSTTHREAD BUTTON")
                var transaction = MyDataClass.getTransaction()
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    SelectThreadToPost())
                transaction.addToBackStack(null).commit()
            }
        })
        binding.homeNotification.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (goNotification) {
                    goNotification = false
                    MyDataClass.homeFragmentContainerView.visibility = View.GONE
                    MyDataClass.homeNestedScrollView.visibility = View.VISIBLE
                    startActivity(Intent(this@Home, Home().javaClass))
                } else {
                    goNotification = true
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    var fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_fragment_containerViewForShowDetails,
                        Notification())
                    fragmentTransaction.addToBackStack(null).commit()
                }
            }
        })
        binding.homeEmail.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (goConversation) {
                    goConversation = false
                    MyDataClass.homeFragmentContainerView.visibility = View.GONE
                    MyDataClass.homeNestedScrollView.visibility = View.VISIBLE
                    startActivity(Intent(this@Home, Home().javaClass))
                } else {
                    goConversation = true
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    var fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_fragment_containerViewForShowDetails,
                        ShowConversations())
                    fragmentTransaction.addToBackStack(null).commit()
                }
            }
        })
        navigationView.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {

                when (item.itemId) {
                    R.id.whatsNew -> {
                        var transaction = supportFragmentManager.beginTransaction()
                        MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                        MyDataClass.homeNestedScrollView.visibility = View.GONE
                        transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                            ShowLatestPosts())
                        transaction.addToBackStack(null).commit()
                        drawerLayout.closeDrawer(Gravity.LEFT)
                    }
                    R.id.youtube -> {
                        val url = "http://YouTube.com/c/Technofino"
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                        drawerLayout.closeDrawer(Gravity.LEFT)
                    }
                    R.id.messages -> {
                        goConversation = true
                        MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                        MyDataClass.homeNestedScrollView.visibility = View.GONE
                        var fragmentTransaction: FragmentTransaction =
                            supportFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.home_fragment_containerViewForShowDetails,
                            ShowConversations())
                        fragmentTransaction.addToBackStack(null).commit()
                        drawerLayout.closeDrawer(Gravity.LEFT)
                    }
                    R.id.home -> {
                        startActivity(Intent(this@Home, Home()::class.java))
                        finish()
                        drawerLayout.closeDrawer(Gravity.LEFT)
                    }
                    R.id.contactsUs->{
                        val url = "https://www.technofino.in/community/misc/contact"
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                        drawerLayout.closeDrawer(Gravity.LEFT)
                    }

                }
                return true
            }
        })
    }

    private fun fetchTotalConversationsCounts() {
var retrofit= RetrofitManager.getRetrofit1()
        var api=retrofit.create(HitApi::class.java)
        api.getUnViewedConversations(MyDataClass.api_key,MyDataClass.myUserId,true).enqueue(object :Callback<ResponseDataClass>{
            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>
            ) {
                if(response.isSuccessful){
                    var pagination:Pagination?=response.body()?.pagination
                    if(pagination?.total!!>0){
                        binding.homeShowTotalConversationsTv.visibility=View.VISIBLE
                        binding.homeShowTotalConversationsTv.setText(pagination.total.toString())
                    }else{
                        binding.homeShowTotalConversationsTv.visibility=View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
               Log.d("TAG",t.localizedMessage)
            }
        })
    }

    private fun fetchTotalAlertsCounts() {
        var retrofit= RetrofitManager.getRetrofit1()
        var api=retrofit.create(HitApi::class.java)
        api.getUnViewedAlerts(MyDataClass.api_key,MyDataClass.myUserId,true).enqueue(object :Callback<ResponseDataClass>{
            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>
            ) {
                if(response.isSuccessful){
                    var pagination:Pagination?=response.body()?.pagination
                    if(pagination?.total!!>0){
                        binding.homeShowTotatAlertsTv.visibility=View.VISIBLE
                        binding.homeShowTotatAlertsTv.setText(pagination.total.toString())
                    }else{
                        binding.homeShowTotatAlertsTv.visibility=View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                Log.d("TAG",t.localizedMessage)
            }
        })
    }

    private fun checkDevicePermission() {
        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == false) {
                        Toast.makeText(this@Home, "Allow Storage Permission!", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?,
                ) {
                    token?.continuePermissionRequest()
                }

            }).check()
    }

    @SuppressLint("RestrictedApi")
    private fun initializeAddObject() {
        mInterstitialAdLoadCallback =
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    InterstitialAd.load(this@Home,
                        MyDataClass.api_key,
                        adRequest,
                        this)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mInterstitialAd = interstitialAd
//                    if (mInterstitialAd != null) {
//                        mInterstitialAd!!.show(getActivity(this@Home))
//                    } else {
//                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
//                    }
                    mInterstitialAd!!.setFullScreenContentCallback(object :
                        FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            Log.d("TAG", "The ad was dismissed.")
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when fullscreen content failed to show.
                            Log.d("TAG", "The ad failed to show.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            mInterstitialAd = null
                            Log.d("TAG", "The ad was shown.")
                        }
                    })
                }
            }
        loadAd()
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
        api.getNodesResponse(MyDataClass.api_key).enqueue(object : Callback<Node> {
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
                            }
                        }
                        if (!arrayListForChildSubNode.isEmpty()) {
                            list1ForChildSubNode.put(indexForSubNode, arrayListForChildSubNode)
                        }
                    }
                    list1ForChildNode.put(index, arrayListForChildNode)
                    list1ForParentNode.put(index, arrayListForParentNode)

                }
                var parentNodeId = treeMap.get("0")
                for (i in 0..(parentNodeId?.size!! - 1)) {
                    if(i<2){
                        isClickedList.add(true)
                    }else{
                    isClickedList.add(false)
                    }
                }
              if(MyDataClass.isClickedValueForBtn==false){
                  MyDataClass.isClickedList=isClickedList
              }

                binding.homeRecyclerView.adapter =
                    treeMap.get("0")?.let {
                        ShowNodesAdapter(this@Home,
                            it,
                            list1ForChildNode,
                            list1ForParentNode, MyDataClass.isClickedList, list1ForChildSubNode)
                    }
                binding.homeRecyclerView.layoutManager = LinearLayoutManager(this@Home)

            }

            override fun onFailure(call: Call<Node>, t: Throwable) {
                Log.d("TAG", t.localizedMessage)
                Toast.makeText(this@Home, t.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initialize() {
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        var sharedPreferences = getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
//        val gson = Gson()
//        val json: String = sharedPreferences.getString("MyObject", "").toString()
//        val responseDataObject:ResponseDataClass = gson.fromJson(json, ResponseDataClass::class.java)
//        MyDataClass.responseDataClass=responseDataObject
//        MyDataClass.myUserId= responseDataObject?.user?.user_id!!
//        val adMobInitialzer = AdMobInitialzer(getContext(), root.findViewById(R.id.adView))
        MyDataClass.homeFragmentContainerView = binding.homeFragmentContainerViewForShowDetails
        MyDataClass.homeNestedScrollView = binding.homeScrollBar
        MyDataClass.getTransaction = ::getFragmentTransaction
        MyDataClass.onBack = ::onBackPressed
        MyDataClass.getTotalConversationsCount=::fetchTotalConversationsCounts
        MyDataClass.getTotalAlertsCount=::fetchTotalAlertsCounts
        MyDataClass.reactionDialog = ::getReactionsDialog
        if (MyDataClass.responseDataClass == null) {
            binding.homeScrollBar.visibility = View.VISIBLE
            binding.homeFragmentContainerViewForShowDetails.visibility = View.GONE
            var retrofit = RetrofitManager.getRetrofit1()
            var api = retrofit.create(HitApi::class.java)
            var sharedPreferences = getSharedPreferences("LoginUserDetails", MODE_PRIVATE)
            var userId = sharedPreferences.getInt("userId", 1)
            progressDialog.show()
            thread {
                api.getUsersProfileResponse(MyDataClass.api_key, userId)
                    .enqueue(object : Callback<ResponseDataClass> {
                        override fun onResponse(
                            call: Call<ResponseDataClass>,
                            response: Response<ResponseDataClass>,
                        ) {
                            if (response.isSuccessful) {

                                MyDataClass.responseDataClass = response.body()!!
                                progressDialog.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {

                        }
                    })
            }

        }

        var editor = sharedPreferences.edit()
        editor.putBoolean("valid", true)
        editor.commit()
        if (MyDataClass.responseDataClass?.user?.avatar_urls?.o == null) {
            binding.homeUserAccountProfileTv.visibility = View.VISIBLE
            binding.homeUserAccountProfile.visibility = View.GONE
            binding.homeUserAccountProfileTv.gravity = Gravity.CENTER
            binding.homeUserAccountProfileTv.setText(MyDataClass.responseDataClass?.user?.username?.get(
                0).toString())
        } else {
            Picasso.get().load(MyDataClass.responseDataClass?.user?.avatar_urls?.o)
                .placeholder(R.drawable.person)
                .into(binding.homeUserAccountProfile)
        }
        Picasso.get().load(MyDataClass.responseDataClass?.user?.avatar_urls?.o)
            .placeholder(R.drawable.ic_no_image).into(binding.homeUserAccountProfile)
        goUserNotification = findViewById(R.id.home_notification)
        goUserProfile = findViewById(R.id.home_userAccountProfile)
        drawerLayout = findViewById(R.id.home_drawer)
        navigationView = findViewById(R.id.home_navigation_view)
        toolbar = findViewById(R.id.home_toolbar)
        toggle = ActionBarDrawerToggle(this@Home,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun mobileAdInitialize() {
        MobileAds.initialize(this
        ) { }
    }


    @SuppressLint("RestrictedApi")
    private fun loadAd() {
        adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            mInterstitialAdLoadCallback)
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        Log.d("TAG", "count $count")
        if (drawerLayout.isOpen) {
            drawerLayout.closeDrawer(Gravity.LEFT)
        } else if (count == 0) {
            super.onBackPressed()
        } else if (MyDataClass.isEnteredInShowDetails) {
            Log.d("TAG", "isEnteredShowDetails")
            MyDataClass.isEnteredInShowDetails = false
            supportFragmentManager.popBackStack()
            startActivity(Intent(this, Home().javaClass))
            finish()
        } else if (MyDataClass.isGoConversation) {
            Log.d("TAG", "isGoconversation")
            MyDataClass.isGoConversation = false
            supportFragmentManager.popBackStack()
            startActivity(Intent(this, Home().javaClass))
            finish()
        } else if (MyDataClass.isGoNotification) {
            Log.d("TAG", "goNotification")
            MyDataClass.isGoNotification = false
            supportFragmentManager.popBackStack()
            startActivity(Intent(this, Home().javaClass))
            finish()
        } else if (MyDataClass.isGoProfile) {
            Log.d("TAG", "goProfile")
            MyDataClass.isGoProfile = false
            supportFragmentManager.popBackStack()
            startActivity(Intent(this, Home().javaClass))
            finish()
        } else if (MyDataClass.isPostThread) {
            Log.d("TAG", "isPostThread")
            MyDataClass.isPostThread = false
            supportFragmentManager.popBackStack()
            startActivity(Intent(this, Home().javaClass))
            finish()
        } else if (MyDataClass.isGoForLatestPosts) {
            Log.d("TAG", "isForLatestPosts")
            MyDataClass.isGoForLatestPosts = false
            supportFragmentManager.popBackStack()
            startActivity(Intent(this, Home().javaClass))
            finish()
        } else {
            Log.d("TAG", "poped from stack")
            supportFragmentManager.popBackStack()
        }
    }

    fun getFragmentTransaction(): FragmentTransaction {
        var fragmentManager: FragmentTransaction? = null
        if (!supportFragmentManager.isDestroyed) {
            fragmentManager = supportFragmentManager.beginTransaction()
            return fragmentManager
        } else {
            startActivity(Intent(this, Home()::class.java))
            finish()
        }
        return fragmentManager!!
    }


    fun getReactionsDialog(
        postId: Int,
        holder: Any,
        reactionScore: Int,
    ): DialogFragment {
        var reactionDialog =
            ReactionDialogClass(
                postId,
                holder,
                reactionScore)
        reactionDialog.show(supportFragmentManager, reactionDialog.javaClass.simpleName)
        return reactionDialog
    }

    override fun onReactionSelection(
        reactionType: Int,
        postId: Int,
        holder: Any,
        reactionScore: Int,
    ) {
        Log.d("TAG", reactionType.toString())
        MyDataClass.reactionType = reactionType
        when (reactionType) {
            1 -> {
                hitApiForReact(reactionType, postId, holder, reactionScore)
            }
            2 -> {
                hitApiForReact(reactionType, postId, holder, reactionScore)
            }
            3 -> {
                hitApiForReact(reactionType, postId, holder, reactionScore)
            }
            4 -> {
                hitApiForReact(reactionType, postId, holder, reactionScore)
            }
            5 -> {
                hitApiForReact(reactionType, postId, holder, reactionScore)
            }
            6 -> {
                hitApiForReact(reactionType, postId, holder, reactionScore)
            }
        }
    }

    private fun hitApiForReact(
        reactionType: Int,
        postId: Int,
        holder: Any,
        reactionScore: Int,
    ) {
        if (holder is UserProfileAdapter.UserProfileHolder) {

            var retrofit: Retrofit = RetrofitManager.getRetrofit1()
            var api: HitApi = retrofit.create(HitApi::class.java)
            api.getReaponseOfProfilePostsReact(MyDataClass.api_key,
                MyDataClass.myUserId,
                postId,
                reactionType).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>,
                ) {
                    if (response.body()?.get("action").toString().equals("insert")) {
                        changeLikeButton(reactionType, holder)
                        if (reactionScore > 0) {
                            if ((reactionScore - 1) != 0) {
                                holder.likeCounts.visibility = View.VISIBLE
                                holder.likeCounts.setText("You,${reactionScore} other")
                            } else {
                                holder.likeCounts.visibility = View.VISIBLE
                                holder.likeCounts.setText("You,${reactionScore} other")
                            }
                        } else {
                            holder.likeCounts.visibility = View.VISIBLE
                            holder.likeCounts.setText("You")
                        }
                    } else {
                        Log.d("TAG", response.body()?.get("action").toString())
                        holder.likeBtn.setTextColor(Color.parseColor("#FF000000"))
                        holder.likeBtn.setTypeface(null, Typeface.NORMAL)
                        holder.likeBtn.setText("Like")
                        holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,
                            0,
                            0,
                            0)
                        if (reactionScore > 0) {
                            if ((reactionScore - 1) != 0) {
                                holder.likeCounts.visibility = View.VISIBLE
                                holder.likeCounts.setText("${reactionScore}")
                            } else {
                                holder.likeCounts.visibility = View.VISIBLE
                                holder.likeCounts.setText("${
                                    reactionScore
                                }")
                            }
                        } else {
                            holder.likeCounts.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        } else if (holder is ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder) {
            var retrofit: Retrofit = RetrofitManager.getRetrofit1()
            var api: HitApi = retrofit.create(HitApi::class.java)
            api.getReaponseOfReact(MyDataClass.api_key, MyDataClass.myUserId, postId, reactionType)
                .enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(
                        call: Call<Map<String, Any>>,
                        response: Response<Map<String, Any>>,
                    ) {
                        if (response.body()?.get("action").toString().equals("insert")) {
                            changeLikeButton(reactionType, holder)
                            if (reactionScore > 0) {
                                if ((reactionScore - 1) != 0) {
                                    holder.likeCounts.visibility = View.VISIBLE
                                    holder.likeCounts.setText("You,${reactionScore} other")
                                } else {
                                    holder.likeCounts.visibility = View.VISIBLE
                                    holder.likeCounts.setText("You,${reactionScore} other")
                                }
                            } else {
                                holder.likeCounts.visibility = View.VISIBLE
                                holder.likeCounts.setText("You")
                            }
                        } else {
                            Log.d("TAG", response.body()?.get("action").toString())
                            holder.likeBtn.setTextColor(Color.parseColor("#FF000000"))
                            holder.likeBtn.setTypeface(null, Typeface.NORMAL)
                            holder.likeBtn.setText("Like")
                            holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,
                                0,
                                0,
                                0)
                            if (reactionScore > 0) {
                                if ((reactionScore - 1) != 0) {
                                    holder.likeCounts.visibility = View.VISIBLE
                                    holder.likeCounts.setText("${reactionScore}")
                                } else {
                                    holder.likeCounts.visibility = View.VISIBLE
                                    holder.likeCounts.setText("${
                                        reactionScore
                                    }")
                                }
                            } else {
                                holder.likeCounts.visibility = View.GONE
                            }
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    }
                })
        }


    }

    private fun changeLikeButton(
        reactionType: Int,
        holder: Any,
    ) {
        when (reactionType) {
            1 -> {
                if (holder is UserProfileAdapter.UserProfileHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#0B18CC"))
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setText("Like")
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_like,
                        0,
                        0,
                        0)
                } else if (holder is ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#0B18CC"))
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setText("Like")
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_like,
                        0,
                        0,
                        0)
                }
            }
            2 -> {
                if (holder is UserProfileAdapter.UserProfileHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#BF0404"))
                    holder.likeBtn.setText("Love")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_love_icon,
                        0,
                        0,
                        0)
                } else if (holder is ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#BF0404"))
                    holder.likeBtn.setText("Love")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_love_icon,
                        0,
                        0,
                        0)
                }
            }
            3 -> {
                if (holder is UserProfileAdapter.UserProfileHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("haha")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_haha_icon,
                        0,
                        0,
                        0)
                } else if (holder is ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("haha")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_haha_icon,
                        0,
                        0,
                        0)
                }
            }
            4 -> {
                if (holder is UserProfileAdapter.UserProfileHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("Wow")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wow_icon,
                        0,
                        0,
                        0)
                } else if (holder is ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("Wow")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wow_icon,
                        0,
                        0,
                        0)
                }
            }
            5 -> {
                if (holder is UserProfileAdapter.UserProfileHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("Sad")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sad_icon,
                        0,
                        0,
                        0)
                } else if (holder is ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("Sad")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sad_icon,
                        0,
                        0,
                        0)
                }
            }
            6 -> {
                if (holder is UserProfileAdapter.UserProfileHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#FB2707"))
                    holder.likeBtn.setText("Angery")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_angry_icon,
                        0,
                        0,
                        0)
                } else if (holder is ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder) {
                    holder.likeBtn.setTextColor(Color.parseColor("#FB2707"))
                    holder.likeBtn.setText("Angery")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_angry_icon,
                        0,
                        0,
                        0)
                }

            }
        }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.YEAR, p1)
        c.set(Calendar.MONTH, p2)
        c.set(Calendar.DATE, p3)
        var string = DateFormat.getDateInstance().format(c.time)
        Log.d("TAG", "date $p1 $p2 $p3")
        var list = LinkedList<Int>()
        list.add(p3)
        list.add(p2 + 1)
        list.add(p1)
        MyDataClass.datePick(list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002) {
            var uri1 = FileUtils.getPath(this, data?.data)
            MyDataClass.onReceiveData(uri1)
        }
    }

}
