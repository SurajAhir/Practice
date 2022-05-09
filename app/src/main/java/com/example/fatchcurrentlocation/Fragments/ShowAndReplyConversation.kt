package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fatchcurrentlocation.AdaptersClasses.ShowAndReplyConversationAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.ShowAttachmentFilesAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.ShowParticipantsAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.ShowSelectedUsersNameAdapter
import com.example.fatchcurrentlocation.DataClasses.*
import com.example.fatchcurrentlocation.FileUtils
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.RetrofitManager
import com.example.fatchcurrentlocation.databinding.FragmentShowAndReplyConversationBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.util.*

class ShowAndReplyConversation(val conversation: Conversations) : Fragment() {
    lateinit var binding: FragmentShowAndReplyConversationBinding
    var isChangedTextStyleBold: Boolean = false
    var isChangedTextStyleItalic: Boolean = false
    lateinit var progressBar: ProgressDialog
    val PICKFILE_REQUEST_CODE: Int = 1001
    var attachmentId: Int = 0
    var isAttachedFile: Boolean = false
    var gettedAttachmentKey: String = ""
    var isGeneratedAttachmentKey = false
    lateinit var attachmentRequestBodyKey: RequestBody
    var attachmentFileString: String = ""
    var recommendedAndExactLinkedList: LinkedList<User> = LinkedList()
    var selectedUserLinkedList: LinkedList<User> = LinkedList()
    var listOfUserId: LinkedList<Int> = LinkedList()
    var listOfUsers: LinkedList<User> = LinkedList()
    var listOfUsersName: LinkedList<String> = LinkedList()
    var listOfPosts: LinkedList<Posts> = LinkedList()
    var gettedTextFromEditor: String = ""
    var alertDialog: AlertDialog.Builder? = null
    var alertDialog1: AlertDialog? = null
    var alertDialogForList: AlertDialog.Builder? = null
    var alertDialogForList1: AlertDialog? = null
    var centername = ""
    var rightname = ""
    var sizes = arrayOf("9", "10", "12", "15", "18", "22", "26")
    var items: Array<Item> = arrayOf(Item("Ordered list", R.drawable.numbers),
        Item("Unordered list", R.drawable.bullets),
        Item("Align left", R.drawable.justify_left),
        Item("Align center", R.drawable.justify_center),
        Item("Align right", R.drawable.justify_right))
    var isLinkOpened = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentShowAndReplyConversationBinding.inflate(layoutInflater, container, false)
        initializeData()
        putDataOnFields(conversation)
        if(MyDataClass.isJumpedToImage){
            Log.d("TAGA","position ${MyDataClass.JumpToImagePosition}")
            binding.showAndReplyConversationRecyclerViewForThreads.adapter =
                ShowAndReplyConversationAdapter(MyDataClass.JumpedToImageList,
                    context)
            binding.showAndReplyConversationRecyclerViewForThreads.layoutManager =
                LinearLayoutManager(context)
            binding.showAndReplyConversationRecyclerViewForThreads.scrollToPosition(MyDataClass.JumpToImagePosition)
        }else{
        fetchDataFromApi()}
        binding.showAndReplyConversationCloseInviteDialogBtn.setOnClickListener {
            binding.showAndReplyConversationInviteLayout.visibility = View.GONE
            binding.showAndReplyConversationInviteMoreBtn.visibility = View.VISIBLE
        }
        binding.showAndReplyConversationRecyclerViewForThreads.setOnScrollChangeListener(object :
            View.OnScrollChangeListener {
            override fun onScrollChange(
                v: View?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int,
            ) {
                if (v != null) {
                    if (scrollY == v.height - v.measuredHeight) {
                        if (MyDataClass.paginationForShowDetails.last_page == MyDataClass.page) {
                            binding.showAndReplyConversationProgressBar.visibility = View.GONE
                        } else {
                            MyDataClass.page++
                            binding.showAndReplyConversationProgressBar.visibility = View.VISIBLE
                            fetchDataFromApi(MyDataClass.page)
                        }
                    }
                }
            }
        })
        binding.showAndReplyConversationGoBackBtn.setOnClickListener { MyDataClass.onBack() }
        binding.showAndReplyConversationAttachFileBtn.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent = Intent.createChooser(intent, "Choose a file")
            startActivityForResult(intent, PICKFILE_REQUEST_CODE)
        }
        binding.showAndReplyConversationPostReplyBtn.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                var message=binding.showAndReplyConversationMessageEt.text.toString()
                if (message.isEmpty()) {
                    return
                } else {
                    progressBar.show()
//                    Log.d("TAG", "thread $threadId")
                    if (isAttachedFile) {
                        isAttachedFile = false
                        if (MyDataClass.attachmentFileListAttachmentId.size > 0) {
                            for (i in MyDataClass.attachmentFileListAttachmentId) {
                                attachmentFileString = attachmentFileString +
                                        """[ATTACH type="full"]${i}[/ATTACH] """
                            }
                            hitApi(gettedAttachmentKey, attachmentFileString)
                        }
                    } else {
                        hitApi("", "")
                    }


                }
            }

        })
        binding.showAndReplyConversationFindRecipientEt.addTextChangedListener(object :
            TextWatcher {
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
                                        binding.showAndReplyConversationListView.visibility =
                                            View.VISIBLE
                                        binding.showAndReplyConversationListView.adapter =
                                            context?.let {
                                                StartNewConversation.CustomList(it,
                                                    recommendedAndExactLinkedList)
                                            }

                                    } else if (exactList != null) {
                                        recommendedAndExactLinkedList.clear()
                                        recommendedAndExactLinkedList.add(exactList)
                                        binding.showAndReplyConversationListView.visibility =
                                            View.VISIBLE
                                        binding.showAndReplyConversationListView.adapter =
                                            context?.let {
                                                StartNewConversation.CustomList(it,
                                                    recommendedAndExactLinkedList)
                                            }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ResponseThread>, t: Throwable) {

                            }
                        })
                } else {
                    binding.showAndReplyConversationListView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.showAndReplyConversationInviteMoreBtn.setOnClickListener {
            binding.showAndReplyConversationInviteMoreBtn.visibility = View.GONE
            binding.showAndReplyConversationInviteLayout.visibility = View.VISIBLE
        }

        binding.showAndReplyConversationListView.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            selectedUserLinkedList.add(recommendedAndExactLinkedList.get(position))
            showUsersOnAdapter()
        })

        binding.showAndReplyConversationInviteBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (listOfUserId.isEmpty()) {
                    Toast.makeText(context, "Please select atleast one member", Toast.LENGTH_LONG)
                        .show()
                } else {
                    var list: LinkedList<Int> = LinkedList<Int>()
                    for (i in selectedUserLinkedList) {
                        list.add(i.user_id)
                    }
                    progressBar.show()
                    var retrofit = RetrofitManager.getRetrofit1()
                    var api = retrofit.create(HitApi::class.java)
                    api.inviteMembers(MyDataClass.api_key,
                        MyDataClass.myUserId,
                        conversation.conversation_id,
                        list).enqueue(object : Callback<Map<String, Boolean>> {
                        override fun onResponse(
                            call: Call<Map<String, Boolean>>,
                            response: Response<Map<String, Boolean>>,
                        ) {
                            if (response.isSuccessful) {
                                api.getConversationsByConversId(MyDataClass.api_key,
                                    MyDataClass.myUserId,
                                    conversation.conversation_id)
                                    .enqueue(object : Callback<ResponseThread> {
                                        override fun onResponse(
                                            call: Call<ResponseThread>,
                                            response: Response<ResponseThread>,
                                        ) {
                                            if (response.isSuccessful) {
                                                progressBar.dismiss()
                                                binding.showAndReplyConversationInviteMoreBtn.visibility =
                                                    View.VISIBLE
                                                binding.showAndReplyConversationInviteLayout.visibility =
                                                    View.GONE
                                                binding.showAndReplyConversationRecipientNameTv.setText(
                                                    "")
                                                initializeData()
                                                response.body()?.conversation?.let {
                                                    putDataOnFields(it)
                                                }
                                                fetchDataFromApi()
//                                                var transaction = MyDataClass.getTransaction()
//                                                if (transaction != null) {
//                                                    MyDataClass.homeFragmentContainerView.visibility =
//                                                        View.VISIBLE
//                                                    MyDataClass.homeNestedScrollView.visibility =
//                                                        View.GONE
//                                                    response.body()?.conversation?.let {
//                                                        ShowAndReplyConversation(it)
//                                                    }?.let {
//                                                        transaction.replace(R.id.home_fragment_containerViewForShowDetails,
//                                                            it)
//                                                    }
//                                                    transaction.addToBackStack(null).commit()
//                                                }
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<ResponseThread>,
                                            t: Throwable,
                                        ) {
                                            progressBar.dismiss()
                                        }
                                    })

                            } else {
                                progressBar.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                            Log.d("TAG", t.localizedMessage)
                            progressBar.dismiss()
                        }
                    })
                }
            }

        })
