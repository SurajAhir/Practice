package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.AdaptersClasses.ShowAttachmentFilesAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.ShowDetailsAdapter
import com.example.fatchcurrentlocation.DataClasses.*
import com.example.fatchcurrentlocation.databinding.FragmentShowDetailsBinding
import jp.wasabeef.richeditor.RichEditor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.util.*


class ShowDetails() : Fragment(), AdapterView.OnItemSelectedListener {
    var path: Int = 0
    var isChangedTextStyleBold: Boolean = false
    var isChangedTextStyleItalic: Boolean = false
    lateinit var progressBar: ProgressDialog
    final val PICKFILE_REQUEST_CODE = 1001
    var attachmentId: Int = 0
    var isAttachedFile: Boolean = false
    var gettedAttachmentKey: String = ""
    var attachmentFileString = ""
    var isGeneratedAttachmentKey = false
    lateinit var attachmentRequestBodyKey: RequestBody
    lateinit var btn_text: String
    var recommendedAndExactLinkedList: LinkedList<User> = LinkedList()
    var selectedUserLinkedList: LinkedList<User> = LinkedList()
    lateinit var description: String
    lateinit var title: String
    lateinit var typeData: TypeData
    var list1: LinkedList<Threads> = LinkedList()
    var lastUpdated: String = "Any time"
    var lastMessage: String = "Last message"
    var ascendingOrDesc: String = "Descending"
    var listForLastUpdate = arrayOf("Any time",
        "7 days",
        "14 days",
        "30 days",
        "2 months",
        "3 months",
        "6 months",
        "1 year")
    var listForLastMessage = arrayOf("Last message",
        "First message",
        "Title",
        "Replies",
        "Views",
        "First message reaction score")
    var listForAscOrDesc = arrayOf("Descending", "Ascending")
    var gettedTextFromEditor: String = ""
    var alertDialog: AlertDialog.Builder? = null
    var alertDialog1: AlertDialog? = null
    var centername = ""
    var rightname = ""
    var sizes = arrayOf("9", "10", "12", "15", "18", "22", "26")
    var isLinkOpened=false
    constructor(
        _path: Int,
        _btn_text: String,
        _description: String,
        _title: String,
        _typeData: TypeData,
    ) : this() {
        path = _path
        btn_text = _btn_text
        description = _description
        title = _title
        MyDataClass.path = _path
        typeData = _typeData
    }

    lateinit var binding: FragmentShowDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentShowDetailsBinding.inflate(layoutInflater, container, false)
        val adMobInitialzer = AdMobInitialzer(context, binding.adView)

