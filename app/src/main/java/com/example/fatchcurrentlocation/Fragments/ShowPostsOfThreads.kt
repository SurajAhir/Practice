package com.example.fatchcurrentlocation.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.AdaptersClasses.ShowAttachmentFilesAdapter
import com.example.fatchcurrentlocation.AdaptersClasses.ShowPostsOfThreadsAdapter
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Pagination
import com.example.fatchcurrentlocation.DataClasses.Posts
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.databinding.FragmentShowPostsOfThreadsBinding
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.services.RetrofitManager
import kotlinx.coroutines.*
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.util.*


class ShowPostsOfThreads(
    val lastPostUsername: String,
    val category: String,
    val title1: String,
    val threadId: Int,
    val lastPostDate: String,
    val position: Int,
    val requestCode: Int,
) : Fragment() {
    lateinit var binding: FragmentShowPostsOfThreadsBinding
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
    var list1: LinkedList<Posts> = LinkedList()
    var gettedTextFromEditor: String = ""
    var alertDialog: AlertDialog.Builder? = null
    var alertDialog1: AlertDialog? = null
    var centername = ""
    var rightname = ""
    var sizes = arrayOf("9", "10", "12", "15", "18", "22", "26")
    var isLinkOpened = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentShowPostsOfThreadsBinding.inflate(layoutInflater, container, false)
        val adMobInitialzer = AdMobInitialzer(context, binding.adView1)
        Log.d("TAGA","name $threadId")
        initializeData()
        if(MyDataClass.isJumpedToImage){
            MyDataClass.page=MyDataClass.JumpedToImagePageNum
            checkPageNumber(MyDataClass.JumpedToImagePageNum)
        }else if (requestCode == 1001) {
            if (position < 20) {
                fetchDataFromApi(threadId,1)
            } else {
                if (position % 20 == 0) {
                    var page = position / 20
                    fetchDataFromApi(threadId,page)
                } else {
                    var page = position / 20
                   fetchDataFromApi(threadId,page+1)

                }
            }
        } else if (requestCode == 1002) {
            fetchDataFromApi(threadId)
        }
        binding.showPostsOfThreadsAttachFileBtn.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent = Intent.createChooser(intent, "Choose a file")
            startActivityForResult(intent, PICKFILE_REQUEST_CODE)
        }
        binding.showPostsOfThreadsPostReplyBtn.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.showPostsOfThreadsMessageEt.text.toString().isEmpty()) {
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
        binding.showPostsOfThreadsBottemPrevBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.page--
                var page = MyDataClass.page
                if (page > 0) {
                    checkPageNumber(page)
                }

            }
        })
        binding.showPostsOfThreadsPrevBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.page--
                var page = MyDataClass.page
                if (page > 0) {
                    checkPageNumber(page)
                }
            }
        })
        binding.showPostPostThreadBottemNextBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.page++
                var page = MyDataClass.page
                checkPageNumber(page)

            }
        })
        binding.showPostPostThreadNextBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                MyDataClass.page++
                var page = MyDataClass.page
                checkPageNumber(page)

            }
        })
        binding.showPostPostThreadShowNumberBtn1.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadShowNumberBtn1.text.toString().toInt()
                MyDataClass.page = 1
//                fetchDataFromApi(threadId, num)
                checkPageNumber(num)
            }
        })
        binding.showPostPostThreadShowNumberBtn2.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadShowNumberBtn2.text.toString().toInt()
                MyDataClass.page = 2
                checkPageNumber(num)
            }
        })
        binding.showPostPostThreadBottemShowNumberBtn1.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadBottemShowNumberBtn1.text.toString().toInt()
                MyDataClass.page = 1
                checkPageNumber(num)
            }
        })
        binding.showPostPostThreadBottemShowNumberBtn2.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadBottemShowNumberBtn2.text.toString().toInt()
                MyDataClass.page = 2
                checkPageNumber(num)
            }
        })

        binding.showPostPostThreadBottemShowNumberBtn3.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadBottemShowNumberBtn3.text.toString().toInt()
                MyDataClass.page = 3
                checkPageNumber(num)
            }
        })
        binding.showPostPostThreadShowNumberBtn3.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadShowNumberBtn3.text.toString().toInt()
                MyDataClass.page = 3
                checkPageNumber(num)
            }
        })


        binding.showPostPostThreadBottemShowNumberBtn4.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadBottemShowNumberBtn4.text.toString().toInt()
                MyDataClass.page = num