//        binding.showAndReplyConversationBold.setOnClickListener { binding.showAndReplyConversationRichEditor.setBold() }
//        binding.showAndReplyConversationItalic.setOnClickListener { binding.showAndReplyConversationRichEditor.setItalic() }
//        binding.showAndReplyConversationUndo.setOnClickListener { binding.showAndReplyConversationRichEditor.undo() }
//        binding.showAndReplyConversationRedo.setOnClickListener { binding.showAndReplyConversationRichEditor.redo() }
//        binding.showAndReplyConversationUnderline.setOnClickListener { binding.showAndReplyConversationRichEditor.setUnderline() }
//        binding.showAndReplyConversationIndent.setOnClickListener { binding.showAndReplyConversationRichEditor.setIndent() }
//        binding.showAndReplyConversationOutdent.setOnClickListener { binding.showAndReplyConversationRichEditor.setOutdent() }
//        binding.showAndReplyConversationAlignLeft.setOnClickListener { binding.showAndReplyConversationRichEditor.setAlignLeft() }
//        binding.showAndReplyConversationAlignRight.setOnClickListener { binding.showAndReplyConversationRichEditor.setAlignRight() }
//        binding.showAndReplyConversationAlignCenter.setOnClickListener { binding.showAndReplyConversationRichEditor.setAlignCenter() }
//        binding.showAndReplyConversationInsertBullets.setOnClickListener { binding.showAndReplyConversationRichEditor.setBullets() }
//        binding.showAndReplyConversationInsertNumbers.setOnClickListener {
//            alertDialogForList1 = alertDialogForList!!.create()
//            alertDialogForList1!!.show()
//            alertDialogForList1!!.window!!.setLayout(350, 600)
//            alertDialogForList1!!.window!!.setGravity(Gravity.CENTER_HORIZONTAL)
//            alertDialogForList1!!.show() }
////        binding.showAndReplyConversationIndent.setOnClickListener { binding.showAndReplyConversationRichEditor.setIndent() }
////        binding.showAndReplyConversationOutdent.setOnClickListener { binding.showAndReplyConversationRichEditor.setOutdent() }
//        binding.showAndReplyConversationRichEditor.setOnTextChangeListener(OnTextChangeListener { text -> //                Log.d("TAG", text);
//            val center = ""
//            var right = ""
//            gettedTextFromEditor = text.replace("<", "[")
//            gettedTextFromEditor = gettedTextFromEditor.replace(">", "]")
//            gettedTextFromEditor =
//                gettedTextFromEditor.replace("&nbsp;", "").replace("[/li]", "")
//                    .replace("[ol", "[List=1")
//                    .replace("[li", "[*")
//                    .replace("/ol", "/List").replace(" style=\"\"", "").replace(" style=\"\"", "")
//                    .replace("[br]", "").replace("[i style=\"font-weight: bold;\"]", "[i]")
//                    .replace("[ul", "[List").replace("[/ul", "[/List")
//                    .replace("[span style=\"font-size: 22px;\"]", "[SIZE=6]")
//                    .replace("[/span]", "[/SIZE]").replace("[font size=\"7\"]", "[SIZE=26]")
//                    .replace("[font size=\"6\"]", "[SIZE=22]")
//                    .replace("[font size=\"5\"]", "[SIZE=18]")
//                    .replace("[font size=\"4\"]", "[SIZE=15]")
//                    .replace("[font size=\"3\"]", "[SIZE=12]")
//                    .replace("[font size=\"2\"]", "[SIZE=10]")
//                    .replace("[font size=\"1\"]", "[SIZE=9]").replace("[/font]", "[/SIZE]")
//                    .replace("[a href=", "[URL=").replace("[/a]", "[/URL]")
//                    .replace("[blockquote style=\"margin: 0 0 0 40px; border: none; padding: 0px;\"]",
//                        "[INDENT=2]")
//                    .replace("[/blockquote]", "[/INDENT]")
//                    .replace("[span style=\"font-size: 15px;\"]", "[SIZE=12]")
//            if (gettedTextFromEditor.contains("[div style=\"text-align: center;\"]")) {
//                while (gettedTextFromEditor.contains("[div style=\"text-align: center;\"]")) {
//                    centername = gettedTextFromEditor
//                    centername = centername.substring(centername.indexOf("center;\"]") + 9)
//                    if (centername.contains("[/div]")) {
//                        centername = centername.substring(0, centername.indexOf("[/div]"))
//                        gettedTextFromEditor =
//                            gettedTextFromEditor.replace("[div style=\"text-align: center;\"]$centername",
//                                "[CENTER]$centername")
//                                .replace(centername + "[/div]", centername + "[/CENTER]")
//                                .replace("[CENTER][/CENTER]", "")
//                    } else {
//                        Log.d("TAG", "center $centername")
//                        if (gettedTextFromEditor.contains("[div style=\"text-align: center;\"]")) {
//                            centername = centername.substring(0, centername.indexOf("[/RIGHT]"))
//                            gettedTextFromEditor =
//                                gettedTextFromEditor.replace("[div style=\"text-align: center;\"]$centername",
//                                    "[CENTER]$centername")
//                                    .replace(centername + "[/RIGHT]", centername + "[/CENTER]")
//                                    .replace("[RIGHT][/RIGHT]", "")
//                        } else {
//                            break
//                        }
//                        break
//                    }
//                }
//            }
//            if (gettedTextFromEditor.contains("[div style=\"text-align: right;\"]")) {
//                while (gettedTextFromEditor.contains("[div style=\"text-align: right;\"]")) {
//                    rightname = gettedTextFromEditor
//                    rightname = rightname.substring(rightname.indexOf("right;\"]") + 8)
//                    if (rightname.contains("[/div]")) {
//                        rightname = rightname.substring(0, rightname.indexOf("[/div]"))
//                        right = "[div style=\"text-align: right;\"]$rightname[/div]"
//                        gettedTextFromEditor =
//                            gettedTextFromEditor.replace("[div style=\"text-align: right;\"]$rightname",
//                                "[RIGHT]$rightname")
//                                .replace(rightname + "[/div]", rightname + "[/RIGHT]")
//                                .replace("[RIGHT][/RIGHT]", "")
//                    } else {
//                        Log.d("TAG", "right $rightname")
//                        if (gettedTextFromEditor.contains("[div style=\"text-align: right;\"]")) {
//                            rightname = rightname.substring(0, rightname.indexOf("[/CENTER]"))
//                            gettedTextFromEditor =
//                                gettedTextFromEditor.replace("[div style=\"text-align: right;\"]$rightname",
//                                    "[RIGHT]$rightname")
//                                    .replace(rightname + "[/CENTER]", rightname + "[/RIGHT]")
//                                    .replace("[RIGHT][/RIGHT]", "")
//                        } else {
//                            break
//                        }
//                    }
//                    //                        gettedText = gettedText + "[RIGHT]" + rightname + "[/RIGHT]";
//                }
//            }
//            if (gettedTextFromEditor.contains("[div style=\"text-align: left;\"]")) {
//                gettedTextFromEditor =
//                    gettedTextFromEditor.replace("[div style=\"text-align: left;\"]", "")
//                        .replace("[/div]", "")
//            }
//            Log.d("TAG", gettedTextFromEditor)
//        })
//        binding.showAndReplyConversationInsertLink.setOnClickListener {
//            if (isLinkOpened) {
//                isLinkOpened = false
//                binding.showAndReplyConversationInsertLinkLayout.visibility = View.GONE
//            } else {
//                isLinkOpened = true
//                binding.showAndReplyConversationInsertLinkLayout.visibility = View.VISIBLE
//            }
//        }
//        binding.showAndReplyConversationInsertLinkBtn.setOnClickListener(object :
//            View.OnClickListener {
//            override fun onClick(p0: View?) {
//                if (binding.showAndReplyConversationUrlEt.text.isEmpty()) {
//                    binding.showAndReplyConversationUrlEt.setError("Please enter a valid URL")
//                    binding.showAndReplyConversationUrlEt.focusable = View.FOCUSABLE
//                } else if (binding.showAndReplyConversationTextEt.text.toString().isEmpty()) {
//                    binding.showAndReplyConversationTextEt.setError("Please enter a valid text")
//                    binding.showAndReplyConversationTextEt.focusable = View.FOCUSABLE
//                } else {
//                    binding.showAndReplyConversationRichEditor.insertLink(binding.showAndReplyConversationUrlEt.text.toString(),
//                        binding.showAndReplyConversationTextEt.text.toString())
//                    binding.showAndReplyConversationUrlEt.setText("")
//                    binding.showAndReplyConversationTextEt.setText("")
//                    binding.showAndReplyConversationInsertLinkLayout.visibility = View.GONE
//                }
//            }
//        })
//        binding.showAndReplyConversationFontSizeBtn.setOnClickListener(object :
//            View.OnClickListener {
//            override fun onClick(p0: View?) {
//                alertDialog1 = alertDialog!!.create()
//                alertDialog1!!.show()
//                alertDialog1!!.window!!.setLayout(300, 600)
//                alertDialog1!!.show()
//            }
//        })
//        alertDialog!!.setItems(sizes
//        ) { dialogInterface, i ->
//            when (i) {
//                0 -> {
//                    binding.showAndReplyConversationRichEditor.setFontSize(1)
//                    Log.d("TAG", "clicked$i")
//                }
//                1 -> {
//                    binding.showAndReplyConversationRichEditor.setFontSize(2)
//                    Log.d("TAG", "clicked$i")
//                }
//                2 -> {
//                    binding.showAndReplyConversationRichEditor.setFontSize(3)
//                    Log.d("TAG", "clicked$i")
//                }
//                3 -> {
//                    binding.showAndReplyConversationRichEditor.setFontSize(4)
//                    Log.d("TAG", "clicked$i")
//                }
//                4 -> {
//                    binding.showAndReplyConversationRichEditor.setFontSize(5)
//                    Log.d("TAG", "clicked$i")
//                }
//                5 -> {
//                    binding.showAndReplyConversationRichEditor.setFontSize(6)
//                    Log.d("TAG", "clicked$i")
//                }
//                6 -> {
//                    binding.showAndReplyConversationRichEditor.setFontSize(7)
//                    Log.d("TAG", "clicked$i")
//                }
//            }
//        }
//        val adapter: ArrayAdapter<*> = object : ArrayAdapter<Item?>(
//            requireContext(),
//            android.R.layout.select_dialog_item,
//            android.R.id.text1,
//            items) {
//            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//                //Use super class to create the View
//                val v = super.getView(position, convertView, parent)
//                val tv = v.findViewById<View>(android.R.id.text1) as TextView
//                tv.textSize = 12f
//                //                //Put the image on the TextView
//                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0)
//
//                //Add margin between image and text (support various screen densities)
//                val dp5 = (3 * resources.displayMetrics.density + 0.5f).toInt()
//                tv.compoundDrawablePadding = dp5
//                return v
//            }
//        }
//        alertDialogForList?.setAdapter(adapter,object :DialogInterface.OnClickListener{
//            override fun onClick(p0: DialogInterface?, i: Int) {
//                when (i) {
//                    0 -> {
//                        binding.showAndReplyConversationRichEditor.setNumbers()
//                        Log.d("TAG", "clicked$i")
//                    }
//                    1 -> {
//                        binding.showAndReplyConversationRichEditor.setBullets()
//                        Log.d("TAG", "clicked$i")
//                    }
//                    2 -> {
//                        binding.showAndReplyConversationRichEditor.setAlignLeft()
//                        Log.d("TAG", "clicked$i")
//                    }
//                    3 -> {
//                        binding.showAndReplyConversationRichEditor.setAlignCenter()
//                        Log.d("TAG", "clicked$i")
//                    }
//                    4 -> {
//                        binding.showAndReplyConversationRichEditor.setAlignRight()
//                        Log.d("TAG", "clicked$i")
//                    }
//
//                }
//            }
//        })
        return binding.root
    }

    private fun showUsersOnAdapter() {
        binding.showAndReplyConversationRecyclerViewForShowSelectUsersName.adapter =
            context?.let {
                ShowSelectedUsersNameAdapter(it,
                    selectedUserLinkedList,
                    ::showUsersOnAdapter)
            }
        binding.showAndReplyConversationRecyclerViewForShowSelectUsersName.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchDataFromApi(page: Int = 1) {
        var retrofit = RetrofitManager.getRetrofit1()
        var api = retrofit.create(HitApi::class.java)
        api.getConversationMessages(MyDataClass.api_key,
            MyDataClass.myUserId,
            conversation.conversation_id, page).enqueue(object : Callback<ResponseDataClass> {
            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>,
            ) {
                if (response.isSuccessful) {
                    MyDataClass.paginationForShowDetails = response.body()?.pagination!!
                    listOfPosts.addAll(response.body()!!.messages)
                    MyDataClass.JumpedToImageList.addAll(listOfPosts)
                    binding.showAndReplyConversationRecyclerViewForThreads.adapter =
                        ShowAndReplyConversationAdapter(listOfPosts,
                            context)
//                        response.body()?.listofPo?.let {
//                            response.body()?.pagination?.let { it1 ->
//                                ShowAndReplyConversationAdapter(it, context,
//                                    it1)
                    binding.showAndReplyConversationRecyclerViewForThreads.layoutManager =
                        LinearLayoutManager(context)
                } else {
                    binding.showAndReplyConversationProgressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                binding.showAndReplyConversationProgressBar.visibility = View.GONE
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun putDataOnFields(conversation: Conversations) {

        binding.showAndReplyConversationTitleTv.setText(conversation.title)
        listOfUsersName.add(conversation.username)
        var jsonObject = conversation.recipients
        Log.d("TAG", "aya $jsonObject")
        var _set = jsonObject.keySet()
        var _iterator = _set.iterator()
        while (_iterator.hasNext()) {
            var key = _iterator.next()
            var value = jsonObject.get(key).asString
            listOfUserId.add(key.toInt())
            listOfUsersName.add(value)
        }
        listOfUsers.add(conversation.Starter)
        for (name in listOfUsersName) {
            binding.showAndReplyConversationRecipientNameTv.append("${name},")
        }
        var lastActivityDate = Date((conversation.start_date as Long) * 1000)
        var currentTime = Date(System.currentTimeMillis())
        if (currentTime.year == lastActivityDate.year) {
            if (lastActivityDate.month == currentTime.month) {
                if (lastActivityDate.date == currentTime.date) {
                    if (lastActivityDate.hours == currentTime.hours) {
                        if (currentTime.minutes - lastActivityDate.minutes < 2) {
                            binding.showAndReplyConversationLastTimeTv.setText("a moment ago")
                        } else {
                            binding.showAndReplyConversationLastTimeTv.setText("${currentTime.minutes - lastActivityDate.minutes} minuts ago")
                        }
                    } else if (lastActivityDate.hours < currentTime.hours) {
                        var timeInMinuts =
                            (currentTime.hours - lastActivityDate.hours) * 60 + currentTime.minutes
                        if (timeInMinuts - lastActivityDate.minutes < 60) {
                            binding.showAndReplyConversationLastTimeTv.setText("${
                                Math.abs(timeInMinuts - lastActivityDate.minutes)
                            } minuts ago")
                        } else {
                            binding.showAndReplyConversationLastTimeTv.setText("${currentTime.hours - lastActivityDate.hours} hour ago")
                        }
                    }
                } else {
                    if (currentTime.date - lastActivityDate.date < 2) {
                        binding.showAndReplyConversationLastTimeTv.setText("${currentTime.date - lastActivityDate.date} day ago")
                    } else {
                        binding.showAndReplyConversationLastTimeTv.setText("${currentTime.date - lastActivityDate.date} days ago")
                    }
                }
            } else {
                if (currentTime.month - lastActivityDate.month < 2) {
                    binding.showAndReplyConversationLastTimeTv.setText("${currentTime.month - lastActivityDate.month} month ago")
                } else {
                    binding.showAndReplyConversationLastTimeTv.setText("${currentTime.month - lastActivityDate.month} months ago")
                }
            }
        } else {
            if (currentTime.year - lastActivityDate.year < 2) {
                binding.showAndReplyConversationLastTimeTv.setText("${currentTime.year - lastActivityDate.year} year ago")
            } else {
                binding.showAndReplyConversationLastTimeTv.setText("${currentTime.year - lastActivityDate.year} years ago")
            }
        }
        for (i in listOfUserId) {
            callMeApi(i)
        }
    }

    private fun callMeApi(key: Int) {
        var retrofit = RetrofitManager.getRetrofit1()
        var api = retrofit.create(HitApi::class.java)
        Log.d("TAG", "size ${listOfUserId.size}")
        api.getUsersProfileResponse(MyDataClass.api_key, key)
            .enqueue(object : Callback<ResponseDataClass> {
                override fun onResponse(
                    call: Call<ResponseDataClass>,
                    response: Response<ResponseDataClass>,
                ) {
                    Log.d("TAG", "code ${response.code()}")
                    if (response.isSuccessful) {
                        var responseData = response.body()?.me
                        if (responseData != null) {
                            listOfUsers.add(responseData)
                            binding.showAndReplyConversationRecyclerViewForParticipants.adapter =
                                context?.let { ShowParticipantsAdapter(it, listOfUsers) }
                            binding.showAndReplyConversationRecyclerViewForParticipants.layoutManager =
                                LinearLayoutManager(context)
                        }

                    }
                }

                override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                    Log.d("TAG", "errror ${t.localizedMessage}")
                }
            })

    }

    private fun hitApi(gettedAttachmentKey: String, attachmentFileString: String) {
        Log.d("TAG", "string ${attachmentFileString}")
        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.postConversationReply("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
            MyDataClass.myUserId,
            conversation.conversation_id,
            binding.showAndReplyConversationMessageEt.text.toString() + attachmentFileString,
            gettedAttachmentKey)
            .enqueue(object : Callback<ResponseThread> {
                override fun onResponse(
                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    Log.d("TAG", "CODE ${response.code()}")
                    if (response.isSuccessful) {
                        isAttachedFile = false
                        MyDataClass.attachmentFileListAttachmentId.clear()
                        MyDataClass.attachmentFileListItem.clear()
                        listOfPosts.clear()
                        progressBar.dismiss()
                        MyDataClass.page = 1
                        binding.showAndReplyConversationMessageEt.setText("")
                        funAdapter()
                        fetchDataFromApi()
                    } else {
                        progressBar.dismiss()
                    }
                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                    progressBar.dismiss()
                }
            })
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
            binding.showAndReplyConversationRecyclerViewForAttachments.visibility = View.VISIBLE
            binding.showAndReplyConversationRecyclerViewForAttachments.adapter =
                context?.let {
                    ShowAttachmentFilesAdapter(it,
                        MyDataClass.attachmentFileListItem,
                        MyDataClass.attachmentFileListAttachmentId)
                }
            binding.showAndReplyConversationRecyclerViewForAttachments.layoutManager =
                LinearLayoutManager(context)
            binding.showAndReplyConversationRecyclerViewForAttachments.adapter?.notifyDataSetChanged()
        } else {
            binding.showAndReplyConversationRecyclerViewForAttachments.visibility = View.GONE
        }
    }

    private fun initializeData() {
        listOfUsers.clear()
        listOfUserId.clear()
        listOfUsersName.clear()
        listOfPosts.clear()
        MyDataClass.isGoConversation = false
        alertDialog = context?.let { AlertDialog.Builder(it) }
        alertDialogForList= context?.let { AlertDialog.Builder(it) }
//        binding.showAndReplyConversationRichEditor.setEditorHeight(100)
//        binding.showAndReplyConversationRichEditor.setEditorFontSize(15)
//        binding.showAndReplyConversationRichEditor.setEditorFontColor(Color.BLACK)
//        //mEditor.setEditorBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundResource(R.drawable.bg);
//        //mEditor.setEditorBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundResource(R.drawable.bg);
//        binding.showAndReplyConversationRichEditor.setPadding(4, 4, 4, 4)
//        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
//        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
//        binding.showAndReplyConversationRichEditor.setPlaceholder("Write reply...")
        MyDataClass.attachmentFileListItem.clear()
        MyDataClass.attachmentFileListAttachmentId.clear()
        progressBar = ProgressDialog(context)
        progressBar.setTitle("Wait...")
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
                Log.d("TAG", "code ${response.code()}")
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
        api.generateAttachmentKeyForPostReplyForConversation(MyDataClass.api_key,
            MyDataClass.myUserId,
            conversation.conversation_id,
            "conversation_message")
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>,
                ) {
                    Log.d("TAG",
                        "CODE ${response.code()} and messageid ${conversation.conversation_id}")
                    if (response.isSuccessful) {
                        gettedAttachmentKey = response.body()?.get("key").toString()
                        Log.d("TAG", "key $gettedAttachmentKey")
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

}