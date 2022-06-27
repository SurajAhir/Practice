package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.ProfilePosts
import com.example.fatchcurrentlocation.DataClasses.ResponseThread
import com.example.fatchcurrentlocation.HtmlmageWork.ImageGetter
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.ShowGridImageView
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import jp.wasabeef.richeditor.RichEditor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.sufficientlysecure.htmltextview.ClickableTableSpan
import org.sufficientlysecure.htmltextview.DrawTableLinkSpan
import org.sufficientlysecure.htmltextview.HtmlTextView
import org.sufficientlysecure.htmltextview.OnClickATagListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class MyProfileAdapter(
    val context: Context,
    val list: LinkedList<ProfilePosts>,
    val userId: Int,
    val activity: FragmentActivity?,
) : RecyclerView.Adapter<MyProfileAdapter.MyProfileHolder>() {
    var isChangedTextStyleBold = false
    var isChangedTextStyleItalic = false
    var attachmentId = 0
    var isAttachedFile: Boolean = false
    var gettedAttachmentKey: String = ""
    var attachmentFileString = ""
    var isGeneratedAttachmentKey = false
    lateinit var attachmentRequestBodyKey: RequestBody
    lateinit var holder: MyProfileAdapter.MyProfileHolder
    var position: Int = 0
    var progressBar: ProgressDialog = ProgressDialog(context)
    var gettedTextFromEditor: String = ""
    var alertDialog: androidx.appcompat.app.AlertDialog.Builder =
        context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
    var alertDialog1: androidx.appcompat.app.AlertDialog? = null
    var centername = ""
    var rightname = ""
    var sizes = arrayOf("9", "10", "12", "15", "18", "22", "26")
    var isOpenedLink = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProfileHolder {
        var view = LayoutInflater.from(context)
            .inflate(R.layout.fragment_my_profile_custom_layout, parent, false)
        return MyProfileHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyProfileHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {

        holder.richEditor.setEditorFontSize(15)
        holder.richEditor.setEditorFontColor(Color.BLACK)
        holder.richEditor.setPadding(4, 4, 4, 4)
        holder.richEditor.setPlaceholder("Write comment...")
        holder.richEditor.setEditorHeight(100)
        holder.richEditor.setOnTextChangeListener(RichEditor.OnTextChangeListener { text -> //                Log.d("TAG", text);
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
        holder.textBoldBtn.setOnClickListener { holder.richEditor.setBold() }
//        holder.indentBtn.setOnClickListener { holder.richEditor.setIndent() }
//        holder.outdentBtn.setOnClickListener { holder.richEditor.setOutdent() }
        holder.textItalicBtn.setOnClickListener { holder.richEditor.setItalic() }
        holder.textUnderlineBtn.setOnClickListener { holder.richEditor.setUnderline() }
        holder.textUndoBtn.setOnClickListener { holder.richEditor.undo() }
        holder.textRedoBtn.setOnClickListener { holder.richEditor.redo() }
        holder.textBulletsBtn.setOnClickListener { holder.richEditor.setBullets() }
        holder.textNumbersBtn.setOnClickListener { holder.richEditor.setNumbers() }
        holder.textIncreaseSizeBtn.setOnClickListener {
            alertDialog1 = alertDialog!!.create()
            alertDialog1!!.show()
            alertDialog1!!.window!!.setLayout(300, 600)
            alertDialog1!!.show()
        }
        holder.textAlignLeftBtn.setOnClickListener { holder.richEditor.setAlignLeft() }
        holder.textAlignCenterBtn.setOnClickListener { holder.richEditor.setAlignCenter() }
        holder.textAlignRightBtn.setOnClickListener { holder.richEditor.setAlignRight() }
        holder.inserLinke.setOnClickListener {
            if (isOpenedLink) {
                holder.inserLinkeLayout.visibility = View.GONE
                isOpenedLink = false
            } else {
                holder.inserLinkeLayout.visibility = View.VISIBLE
                isOpenedLink = true
            }
        }
        holder.inserLinkeBtn.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(p0: View?) {
                if (holder.url.text.isEmpty()) {
                    holder.url.setError("Please enter a valid URL")
                    holder.url.focusable = View.FOCUSABLE
                } else if (holder.title.text.toString().isEmpty()) {
                    holder.title.setError("Please enter a valid text")
                    holder.title.focusable = View.FOCUSABLE
                } else {
                    holder.richEditor.insertLink(holder.url.text.toString(),
                        holder.title.text.toString())
                    holder.title.setText("")
                    holder.url.setText("")
                    holder.inserLinkeLayout.visibility = View.GONE
                }
            }
        })
        alertDialog!!.setItems(sizes
        ) { dialogInterface, i ->
            when (i) {
                0 -> {
                    holder.richEditor.setFontSize(1)
                    Log.d("TAG", "clicked$i")
                }
                1 -> {
                    holder.richEditor.setFontSize(2)
                    Log.d("TAG", "clicked$i")
                }
                2 -> {
                    holder.richEditor.setFontSize(3)
                    Log.d("TAG", "clicked$i")
                }
                3 -> {
                    holder.richEditor.setFontSize(4)
                    Log.d("TAG", "clicked$i")
                }
                4 -> {
                    holder.richEditor.setFontSize(5)
                    Log.d("TAG", "clicked$i")
                }
                5 -> {
                    holder.richEditor.setFontSize(6)
                    Log.d("TAG", "clicked$i")
                }
                6 -> {
                    holder.richEditor.setFontSize(7)
                    Log.d("TAG", "clicked$i")
                }
            }
        }
        if (list.get(position).User.avatar_urls.o == null) {
            holder.profileImage_tv.visibility = View.VISIBLE
            holder.ProfileImage.visibility = View.GONE
            holder.profileImage_tv.gravity = Gravity.CENTER
            holder.profileImage_tv.setText(list.get(position).User.username.get(0).toString())
        } else {
            Picasso.get().load(list.get(position).User.avatar_urls.o).placeholder(R.drawable.person)
                .into(holder.ProfileImage)
        }
        holder.userName.setText(list.get(position).username)
        holder.htmlTextView.setOnClickATagListener(object : OnClickATagListener {
            override fun onClick(widget: View?, spannedText: String?, href: String?): Boolean {
                return false
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.webViewDown.settings.setMixedContentMode(0);
            holder.webViewDown.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            holder.webViewDown.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            holder.webViewDown.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        holder.htmlTextView.setLayerType(HtmlTextView.LAYER_TYPE_NONE, null)
        holder.htmlTextView.setClickableTableSpan(
            MyProfileAdapter.ClickableTableSpanImpl(
                holder.webViewDown
            )
        )
        val drawTableLinkSpan = DrawTableLinkSpan()
        drawTableLinkSpan.tableLinkText = "[Show table]"
        holder.htmlTextView.setDrawTableLinkSpan(drawTableLinkSpan)
        val metrics = DisplayMetrics()
        holder.htmlTextView.setListIndentPx(metrics.density * 10)
        var string: String = list.get(position).message_parsed.replace("(<(/)img>)|(<img. +?>)", "")
        var string1: String =
            list.get(position).message_parsed.replace("(<(/)img>)|(<img. +?>)", "")
        var upMessage: String = ""
        holder.htmlTextView.blockQuoteBackgroundColor = Color.parseColor("#BCE7E1E1")
        holder.htmlTextView.blockQuoteStripWidth = 0f
        holder.htmlTextView.blockQuoteGap = 20f
        holder.htmlTextView.setLineSpacing(0f, 1.6f)
        if (list.get(position).message_parsed.contains("<img")) {
            if(list.get(position).attach_count>0){
                var data = Jsoup.parse(list.get(position).message_parsed)
                var message=list.get(position).message_parsed
                var imagTag = data.select("img")
                var imageList:ArrayList<String> =ArrayList()
                for (image in list.get(position).Attachments){
                    imageList.add(image.thumbnail_url)
                }
                for (i in 0..imagTag.size-1){
                    var img=imagTag[i].toString().replace(">", " />")
                    var imageUrl="<img src='${imageList.get(i)}'/>"
                    message=message.replace(img, imageUrl)
                    val imageGetter = context?.resources?.let { ImageGetter(it, holder.htmlTextView) }

                    val styledText =
                        HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter,null)

//                    replaceQuoteSpans(styledText as Spannable)
                    ImageClick(styledText as Spannable,position)
                    // setting the text after formatting html and downloading and setting images
                    holder.htmlTextView.text = styledText
                    holder.htmlTextView.movementMethod = LinkMovementMethod.getInstance()
                }
            }
        } else {
            if(list.get(position).attach_count>0){
                var message=list.get(position).message_parsed
                var imageList:ArrayList<String> =ArrayList()
                for (image in list.get(position).Attachments){
                    imageList.add(image.thumbnail_url)
                }
                for (i in 0..imageList.size-1){
                    var imageUrl="<br /><img src='${imageList.get(i)}'/>"
                    message=message+imageUrl
                }
                val imageGetter = context?.resources?.let { ImageGetter(it, holder.htmlTextView) }

                val styledText =
                    HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter,null)

//                replaceQuoteSpans(styledText as Spannable)
                ImageClick(styledText as Spannable,position)
                // setting the text after formatting html and downloading and setting images
                holder.htmlTextView.text = styledText
                holder.htmlTextView.movementMethod = LinkMovementMethod.getInstance()
            }else{

                holder.htmlTextView.setHtml(list.get(position).message_parsed)
            }
        }
//        holder.message.setText(Jsoup.parse(list.get(position).message_parsed).text())
        var date = Date((list.get(position).post_date as Long) * 1000)
        var simple = SimpleDateFormat("dd yyyy")
        holder.postDate.setText("${DateFormatSymbols().getShortMonths()[date.month]} ${
            simple.format(date)
        }")
        holder.postComment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                holder.postCommentLayout.visibility = View.VISIBLE
                holder.postComment.visibility = View.GONE
            }
        })
        holder.postBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (holder.editText.text.toString().isEmpty()) {
                    return
                } else {
                    progressBar.show()
                    this@MyProfileAdapter.holder = holder
                    this@MyProfileAdapter.position = position
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
                        hitApi(api, holder.editText.text.toString(), gettedAttachmentKey)
                    } else {
                        var retrofit = RetrofitManager.getRetrofit1()
                        var api = retrofit.create(HitApi::class.java)
                        hitApi(api,holder.editText.text.toString(), "")
                    }

                }
            }

        })
        holder.deleteBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                var alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
                alertDialog.setTitle("Delete")
                    .setMessage("Are you sure you want to delete this post")
                    .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            api.deleteSpecificPost(MyDataClass.api_key,
                                MyDataClass.myUserId,
                                list.get(position).profile_post_id)
                                .enqueue(object : Callback<Map<String, Boolean>> {
                                    override fun onResponse(
                                        call: Call<Map<String, Boolean>>,
                                        response: Response<Map<String, Boolean>>,
                                    ) {
                                        if (response.isSuccessful) {
                                            if (response.body()?.get("success") == true) {
                                                MyDataClass.onBack()
                                            }
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<Map<String, Boolean>>,
                                        t: Throwable,
                                    ) {
                                        MyDataClass.onBack()
                                    }
                                })

                        }
                    }).setNegativeButton("No", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            p0?.dismiss()
                        }
                    }).show()
            }
        })
        holder.attachFileBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                this@MyProfileAdapter.holder = holder
                this@MyProfileAdapter.position = position
                Log.d("TAG", "size34 $holder")
                Log.d("TAG", "ay methodd")
                MyDataClass.onReceiveData = ::onReceiveData
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent = Intent.createChooser(intent, "Choose a file")
                activity?.startActivityForResult(intent, 1002)
            }
        })
    }