        initializeData()
        fetchDataFromApi(path)
        binding.showPostPostThreadBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (typeData.allow_posting) {
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    var transaction = MyDataClass.getTransaction()
                    transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                        PostThread(path, title))
                    transaction.addToBackStack(null).commit()
                } else {
                    Toast.makeText(context, "You can't create a thread here", Toast.LENGTH_LONG)
                        .show()
                    return
                }
            }
        })
        binding.showDetailsTitleBtn.setOnClickListener { binding.showDetailsPostLinearLayout.visibility=View.VISIBLE
            binding.showDetailsTitleBtn.visibility=View.GONE
      }
        binding.showDetailsAttachFileBtn.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent = Intent.createChooser(intent, "Choose a file")
                startActivityForResult(intent, PICKFILE_REQUEST_CODE)
            }
        })
        binding.showNestedScrollView.setOnScrollChangeListener(object :
            NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int,
            ) {
                if (v != null) {
                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                        Log.d("TAG",
                            "${MyDataClass.paginationForShowDetails.last_page} and ${MyDataClass.page}")
                        if (MyDataClass.paginationForShowDetails.last_page == MyDataClass.page) {
                            binding.showProgressBar.visibility = View.GONE
                        } else {
                            MyDataClass.page++
                            Log.d("TAG", "true h")
                            binding.showProgressBar.visibility = View.VISIBLE
                            fetchDataFromApi(MyDataClass.path, MyDataClass.page)
                        }
                    }
                }
            }
        })

        binding.showCategory.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(context, Home().javaClass))
            }
        })
        binding.showDetailsFiltersBtn.setOnClickListener {
            binding.showDetailsLinearLayoutForDetails.visibility = View.GONE
            binding.showDetailsLinearLayoutForFileter.visibility = View.VISIBLE
        }
        binding.showDetailsFilterBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                api.getForumsResponseByFilter(
                    MyDataClass.api_key,
                    path,
                    ascendingOrDesc,
                    lastMessage,
                    selectedUserLinkedList.get(0).user_id, lastUpdated
                ).enqueue(object : Callback<ResponseThread> {
                    override fun onResponse(
                        call: Call<ResponseThread>,
                        response: Response<ResponseThread>,
                    ) {
                        Log.d("TAG",
                            "$path and $ascendingOrDesc and $lastMessage and ${
                                selectedUserLinkedList.get(0).user_id
                            } and $lastUpdated")
                        if (response.isSuccessful && response.body() != null) {
                            list1.clear()
                            binding.showDetailsLinearLayoutForDetails.visibility = View.VISIBLE
                            binding.showDetailsLinearLayoutForFileter.visibility = View.GONE
                            var list = response.body()?.threads?.toMutableList()
                            var sticky = response.body()?.sticky?.toMutableList()
                            if (!list?.isEmpty()!!) {
                                MyDataClass.paginationForShowDetails = response.body()!!.pagination
                                binding.showProgressBar.visibility = View.GONE
                                list?.let { list1.addAll(it) }
                                sticky?.let { list1.addAll(it) }
                                binding.showCategory.setText(btn_text)
                                binding.showDescription.setText(description)
                                binding.showTitle.setText(title)
                                var showDetailsAdapter =
                                    ShowDetailsAdapter(list1,
                                        context,
                                        response?.body()!!.pagination,
                                        title,
                                        1001)
                                binding.showRecyclerView.adapter = showDetailsAdapter
                                binding.showRecyclerView.layoutManager =
                                    LinearLayoutManager(context)
                            }
                        } else {
                            binding.showDetailsLinearLayoutForDetails.visibility = View.VISIBLE
                            binding.showDetailsLinearLayoutForFileter.visibility = View.GONE
                        }
                    }

                    override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                        binding.showDetailsLinearLayoutForDetails.visibility = View.VISIBLE
                        binding.showDetailsLinearLayoutForFileter.visibility = View.GONE
                    }
                })
            }
        })
        binding.startNewConversationFindRecipientEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                if (p0.toString().length > 2) {
                    api.findUserName(MyDataClass.api_key, MyDataClass.myUserId, p0.toString())
                        .enqueue(object : Callback<ResponseThread> {
                            override fun onResponse(
                                call: Call<ResponseThread>,
                                response: Response<ResponseThread>,
                            ) {
                                if (response.isSuccessful) {
                                    var recommendesList = response.body()?.recommendations
                                    var exactList = response.body()?.exact
                                    if (!recommendesList?.isEmpty()!!) {
                                        recommendedAndExactLinkedList.clear()
                                        recommendedAndExactLinkedList.addAll(recommendesList)
                                        binding.startNewConversationListView.visibility =
                                            View.VISIBLE
                                        binding.startNewConversationListView.adapter =
                                            context?.let {
                                                StartNewConversation.CustomList(it,
                                                    recommendedAndExactLinkedList)
                                            }

                                    } else if (exactList != null) {
                                        recommendedAndExactLinkedList.clear()
                                        recommendedAndExactLinkedList.add(exactList)
                                        binding.startNewConversationListView.visibility =
                                            View.VISIBLE
                                        binding.startNewConversationListView.adapter =
                                            context?.let {
                                                StartNewConversation.CustomList(it,
                                                    recommendedAndExactLinkedList)
                                            }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                                TODO("Not yet implemented")
                            }
                        })
                } else {
                    binding.startNewConversationListView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.startNewConversationListView.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            selectedUserLinkedList.clear()
            selectedUserLinkedList.add(recommendedAndExactLinkedList.get(position))
            binding.startNewConversationFindRecipientEt.setText(selectedUserLinkedList.get(position).username)
        })
//        binding.showDetailsIBtn.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(p0: View?) {
//                if (isChangedTextStyleItalic) {
//                    binding.showDetailsMessageEt.setTypeface(null, Typeface.NORMAL)
//                    isChangedTextStyleItalic = false
//                } else {
//                    isChangedTextStyleItalic = true
//                    binding.showDetailsMessageEt.setTypeface(null, Typeface.ITALIC)
//                }
//            }
//        })
//        binding.showDetailsBBtn.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(p0: View?) {
//                if (isChangedTextStyleBold) {
//                    binding.showDetailsMessageEt.setTypeface(null, Typeface.NORMAL)
//                    isChangedTextStyleBold = false
//                } else {
//                    isChangedTextStyleBold = true
//                    binding.showDetailsMessageEt.setTypeface(null, Typeface.BOLD)
//                }
//            }
//        })
        binding.showDetailsPostBtn.setOnClickListener(object :View.OnClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                var title = binding.showDetailsTitleEt.text.toString()
                var message = binding.showDetailsMessageEt.text.toString()
                if (title.isEmpty()) {
                    binding.showDetailsTitleEt.setError("Please enter a valid title")
                    binding.showDetailsTitleEt.focusable=View.FOCUSABLE
                } else if (message.isEmpty()) {
                    Toast.makeText(context,"Please enter a valid message",Toast.LENGTH_LONG).show()
                } else {
                    progressBar.show()
                    if (isAttachedFile) {
                        isAttachedFile = false
                        var retrofit = RetrofitManager.getRetrofit1()
                        var api = retrofit.create(HitApi::class.java)
                        if (MyDataClass.attachmentFileListAttachmentId.size > 0) {
                            for (i in MyDataClass.attachmentFileListAttachmentId) {
                                attachmentFileString = attachmentFileString +
                                        """[ATTACH type="full"]${i}[/ATTACH] """
                            }
                        }
                        hitApi(api, message, title, gettedAttachmentKey)
                    } else {
                        var retrofit = RetrofitManager.getRetrofit1()
                        var api = retrofit.create(HitApi::class.java)
                        hitApi(api, message, title, "")
                    }

                }
            }
        })
        binding.showDetailsMoreOptionsBtn.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                if (typeData.allow_posting) {
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    var transaction = MyDataClass.getTransaction()
                    transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                        PostThread(path, title))
                    transaction.addToBackStack(null).commit()
                } else {
                    Toast.makeText(context, "You can't create a thread here", Toast.LENGTH_LONG)
                        .show()
                    return
                }
            }
        })
        binding.showDetailsBold.setOnClickListener { binding.showDetailsRichEditor.setBold() }
        binding.showDetailsItalic.setOnClickListener { binding.showDetailsRichEditor.setItalic() }
        binding.showDetailsUndo.setOnClickListener { binding.showDetailsRichEditor.undo() }
        binding.showDetailsRedo.setOnClickListener { binding.showDetailsRichEditor.redo() }
        binding.showDetailsUnderline.setOnClickListener { binding.showDetailsRichEditor.setUnderline() }