//                fetchDataFromApi(threadId, num)
                checkPageNumber(num)
            }
        })
        binding.showPostPostThreadShowNumberBtn4.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadShowNumberBtn4.text.toString().toInt()
                MyDataClass.page = num
//                fetchDataFromApi(threadId, num)
                checkPageNumber(num)
            }
        })

        binding.showPostPostThreadBottemShowCurrentNumber.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadBottemShowCurrentNumber.text.toString().toInt()
                MyDataClass.page = num
                checkPageNumber(num)
            }
        })
        binding.showPostPostThreadShowCurrentNumber.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(p0: View?) {
                var num = binding.showPostPostThreadShowCurrentNumber.text.toString().toInt()
                MyDataClass.page = num
                checkPageNumber(num)
            }
        })

//        binding.showPostsOfThreadsNestedScrollView.setOnScrollChangeListener(object :View.OnScrollChangeListener{
//            override fun onScrollChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int) {
//
//            }
//        })
//        binding.showPostsOfThreadsRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                currentItems=manager.childCount
//                totalItesm=manager.itemCount
//                scrollOutItems=manager.findFirstVisibleItemPosition()
//                Log.d("TAGA","$currentItems $totalItesm $scrollOutItems")
//                if(isScrolled){
//                    if(currentItems+scrollOutItems==totalItesm){
//                        Log.d("TAG","snSrollde1")
//                        isScrolled=false
//                        if (MyDataClass.paginationForPostsOfThreads.last_page == MyDataClass.page) {
//                            binding.showPostsOfThreadsProgressBar.visibility = View.GONE
//                        } else {
//                            MyDataClass.page++
//                            Log.d("TAG","snSrollde2")
//                            binding.showPostsOfThreadsProgressBar.visibility = View.VISIBLE
//                            fetchDataFromApi(MyDataClass.threadId, MyDataClass.page)
//                        }
//                    }
//                }
//            }
//        })
//        binding.showPostsOfThreadsRecyclerView.setOnScrollChangeListener(@RequiresApi(Build.VERSION_CODES.M)
//        object :View.OnScrollChangeListener{
//            override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
//                if (v != null) {
//                    if (scrollY == v.height - v.measuredHeight) {
//                        Log.d("TAG",
//                            "${MyDataClass.paginationForPostsOfThreads.last_page} and ${MyDataClass.pageForPosts}")
//                        if (MyDataClass.paginationForPostsOfThreads.last_page == MyDataClass.page) {
//                            binding.showPostsOfThreadsProgressBar.visibility = View.GONE
//                        } else {
//                            MyDataClass.page++
//                            binding.showPostsOfThreadsProgressBar.visibility = View.VISIBLE
//                            fetchDataFromApi(MyDataClass.threadId, MyDataClass.page)
//                        }
//                    }
//            }
//            }
//        })

//        binding.showPostsOfThreadsNestedScrollView.setOnScrollChangeListener(object :
//           NestedScrollView.OnScrollChangeListener {
//            override fun onScrollChange(
//                v: NestedScrollView?,
//                scrollX: Int,
//                scrollY: Int,
//                oldScrollX: Int,
//                oldScrollY: Int,
//            ) {
//                if (v != null) {
//                    Log.d("TAG","${v.measuredHeight} AND ${v.height}")
//                    if (scrollY == v.getChildAt(0).measuredHeight-v.measuredHeight) {
//                        if (MyDataClass.paginationForPostsOfThreads.last_page == MyDataClass.page) {
//                            binding.showPostsOfThreadsProgressBar.visibility = View.GONE
//                        } else {
//                            MyDataClass.page++
//                            binding.showPostsOfThreadsProgressBar.visibility = View.VISIBLE
//                            fetchDataFromApi(MyDataClass.threadId, MyDataClass.page)
//                        }
//                    }
//                }
//            }
//        })
        binding.showPostsOfThreadsCategory.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(context, Home().javaClass))
            }
        })