//    private fun callGetAttachmentPicApi(position: Int, holder: MyProfileHolder) {
//        var listOfAttachmentPics: LinkedList<Bitmap> = LinkedList()
//        var retrofit = RetrofitManager.getRetrofit1()
//        var api = retrofit.create(HitApi::class.java)
//        for (i in list.get(position).Attachments) {
//            Log.d("TAG","id ${i.attachment_id}")
//            api.getAttachments(MyDataClass.api_key, MyDataClass.myUserId, i.attachment_id)
//                .enqueue(object : Callback<ResponseBody> {
//                    override fun onResponse(
//                        call: Call<ResponseBody>,
//                        response: Response<ResponseBody>,
//                    ) {
//                        Log.d("TAG", "url ${response.code()}")
//                        if (response.isSuccessful) {
//                            Log.d("TAG", "${response.body()}")
//                            var byteArray = response.body()?.bytes()
//                            var base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
//                            var decodedString = Base64.decode(base64, Base64.DEFAULT)
//                            var bitmap = BitmapFactory.decodeByteArray(decodedString,
//                                0,
//                                decodedString.size)
//                            listOfAttachmentPics.add(bitmap)
//                            holder.recyclerView.visibility = View.VISIBLE
//                            holder.recyclerView.adapter = context?.let { ShowAttachmentImageAdapter(
//                                listOfAttachmentPics,
//                                it,
//                                position) }
//                            holder.recyclerView.layoutManager = LinearLayoutManager(context)
////                                Log.d("TAG", bitmap.toString())
////                                var path = saveBitmap(bitmap)
////                                Log.d("TAG", "saved in $path")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        Log.d("TAG", "${t.localizedMessage}")
//                    }
//                })
////                Log.d("TAG","${i.thumbnail_url}")
////                list1.add(i.thumbnail_url)
//        }
//    }
//    private fun isReactedTo(holder: MyProfileHolder, position: Int) {
//        if (list.get(position).is_reacted_to) {
//            if (list.get(position).reaction_score > 0) {
//                if ((list.get(position).reaction_score - 1) != 0) {
//                    holder.likeCounts.visibility=View.VISIBLE
//                    holder.likeCounts.setText("You,${list.get(position).reaction_score - 1} other")
//                } else {
//                    holder.likeCounts.visibility=View.VISIBLE
//                    holder.likeCounts.setText("You")
//                }
//            } else {
//                holder.likeCounts.visibility = View.GONE
//            }
//            when (list.get(position).visitor_reaction_id) {
//                1 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#0B18CC"))
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setText("Like")
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_like,
//                        0,
//                        0,
//                        0)
//                }
//                2 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#BF0404"))
//                    holder.likeBtn.setText("Love")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_love_icon,
//                        0,
//                        0,
//                        0)
//                }
//                3 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
//                    holder.likeBtn.setText("haha")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_haha_icon,
//                        0,
//                        0,
//                        0)
//                }
//                4 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
//                    holder.likeBtn.setText("Wow")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wow_icon,
//                        0,
//                        0,
//                        0)
//                }
//                5 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
//                    holder.likeBtn.setText("Sad")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sad_icon,
//                        0,
//                        0,
//                        0)
//                }
//                6 -> {
//                    holder.likeBtn.setTextColor(Color.parseColor("#FB2707"))
//                    holder.likeBtn.setText("Angery")
//                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
//                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_angry_icon,
//                        0,
//                        0,
//                        0)
//                }
//            }
//
//        } else {
//            holder.likeBtn.setTextColor(Color.parseColor("#FF000000"))
//            holder.likeBtn.setTypeface(null, Typeface.NORMAL)
//            holder.likeBtn.setText("Like")
//            holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,
//                0,
//                0,
//                0)
//            if(list.get(position).reaction_score>0){
//                holder.likeCounts.visibility=View.VISIBLE
//                holder.likeCounts.setText("${list.get(position).reaction_score}")
//            }else{
//                holder.likeCounts.visibility=View.GONE
//            }
//        }
//
//    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun funAdapter() {
        Log.d("TAG", "size ${MyDataClass.attachmentFileListItem.size}")
        if (MyDataClass.attachmentFileListItem.size > 0) {
            holder.attachRecyclerView.visibility = View.VISIBLE
            Log.d("TAG", "size ${holder}")
            holder.attachRecyclerView.adapter =
                context?.let {
                    ShowAttachmentFilesAdapter(it,
                        MyDataClass.attachmentFileListItem,
                        MyDataClass.attachmentFileListAttachmentId)
                }
            holder.attachRecyclerView.layoutManager = LinearLayoutManager(context)
            holder.attachRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            holder.attachRecyclerView.visibility = View.GONE
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
                Log.d("TAG", "response ${response.code()}")
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
        api.generateAttachmentKeyForUserProfileOfComments(MyDataClass.api_key,
            MyDataClass.myUserId,
            list.get(position).profile_post_id,
            "profile_post_comment")
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>,
                ) {
                    Log.d("TAG", "response ${response.code()}")
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

    private fun hitApi(api: HitApi, message: String, gettedAttachmentKey: String) {
        Log.d("TAG", "respone ${message} ${gettedAttachmentKey} ${userId}")
        api.getResponseOfProfilePostsOfComments(MyDataClass.api_key,
            MyDataClass.myUserId,
            list.get(position).profile_post_id,
            (message + attachmentFileString),
            gettedAttachmentKey)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>,
                ) {
                    Log.d("TAG", "${response.code()}")
                    if (response.body()?.get("success") == true) {
                        progressBar.dismiss()
                        Log.d("TAG", "respon ${response.code()}")
                        holder.postComment.visibility = View.VISIBLE
                        holder.postCommentLayout.visibility = View.GONE
                    } else {
                        progressBar.dismiss()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    progressBar.dismiss()
                    Log.d("TAG", "${t.localizedMessage} errro")
                }
            })
    }

    private fun onReceiveData(data: String) {
        progressBar.show()
        var file: File = File(data)
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
    internal class ClickableTableSpanImpl(val webView: WebView) : ClickableTableSpan() {


        override fun newInstance(): ClickableTableSpan {
            return ClickableTableSpanImpl(webView)
        }

        override fun onClick(widget: View) {
            val tableHtml = getTableHtml()
            Log.d("TAG", tableHtml.toString())
            webView.loadData(tableHtml!!, "text/html", "UTF-8")

        }
    }
    class MyProfileHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ProfileImage: CircleImageView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_UserProfileImage)
        var profileImage_tv: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_UserProfileImage_tv)
        var userName: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_userName_tv)
        var postDate: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_postDate_tv)