//        binding.showDetailsIndent.setOnClickListener { binding.showDetailsRichEditor.setIndent() }
//        binding.showDetailsOutdent.setOnClickListener { binding.showDetailsRichEditor.setOutdent() }
        binding.showDetailsAlignLeft.setOnClickListener { binding.showDetailsRichEditor.setAlignLeft() }
        binding.showDetailsAlignRight.setOnClickListener { binding.showDetailsRichEditor.setAlignRight() }
        binding.showDetailsAlignCenter.setOnClickListener { binding.showDetailsRichEditor.setAlignCenter() }
        binding.showDetailsInsertBullets.setOnClickListener { binding.showDetailsRichEditor.setBullets() }
        binding.showDetailsInsertNumbers.setOnClickListener { binding.showDetailsRichEditor.setNumbers() }
//        binding.showDetailsIndent.setOnClickListener { binding.showDetailsRichEditor.setIndent() }
//        binding.showDetailsOutdent.setOnClickListener { binding.showDetailsRichEditor.setOutdent() }
        binding.showDetailsRichEditor.setOnTextChangeListener(RichEditor.OnTextChangeListener { text -> //                Log.d("TAG", text);
            val center = ""
            var right = ""
            gettedTextFromEditor = text.replace("<", "[")
            gettedTextFromEditor = gettedTextFromEditor.replace(">", "]")
            gettedTextFromEditor =
                gettedTextFromEditor.replace("&nbsp;", "").replace("[/li]", "")
                    .replace("[ol", "[List=1")
                    .replace("[li", "[*")
                    .replace("/ol", "/List").replace(" style=\"\"", "").replace(" style=\"\"", "")
                    .replace("[br]", "").replace("[i style=\"font-weight: bold;\"]", "[i]")
                    .replace("[ul", "[List").replace("[/ul", "[/List")
                    .replace("[span style=\"font-size: 22px;\"]", "[SIZE=6]")
                    .replace("[/span]", "[/SIZE]").replace("[font size=\"7\"]", "[SIZE=26]")
                    .replace("[font size=\"6\"]", "[SIZE=22]")
                    .replace("[font size=\"5\"]", "[SIZE=18]")
                    .replace("[font size=\"4\"]", "[SIZE=15]")
                    .replace("[font size=\"3\"]", "[SIZE=12]")
                    .replace("[font size=\"2\"]", "[SIZE=10]")
                    .replace("[font size=\"1\"]", "[SIZE=9]").replace("[/font]", "[/SIZE]")
                    .replace("[a href=","[URL=").replace("[/a]","[/URL]")
                    .replace("[blockquote style=\"margin: 0 0 0 40px; border: none; padding: 0px;\"]", "[INDENT=2]")
                    .replace("[/blockquote]", "[/INDENT]").replace("[span style=\"font-size: 15px;\"]","[SIZE=12]")
            if (gettedTextFromEditor.contains("[div style=\"text-align: center;\"]")) {
                while (gettedTextFromEditor.contains("[div style=\"text-align: center;\"]")) {
                    centername = gettedTextFromEditor
                    centername = centername.substring(centername.indexOf("center;\"]") + 9)
                    if (centername.contains("[/div]")) {
                        centername = centername.substring(0, centername.indexOf("[/div]"))
                        gettedTextFromEditor =
                            gettedTextFromEditor.replace("[div style=\"text-align: center;\"]$centername",
                                "[CENTER]$centername")
                                .replace(centername + "[/div]", centername + "[/CENTER]")
                                .replace("[CENTER][/CENTER]", "")
                    } else {
                        Log.d("TAG", "center $centername")
                        if (gettedTextFromEditor.contains("[div style=\"text-align: center;\"]")) {
                            centername = centername.substring(0, centername.indexOf("[/RIGHT]"))
                            gettedTextFromEditor =
                                gettedTextFromEditor.replace("[div style=\"text-align: center;\"]$centername",
                                    "[CENTER]$centername")
                                    .replace(centername + "[/RIGHT]", centername + "[/CENTER]")
                                    .replace("[RIGHT][/RIGHT]", "")
                        } else {
                            break
                        }
                        break
                    }
                }
            }
            if (gettedTextFromEditor.contains("[div style=\"text-align: right;\"]")) {
                while (gettedTextFromEditor.contains("[div style=\"text-align: right;\"]")) {
                    rightname = gettedTextFromEditor
                    rightname = rightname.substring(rightname.indexOf("right;\"]") + 8)
                    if (rightname.contains("[/div]")) {
                        rightname = rightname.substring(0, rightname.indexOf("[/div]"))
                        right = "[div style=\"text-align: right;\"]$rightname[/div]"
                        gettedTextFromEditor =
                            gettedTextFromEditor.replace("[div style=\"text-align: right;\"]$rightname",
                                "[RIGHT]$rightname")
                                .replace(rightname + "[/div]", rightname + "[/RIGHT]")
                                .replace("[RIGHT][/RIGHT]", "")
                    } else {
                        Log.d("TAG", "right $rightname")
                        if (gettedTextFromEditor.contains("[div style=\"text-align: right;\"]")) {
                            rightname = rightname.substring(0, rightname.indexOf("[/CENTER]"))
                            gettedTextFromEditor =
                                gettedTextFromEditor.replace("[div style=\"text-align: right;\"]$rightname",
                                    "[RIGHT]$rightname")
                                    .replace(rightname + "[/CENTER]", rightname + "[/RIGHT]")
                                    .replace("[RIGHT][/RIGHT]", "")
                        } else {
                            break
                        }
                    }
                    //                        gettedText = gettedText + "[RIGHT]" + rightname + "[/RIGHT]";
                }
            }
            if (gettedTextFromEditor.contains("[div style=\"text-align: left;\"]")) {
                gettedTextFromEditor =
                    gettedTextFromEditor.replace("[div style=\"text-align: left;\"]", "")
                        .replace("[/div]", "")
            }
            Log.d("TAG", gettedTextFromEditor)
        })
        binding.showDetailsFontSizeBtn.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                alertDialog1 = alertDialog!!.create()
                alertDialog1!!.show()
                alertDialog1!!.window!!.setLayout(300, 600)
                alertDialog1!!.show()
            }
        })
        alertDialog!!.setItems(sizes
        ) { dialogInterface, i ->
            when (i) {
                0 -> {
                    binding.showDetailsRichEditor.setFontSize(1)
                    Log.d("TAG", "clicked$i")
                }
                1 -> {
                    binding.showDetailsRichEditor.setFontSize(2)
                    Log.d("TAG", "clicked$i")
                }
                2 -> {
                    binding.showDetailsRichEditor.setFontSize(3)
                    Log.d("TAG", "clicked$i")
                }
                3 -> {
                    binding.showDetailsRichEditor.setFontSize(4)
                    Log.d("TAG", "clicked$i")
                }
                4 -> {
                    binding.showDetailsRichEditor.setFontSize(5)
                    Log.d("TAG", "clicked$i")
                }
                5 -> {
                    binding.showDetailsRichEditor.setFontSize(6)
                    Log.d("TAG", "clicked$i")
                }
                6 -> {
                    binding.showDetailsRichEditor.setFontSize(7)
                    Log.d("TAG", "clicked$i")
                }
            }
        }
        binding.showDetailsInsertLink.setOnClickListener {
            if (isLinkOpened) {
                isLinkOpened = false
                binding.showDetailsInsertLinkLayout.visibility = View.GONE
            } else {
                isLinkOpened = true
                binding.showDetailsInsertLinkLayout.visibility = View.VISIBLE
            }
        }
        binding.showDetailsInsertLinkBtn.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                if (binding.showDetailsUrlEt.text.isEmpty()) {
                    binding.showDetailsUrlEt.setError("Please enter a valid URL")
                    binding.showDetailsUrlEt.focusable = View.FOCUSABLE
                } else if (binding.showDetailsTextEt.text.toString().isEmpty()) {
                    binding.showDetailsTextEt.setError("Please enter a valid text")
                    binding.showDetailsTextEt.focusable = View.FOCUSABLE
                } else {
                    binding.showDetailsRichEditor.insertLink(binding.showDetailsUrlEt.text.toString(),
                        binding.showDetailsTextEt.text.toString())
                    binding.showDetailsUrlEt.setText("")
                    binding.showDetailsTextEt.setText("")
                    binding.showDetailsInsertLinkLayout.visibility = View.GONE
                }
            }
        })
        return binding.root
    }

    private fun initializeData() {
        MyDataClass.isJumpedToImage=false
        MyDataClass.JumpToImagePosition=0
        MyDataClass.JumpedToImageList.clear()
        MyDataClass.JumpedToImagePageNum=0
        MyDataClass.page = 1
        list1.clear()
        MyDataClass.isEnteredInShowDetails=true
        alertDialog = context?.let { AlertDialog.Builder(it) }
        binding.showDetailsRichEditor.setEditorHeight(100)
        binding.showDetailsRichEditor.setEditorFontSize(15)
        binding.showDetailsRichEditor.setEditorFontColor(Color.BLACK)
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        binding.showDetailsRichEditor.setPadding(4,4,4,4)
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        binding.showDetailsRichEditor.setPlaceholder("Write reply...")
        progressBar= ProgressDialog(context)
        MyDataClass.attachmentFileListItem.clear()
        MyDataClass.attachmentFileListAttachmentId.clear()
        binding.showDetailsLastUpdatedSpinnerView.adapter = context?.let {
            ArrayAdapter(it,
                android.R.layout.simple_spinner_dropdown_item,
                listForLastUpdate)
        }
        binding.showDetailsLastMessageSpinnerView.adapter = context?.let {
            ArrayAdapter(it,
                android.R.layout.simple_spinner_dropdown_item,
                listForLastMessage)
        }
        binding.showDetailsAscendingSpinnerView.adapter = context?.let {
            ArrayAdapter(it,
                android.R.layout.simple_spinner_dropdown_item,
                listForAscOrDesc)
        }
    }

    fun fetchDataFromApi(path: Int, page: Int = 1) {
        var retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getForumsResponse("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
            path,
            page,
            "desc",
            "last_post_date")
            .enqueue(object : retrofit2.Callback<ResponseThread> {
                override fun onResponse(

                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        var list = response.body()?.threads?.toMutableList()
                        var sticky = response.body()?.sticky?.toMutableList()
                        MyDataClass.paginationForShowDetails = response.body()!!.pagination
//                        binding.showProgressBar.visibility = View.GONE
                        list?.let { list1.addAll(it) }
                        sticky?.let { list1.addAll(it) }
                        binding.showCategory.setText(btn_text)
                        binding.showDescription.setText(description)
                        binding.showTitle.setText(title)
                        var showDetailsAdapter =
                            ShowDetailsAdapter(list1, context, response?.body()!!.pagination, title,1001)
                        binding.showRecyclerView.adapter = showDetailsAdapter
                        binding.showRecyclerView.layoutManager = LinearLayoutManager(context)
                    }else{
                        binding.showProgressBar.visibility=View.GONE
                    }
                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                    binding.showProgressBar.visibility = View.GONE
                }
            })
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0?.id) {
            R.id.show_details_LastUpdatedSpinnerView -> {
                lastUpdated = listForLastUpdate.get(p2)
            }
            R.id.show_details_LastMessageSpinnerView -> {
                lastMessage = listForLastMessage.get(p2)
            }
            R.id.show_details_AscendingSpinnerView -> {
                ascendingOrDesc = listForAscOrDesc.get(p2)
            }

        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressBar.show()
        if (requestCode == PICKFILE_REQUEST_CODE) {
            var uri1 = FileUtils.getPath(context, data?.data)
            Log.d("TAG", uri1)
            var file: File = File(uri1)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val fileToUpload =
                MultipartBody.Part.createFormData("attachment", file.getName(), requestBody)
            var retrofit: Retrofit = RetrofitManager.getRetrofit1()
            var api: HitApi = retrofit.create(HitApi::class.java)
            if (isGeneratedAttachmentKey) {
                postAttachmentFile(fileToUpload, attachmentRequestBodyKey, api)
            } else {
                generateAttachmentKey(api, fileToUpload)
            }

        }

    }

    private fun funAdapter() {
        if (MyDataClass.attachmentFileListItem.size > 0) {
            binding.showDetailsRecyclerView.visibility = View.VISIBLE
            binding.showDetailsRecyclerView.adapter =
                context?.let {
                    ShowAttachmentFilesAdapter(it,
                        MyDataClass.attachmentFileListItem,
                        MyDataClass.attachmentFileListAttachmentId)
                }
            binding.showDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.showDetailsRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            binding.showDetailsRecyclerView.visibility = View.GONE
        }
    }

    private fun postAttachmentFile(
        fileToUpload: MultipartBody.Part,
        attachmentKey: RequestBody,
        api: HitApi,
    ) {
        api.postAttachmentFile("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
            MyDataClass.myUserId, fileToUpload, attachmentKey
        ).enqueue(object : Callback<ResponseThread> {
            override fun onResponse(
                call: Call<ResponseThread>,
                response: Response<ResponseThread>,
            ) {
                if (response.isSuccessful) {
                    progressBar.dismiss()
                    attachmentId = response.body()?.attachment?.attachment_id!!
                    MyDataClass.attachmentFileListItem.add(response.body()!!.attachment.filename)
                    MyDataClass.attachmentFileListAttachmentId.add(attachmentId)
                    MyDataClass.funAdapter = ::funAdapter
                    funAdapter()
                    isAttachedFile = true
                } else {
                    progressBar.dismiss()
                }

            }

            override fun onFailure(
                call: Call<ResponseThread>,
                t: Throwable,
            ) {
                progressBar.dismiss()
                Log.d("TAG", t.localizedMessage)
            }
        })
    }

    private fun generateAttachmentKey(api: HitApi, fileToUpload: MultipartBody.Part) {
        isGeneratedAttachmentKey = true
        api.generateAttachmentKeyForPostThread("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
            MyDataClass.myUserId,
            path,
            "post")
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>,
                ) {
                    if (response.isSuccessful) {
                        gettedAttachmentKey = response.body()?.get("key").toString()
                        attachmentRequestBodyKey =
                            RequestBody.create(MediaType.parse("multipart/form-data"),
                                gettedAttachmentKey)
                        postAttachmentFile(fileToUpload, attachmentRequestBodyKey, api)
                    } else {
                        progressBar.dismiss()
                    }

                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Log.d("TAG", "error ${t.localizedMessage}")
                    progressBar.dismiss()
                }
            })
    }

    private fun hitApi(api: HitApi, message: String, title: String, gettedAttachmentKey: String) {
        api.postThread(
            MyDataClass.api_key,
            MyDataClass.myUserId,
            path,
            title,
            (message + attachmentFileString),
            gettedAttachmentKey
        ).enqueue(object : Callback<Map<String, Boolean>> {
            override fun onResponse(
                call: Call<Map<String, Boolean>>,
                response: Response<Map<String, Boolean>>,
            ) {
                Log.d("TAG", "code${response.code()}")
                if (response.isSuccessful && response.body()
                        ?.get("success") == true
                ) {
                    progressBar.dismiss()
                    binding.showDetailsTitleBtn.setText("")
                    binding.showDetailsPostLinearLayout.visibility=View.GONE
                    fetchDataFromApi(path)
                } else {
                    progressBar.dismiss()
                }
            }

            override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                progressBar.dismiss()
            }
        })
    }
}