//        binding.showPostsOfThreadsBold.setOnClickListener { binding.showPostsOfThreadsRichEditor.setBold() }
//        binding.showPostsOfThreadsItalic.setOnClickListener { binding.showPostsOfThreadsRichEditor.setItalic() }
//        binding.showPostsOfThreadsUndo.setOnClickListener { binding.showPostsOfThreadsRichEditor.undo() }
//        binding.showPostsOfThreadsRedo.setOnClickListener { binding.showPostsOfThreadsRichEditor.redo() }
//        binding.showPostsOfThreadsUnderline.setOnClickListener { binding.showPostsOfThreadsRichEditor.setUnderline() }
////        binding.showPostsOfThreadsIndent.setOnClickListener { binding.showPostsOfThreadsRichEditor.setIndent() }
////        binding.showPostsOfThreadsOutdent.setOnClickListener { binding.showPostsOfThreadsRichEditor.setOutdent() }
//        binding.showPostsOfThreadsAlignLeft.setOnClickListener { binding.showPostsOfThreadsRichEditor.setAlignLeft() }
//        binding.showPostsOfThreadsAlignRight.setOnClickListener { binding.showPostsOfThreadsRichEditor.setAlignRight() }
//        binding.showPostsOfThreadsAlignCenter.setOnClickListener { binding.showPostsOfThreadsRichEditor.setAlignCenter() }
//        binding.showPostsOfThreadsInsertBullets.setOnClickListener { binding.showPostsOfThreadsRichEditor.setBullets() }
//        binding.showPostsOfThreadsInsertNumbers.setOnClickListener { binding.showPostsOfThreadsRichEditor.setNumbers() }
////        binding.showPostsOfThreadsIndent.setOnClickListener { binding.showPostsOfThreadsRichEditor.setIndent() }
////        binding.showPostsOfThreadsOutdent.setOnClickListener { binding.showPostsOfThreadsRichEditor.setOutdent() }
//        binding.showPostsOfThreadsRichEditor.setOnTextChangeListener(RichEditor.OnTextChangeListener { text -> //                Log.d("TAG", text);
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
//        binding.showPostsOfThreadsFontSizeBtn.setOnClickListener(object :
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
//                    binding.showPostsOfThreadsRichEditor.setFontSize(1)
//                    Log.d("TAG", "clicked$i")
//                }
//                1 -> {
//                    binding.showPostsOfThreadsRichEditor.setFontSize(2)
//                    Log.d("TAG", "clicked$i")
//                }
//                2 -> {
//                    binding.showPostsOfThreadsRichEditor.setFontSize(3)
//                    Log.d("TAG", "clicked$i")
//                }
//                3 -> {
//                    binding.showPostsOfThreadsRichEditor.setFontSize(4)
//                    Log.d("TAG", "clicked$i")
//                }
//                4 -> {
//                    binding.showPostsOfThreadsRichEditor.setFontSize(5)
//                    Log.d("TAG", "clicked$i")
//                }
//                5 -> {
//                    binding.showPostsOfThreadsRichEditor.setFontSize(6)
//                    Log.d("TAG", "clicked$i")
//                }
//                6 -> {
//                    binding.showPostsOfThreadsRichEditor.setFontSize(7)
//                    Log.d("TAG", "clicked$i")
//                }
//            }
//        }
//        binding.showPostsOfThreadsInsertLink.setOnClickListener {
//            if (isLinkOpened) {
//                isLinkOpened = false
//                binding.showPostsOfThreadsInsertLinkLayout.visibility = View.GONE
//            } else {
//                isLinkOpened = true
//                binding.showPostsOfThreadsInsertLinkLayout.visibility = View.VISIBLE
//            }
//        }
//        binding.showPostsOfThreadsInsertLinkBtn.setOnClickListener(object : View.OnClickListener {
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun onClick(p0: View?) {
//                if (binding.showPostsOfThreadsUrlEt.text.isEmpty()) {
//                    binding.showPostsOfThreadsUrlEt.setError("Please enter a valid URL")
//                    binding.showPostsOfThreadsUrlEt.focusable = View.FOCUSABLE
//                } else if (binding.showPostsOfThreadsTextEt.text.toString().isEmpty()) {
//                    binding.showPostsOfThreadsTextEt.setError("Please enter a valid text")
//                    binding.showPostsOfThreadsTextEt.focusable = View.FOCUSABLE
//                } else {
//                    binding.showPostsOfThreadsRichEditor.insertLink(binding.showPostsOfThreadsUrlEt.text.toString(),
//                        binding.showPostsOfThreadsTextEt.text.toString())
//                    binding.showPostsOfThreadsUrlEt.setText("")
//                    binding.showPostsOfThreadsTextEt.setText("")
//                    binding.showPostsOfThreadsInsertLinkLayout.visibility = View.GONE
//                }
//            }
//        })
        return binding.root
    }

    private fun checkPageNumber(page: Int) {
        if (page <= MyDataClass.paginationForPostsOfThreads.last_page) {
            if (page == 1) {
                fetchDataFromApi(MyDataClass.threadId, page)
                binding.showPostPostThreadShowCurrentNumber.visibility = View.GONE
                binding.showPostPostThreadBottemShowCurrentNumber.visibility =
                    View.GONE
                binding.showPostsOfThreadsBottemPrevBtn.visibility = View.GONE
                binding.showPostsOfThreadsPrevBtn.visibility = View.GONE
                binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadShowNumberBtn3.visibility = View.GONE
                binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.GONE
                binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.BLACK)
                binding.showPostPostThreadShowNumberBtn1.visibility = View.VISIBLE
                binding.showPostPostThreadBottemShowNumberBtn1.visibility = View.VISIBLE
                if (page != MyDataClass.paginationForPostsOfThreads.last_page) {
                    binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.BLACK)

                    binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                }
                if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
                    binding.showPostPostThreadNextBtn.visibility = View.GONE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                }
            } else if (page == 2) {
                fetchDataFromApi(MyDataClass.threadId, page)
                binding.showPostPostThreadShowCurrentNumber.visibility = View.GONE
                binding.showPostPostThreadBottemShowCurrentNumber.visibility =
                    View.GONE
                binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.BLACK)
                if (page != MyDataClass.paginationForPostsOfThreads.last_page) {
                    binding.showPostPostThreadShowNumberBtn2.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.BLACK)

                    binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                } else {
                    binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)

                    binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.BLACK)
                }
                if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
                    binding.showPostPostThreadNextBtn.visibility = View.GONE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                }
            } else if (page == 3) {
                fetchDataFromApi(MyDataClass.threadId, page)
                binding.showPostPostThreadShowCurrentNumber.visibility = View.GONE
                binding.showPostPostThreadBottemShowCurrentNumber.visibility =
                    View.GONE
                binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn2.visibility = View.VISIBLE
                binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.VISIBLE
                if (page != MyDataClass.paginationForPostsOfThreads.last_page) {
                    binding.showPostPostThreadShowNumberBtn3.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.BLACK)

                    binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                } else {
                    binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.WHITE)

                    binding.showPostPostThreadShowNumberBtn3.visibility = View.GONE
                    binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.GONE
                    binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.BLACK)
                }

                if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
                    binding.showPostPostThreadNextBtn.visibility = View.GONE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                }
            } else if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
                fetchDataFromApi(MyDataClass.threadId, page)
                binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                binding.showPostPostThreadNextBtn.visibility = View.GONE
                binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                binding.showPostPostThreadShowNumberBtn3.visibility = View.GONE
                binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.GONE
                binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.BLACK)
                binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.WHITE)
                binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.WHITE)
                binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.BLACK)
                binding.showPostPostThreadShowCurrentNumber.visibility = View.GONE
                binding.showPostPostThreadBottemShowCurrentNumber.visibility = View.GONE
                binding.showPostPostThreadBottemShowCurrentNumber.setTextColor(Color.BLACK)
                binding.showPostPostThreadShowCurrentNumber.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowCurrentNumber.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowCurrentNumber.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadBottemShowDotsLayout.visibility = View.VISIBLE
                binding.showPostPostThreadShowDotsLayout.visibility = View.VISIBLE
            } else if (page == 4) {
                fetchDataFromApi(MyDataClass.threadId, page)
                binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadShowNumberBtn2.visibility = View.VISIBLE
                binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.VISIBLE
                binding.showPostPostThreadShowNumberBtn3.visibility = View.VISIBLE
                binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.VISIBLE
                binding.showPostPostThreadShowDotsLayout.visibility = View.VISIBLE
                binding.showPostPostThreadBottemShowDotsLayout.visibility = View.VISIBLE
                binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowCurrentNumber.visibility = View.VISIBLE
                binding.showPostPostThreadBottemShowCurrentNumber.visibility = View.VISIBLE
                binding.showPostPostThreadBottemShowCurrentNumber.setTextColor(Color.WHITE)
                binding.showPostPostThreadShowCurrentNumber.setTextColor(Color.WHITE)
                binding.showPostPostThreadBottemShowCurrentNumber.setBackgroundColor(Color.BLACK)
                binding.showPostPostThreadShowCurrentNumber.setBackgroundColor(Color.BLACK)
                binding.showPostPostThreadShowCurrentNumber.setText(page.toString())
                binding.showPostPostThreadBottemShowCurrentNumber.setText(page.toString())
                if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
                    binding.showPostPostThreadNextBtn.visibility = View.GONE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                }
            } else {
                fetchDataFromApi(MyDataClass.threadId, page)
                binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                binding.showPostPostThreadShowNumberBtn3.visibility = View.GONE
                binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.GONE
                binding.showPostPostThreadShowCurrentNumber.visibility = View.VISIBLE
                binding.showPostPostThreadBottemShowCurrentNumber.visibility = View.VISIBLE
                binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.BLACK)
                binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.WHITE)
                binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.WHITE)
                binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.BLACK)
                binding.showPostPostThreadBottemShowCurrentNumber.setText(page.toString())
                binding.showPostPostThreadShowCurrentNumber.setText(page.toString())
                binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                binding.showPostPostThreadShowCurrentNumber.setBackgroundColor(Color.BLACK)
                binding.showPostPostThreadShowCurrentNumber.setTextColor(Color.WHITE)
                binding.showPostPostThreadBottemShowCurrentNumber.setTextColor(Color.WHITE)
                binding.showPostPostThreadBottemShowCurrentNumber.setBackgroundColor(Color.BLACK)
                binding.showPostPostThreadBottemShowDotsLayout.visibility = View.VISIBLE
                binding.showPostPostThreadShowDotsLayout.visibility = View.VISIBLE
            }
        }
    }


        private fun checkPageNumberForAlerts(page: Int) {
            if (page <= MyDataClass.paginationForPostsOfThreads.last_page) {
                if (page == 1) {
//                    fetchDataFromApi(MyDataClass.threadId, page)
                    binding.showPostPostThreadShowCurrentNumber.visibility = View.GONE
                    binding.showPostPostThreadBottemShowCurrentNumber.visibility =
                        View.GONE
                    binding.showPostsOfThreadsBottemPrevBtn.visibility = View.GONE
                    binding.showPostsOfThreadsPrevBtn.visibility = View.GONE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn3.visibility = View.GONE
                    binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.GONE
                    binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowNumberBtn1.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowNumberBtn1.visibility = View.VISIBLE
                    if (page != MyDataClass.paginationForPostsOfThreads.last_page) {
                        binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.BLACK)
                        binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.BLACK)

                        binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                        binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                        binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                        binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    }
                    if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
                        binding.showPostPostThreadNextBtn.visibility = View.GONE
                        binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                    }
                } else if (page == 2) {
//                    fetchDataFromApi(MyDataClass.threadId, page)
                    binding.showPostPostThreadShowCurrentNumber.visibility = View.GONE
                    binding.showPostPostThreadBottemShowCurrentNumber.visibility =
                        View.GONE
                    binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                    binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.BLACK)
                    if (page != MyDataClass.paginationForPostsOfThreads.last_page) {
                        binding.showPostPostThreadShowNumberBtn2.visibility = View.VISIBLE
                        binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.VISIBLE
                        binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.BLACK)
                        binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.BLACK)

                        binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                        binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                        binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                        binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    } else {
                        binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                        binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                        binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.BLACK)
                        binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                        binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)

                        binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.BLACK)
                        binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.BLACK)
                    }
                    if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
                        binding.showPostPostThreadNextBtn.visibility = View.GONE
                        binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                    }
                } else if (page == 3) {
//                    fetchDataFromApi(MyDataClass.threadId, page)
                    binding.showPostPostThreadShowCurrentNumber.visibility = View.GONE
                    binding.showPostPostThreadBottemShowCurrentNumber.visibility =
                        View.GONE
                    binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                    binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn2.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.VISIBLE
                    if (page != MyDataClass.paginationForPostsOfThreads.last_page) {
                        binding.showPostPostThreadShowNumberBtn3.visibility = View.VISIBLE
                        binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.VISIBLE
                        binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.BLACK)
                        binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.BLACK)

                        binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                        binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                        binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                        binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    } else {
                        binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                        binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                        binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                        binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.WHITE)

                        binding.showPostPostThreadShowNumberBtn3.visibility = View.GONE
                        binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.GONE
                        binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.BLACK)
                        binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.WHITE)
                        binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.BLACK)
                    }

                    if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
                        binding.showPostPostThreadNextBtn.visibility = View.GONE
                        binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                    }
                } else if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