//        var message: TextView =
//            itemView.findViewById(R.id.fragment_My_profile_custom_layout_message_tv)
        var htmlTextView: HtmlTextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_htmlTextviewDown)
        var postComment: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_writeSomething_tv)
        var postCommentLayout: LinearLayout =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_writeSomething_LayoutToShow)
        var textBoldBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_bold)
        var textItalicBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_italic)
        var textUnderlineBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_underline)
        var textUndoBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_undo)
        var textRedoBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_redo)
        var textIncreaseSizeBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_font_size_btn)
        var recyclerView:RecyclerView=itemView.findViewById(R.id.fragment_My_profile_custom_recyclerView)
        var textBulletsBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_insert_bullets)
        var textNumbersBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_insert_numbers)
        var textAlignLeftBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_align_left)
        var textAlignCenterBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_align_center)
        var textAlignRightBtn: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_align_right)
        var inserLinke: ImageButton =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_insert_link)
        var inserLinkeBtn: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_insert_link_btn)
        var inserLinkeLayout: LinearLayout =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_insert_link_Layout)
        var url: EditText = itemView.findViewById(R.id.fragment_My_profile_custom_layout_Url_et)
        var title: EditText = itemView.findViewById(R.id.fragment_My_profile_custom_layout_Text_et)
        var postBtn: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_post_Reply_btn)
        var richEditor: RichEditor =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_RichEditor)
//        var indentBtn:ImageButton=itemView.findViewById(R.id.fragment_My_profile_custom_layout_indent)
//        var outdentBtn:ImageButton=itemView.findViewById(R.id.fragment_My_profile_custom_layout_outdent)
        var deleteBtn: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_delete_btn)
        var editText: EditText =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_write_message)
        var attachFileBtn: TextView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_attach_file_btn)
        var attachRecyclerView: RecyclerView =
            itemView.findViewById(R.id.fragment_My_profile_custom_layout_showAttachmentsRecyclerView)
        var webViewDown:WebView=itemView.findViewById(R.id.fragment_My_profile_custom_layout_webviewForTable)

    }
    // Function to parse image tags and enable click events
    fun ImageClick(html: Spannable, position: Int) {
        for (span in html.getSpans(0, html.length, ImageSpan::class.java)) {
            val flags = html.getSpanFlags(span)
            val start = html.getSpanStart(span)
            val end = html.getSpanEnd(span)
            html.setSpan(object : URLSpan(span.source) {
                override fun onClick(v: View) {
                    Log.d("TAG", "onClick: url is ${span.source}")
                    var intent=Intent(context, ShowGridImageView::class.java)
                    var listOfAttachment:ArrayList<Int> = ArrayList()
                    for (i in list.get(position).Attachments){
                        listOfAttachment.add(i.attachment_id)
                    }
                    intent.putIntegerArrayListExtra("listOfAttachment",listOfAttachment)
                    context?.startActivity(intent)
                }
            }, start, end, flags)
        }
    }
}