//                    fetchDataFromApi(MyDataClass.threadId, page)
                    binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                    binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                    binding.showPostPostThreadNextBtn.visibility = View.GONE
                    binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadShowNumberBtn3.visibility = View.GONE
                    binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.GONE
                    binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowCurrentNumber.visibility = View.GONE
                    binding.showPostPostThreadBottemShowCurrentNumber.visibility = View.GONE
                    binding.showPostPostThreadBottemShowCurrentNumber.setTextColor(Color.BLACK)
                    binding.showPostPostThreadShowCurrentNumber.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowCurrentNumber.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowCurrentNumber.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowDotsLayout.visibility = View.VISIBLE
                    binding.showPostPostThreadShowDotsLayout.visibility = View.VISIBLE
                } else if (page == 4) {
//                    fetchDataFromApi(MyDataClass.threadId, page)
                    binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                    binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn2.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn3.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.VISIBLE
                    binding.showPostPostThreadShowDotsLayout.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowDotsLayout.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowCurrentNumber.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowCurrentNumber.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowCurrentNumber.setTextColor(Color.WHITE)
                    binding.showPostPostThreadShowCurrentNumber.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowCurrentNumber.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowCurrentNumber.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowCurrentNumber.setText(page.toString())
                    binding.showPostPostThreadBottemShowCurrentNumber.setText(page.toString())
                    if (page == MyDataClass.paginationForPostsOfThreads.last_page) {
                        binding.showPostPostThreadNextBtn.visibility = View.GONE
                        binding.showPostPostThreadBottemNextBtn.visibility = View.GONE
                    }
                } else {
//                    fetchDataFromApi(MyDataClass.threadId, page)
                    binding.showPostsOfThreadsBottemPrevBtn.visibility = View.VISIBLE
                    binding.showPostsOfThreadsPrevBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadNextBtn.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadShowNumberBtn3.visibility = View.GONE
                    binding.showPostPostThreadBottemShowNumberBtn3.visibility = View.GONE
                    binding.showPostPostThreadShowCurrentNumber.visibility = View.VISIBLE
                    binding.showPostPostThreadBottemShowCurrentNumber.visibility = View.VISIBLE
                    binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn3.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowNumberBtn3.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn3.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowNumberBtn3.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowCurrentNumber.setText(page.toString())
                    binding.showPostPostThreadShowCurrentNumber.setText(page.toString())
                    binding.showPostPostThreadShowNumberBtn2.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn2.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn2.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn2.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadBottemShowNumberBtn2.visibility = View.GONE
                    binding.showPostPostThreadShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setTextColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowNumberBtn4.setBackgroundColor(Color.WHITE)
                    binding.showPostPostThreadShowCurrentNumber.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadShowCurrentNumber.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowCurrentNumber.setTextColor(Color.WHITE)
                    binding.showPostPostThreadBottemShowCurrentNumber.setBackgroundColor(Color.BLACK)
                    binding.showPostPostThreadBottemShowDotsLayout.visibility = View.VISIBLE
                    binding.showPostPostThreadShowDotsLayout.visibility = View.VISIBLE
                }
            }
        }


    private fun initializeData() {
        MyDataClass.page = 1
        list1.clear()
        MyDataClass.isGoForLatestPosts = false
        MyDataClass.isEnteredInShowDetails = false
        alertDialog = context?.let { AlertDialog.Builder(it) }
//        binding.showPostsOfThreadsRichEditor.setEditorHeight(100)
//        binding.showPostsOfThreadsRichEditor.setEditorFontSize(15)
//        binding.showPostsOfThreadsRichEditor.setEditorFontColor(Color.BLACK)
//        //mEditor.setEditorBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundResource(R.drawable.bg);
//        //mEditor.setEditorBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundResource(R.drawable.bg);
//        binding.showPostsOfThreadsRichEditor.setPadding(4, 4, 4, 4)
//        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
//        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
//        binding.showPostsOfThreadsRichEditor.setPlaceholder("Write reply...")
        MyDataClass.threadId = threadId
        progressBar = ProgressDialog(context)
        MyDataClass.attachmentFileListAttachmentId.clear()
        MyDataClass.attachmentFileListItem.clear()
    }

      fun  fetchDataFromApi(path: Int, page: Int = 1) {
        binding.showPostsOfThreadsProgressBar.visibility = View.VISIBLE
        binding.showPostsOfThreadsRecyclerView.visibility=View.GONE
        var retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getPostsOfThreadsResonse(MyDataClass.api_key,
            MyDataClass.myUserId,
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
                        var list = response.body()?.posts
                        var list1: LinkedList<Posts> = LinkedList()
                        MyDataClass.paginationForPostsOfThreads = response.body()!!.pagination
                        var pagination = response.body()!!.pagination
                        list?.let { list1.addAll(it) }
                        MyDataClass.JumpedToImagePageNum=page
                        binding.showPostsOfThreadsCategory.setText(title1)
                        binding.showPostsOfThreadsUserNameTv.setText(lastPostUsername)
                        binding.showPostsOfThreadsTitle.setText(category)
                        binding.showPostsOfThreadsPostDateTv.setText(lastPostDate)
                        binding.showPostsOfThreadsProgressBar.visibility = View.GONE
                        binding.showPostsOfThreadsRecyclerView.visibility=View.VISIBLE
                        binding.showPostsOfThreadsRecyclerView.adapter =
                            ShowPostsOfThreadsAdapter(list1, context,
                                response.body()!!.pagination, ::fetchDataFromApi)
                        binding.showPostsOfThreadsRecyclerView.layoutManager =
                            LinearLayoutManager(context)
                        if(requestCode==1001){
                            MyDataClass.page=page
                            checkPageNumberForAlerts(page)
                            binding.showPostsOfThreadsRecyclerView.scrollToPosition(position+1)
                        }
                        if(MyDataClass.isJumpedToImage){
                            binding.showPostsOfThreadsRecyclerView.scrollToPosition(MyDataClass.JumpToImagePosition)
                        }
                        if (pagination.last_page > 1) {
                            binding.showPostPostThreadShowPreAndNextLayout.visibility = View.VISIBLE
                            binding.showPostsOfThreadsBottemShowPreAndNextLayout.visibility =
                                View.VISIBLE
                            binding.showPostPostThreadShowNumberBtn1.setText("1")
                            binding.showPostPostThreadBottemShowNumberBtn1.setText("1")
                            binding.showPostPostThreadBottemShowNumberBtn4.setText(pagination.last_page.toString())
                            binding.showPostPostThreadShowNumberBtn4.setText(pagination.last_page.toString())
                            if (page == 1) {
                                binding.showPostPostThreadBottemShowNumberBtn1.setBackgroundColor(
                                    Color.BLACK)
                                binding.showPostPostThreadShowNumberBtn1.setBackgroundColor(Color.BLACK)
                                binding.showPostPostThreadBottemShowNumberBtn1.setTextColor(Color.WHITE)
                                binding.showPostPostThreadShowNumberBtn1.setTextColor(Color.WHITE)
                                binding.showPostsOfThreadsBottemPrevBtn.visibility = View.GONE
                                binding.showPostsOfThreadsPrevBtn.visibility = View.GONE
                                binding.showPostPostThreadShowCurrentNumber.visibility = View.GONE
                                binding.showPostPostThreadBottemShowCurrentNumber.visibility =
                                    View.GONE
                            }
                            if (pagination.last_page > 3) {
                                binding.showPostPostThreadShowDotsLayout.visibility = View.VISIBLE
                                binding.showPostPostThreadBottemShowDotsLayout.visibility =
                                    View.VISIBLE
                            }
                        } else {
                            binding.showPostPostThreadShowPreAndNextLayout.visibility = View.GONE
                            binding.showPostsOfThreadsBottemShowPreAndNextLayout.visibility =
                                View.GONE
                        }

                    } else {
                        binding.showPostsOfThreadsProgressBar.visibility = View.GONE
                    }

                }

                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                    binding.showPostsOfThreadsProgressBar.visibility = View.GONE
                }
            })

    }
    private fun hitApi(gettedAttachmentKey: String, attachmentFileString: String) {
        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getResponseOfComments(MyDataClass.api_key,
            MyDataClass.myUserId,
            threadId,
            binding.showPostsOfThreadsMessageEt.text.toString() + attachmentFileString,
            gettedAttachmentKey)
            .enqueue(object : Callback<ResponseThread> {
                override fun onResponse(
                    call: Call<ResponseThread>,
                    response: Response<ResponseThread>,
                ) {
                    if (response.isSuccessful) {
                        progressBar.dismiss()
                        isAttachedFile = false
                        api.getPostsOfThreadsResonse(MyDataClass.api_key,
                            MyDataClass.myUserId,
                            threadId,
                            1,
                            "desc",
                            "last_post_date")
                            .enqueue(object : retrofit2.Callback<ResponseThread> {
                                override fun onResponse(
                                    call: Call<ResponseThread>,
                                    response: Response<ResponseThread>,
                                ) {
                                    if (response.isSuccessful && response.body() != null) {
                                        list1.clear()
                                        fetchDataFromApi(threadId)
                                        MyDataClass.attachmentFileListItem.clear()
                                        MyDataClass.attachmentFileListAttachmentId.clear()
                                        funAdapter()
                                    } else {
                                        progressBar.dismiss()
                                    }
                                }

                                override fun onFailure(call: Call<ResponseThread>, t: Throwable) {
                                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG)
                                    progressBar.dismiss()
                                }
                            })
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
         if(data?.data!=null){
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
         }else{
             progressBar.dismiss()
         }

        }

    }

    private fun funAdapter() {
        if (MyDataClass.attachmentFileListItem.size > 0) {
            binding.showPostsOfThreadsRecyclerViewForShowAttachFiles.visibility = View.VISIBLE
            binding.showPostsOfThreadsRecyclerViewForShowAttachFiles.adapter =
                context?.let {
                    ShowAttachmentFilesAdapter(it,
                        MyDataClass.attachmentFileListItem,
                        MyDataClass.attachmentFileListAttachmentId)
                }
            binding.showPostsOfThreadsRecyclerViewForShowAttachFiles.layoutManager =
                LinearLayoutManager(context)
            binding.showPostsOfThreadsRecyclerViewForShowAttachFiles.adapter?.notifyDataSetChanged()
        } else {
            binding.showPostsOfThreadsRecyclerViewForShowAttachFiles.visibility = View.GONE
        }
    }

    private fun postAttachmentFile(
        fileToUpload: MultipartBody.Part,
        attachmentKey: RequestBody,
        api: HitApi,
    ) {
        api.postAttachmentFile(MyDataClass.api_key,
            MyDataClass.myUserId, fileToUpload, attachmentKey
        ).enqueue(object : Callback<ResponseThread> {
            override fun onResponse(
                call: Call<ResponseThread>,
                response: Response<ResponseThread>,
            ) {
                Log.d("TAG", "CODE ${response.code()} and ${fileToUpload} and ${attachmentKey}")
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
        api.generateAttachmentKey(MyDataClass.api_key,
            MyDataClass.myUserId,
            threadId,
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


}