package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Environment
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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Posts
import com.example.fatchcurrentlocation.Fragments.PostReplyForConversation
import com.example.fatchcurrentlocation.HtmlmageWork.ImageGetter
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.sufficientlysecure.htmltextview.ClickableTableSpan
import org.sufficientlysecure.htmltextview.DrawTableLinkSpan
import org.sufficientlysecure.htmltextview.HtmlTextView
import org.sufficientlysecure.htmltextview.OnClickATagListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class ShowAndReplyConversationAdapter(
    val _list: List<Posts>,
    val _context: Context?,
) : RecyclerView.Adapter<ShowAndReplyConversationAdapter.ShowAndReplyConversationViewHolder>() {
    var list = _list
    var context = _context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShowAndReplyConversationViewHolder {
        var view =
            LayoutInflater.from(context)
                .inflate(R.layout.show_posts_of_threads_custom_layout, parent, false)
        return ShowAndReplyConversationViewHolder(view)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: ShowAndReplyConversationViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.userName.setText(list.get(position).User.username)
        var string: String = list.get(position).message_parsed
        var string1: String = list.get(position).message_parsed
        var upMessage: String = ""
        val tags: String = list.get(position).message_parsed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.webView.settings.setMixedContentMode(0);
            holder.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            holder.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            holder.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        holder.htmlTextView.setLayerType(HtmlTextView.LAYER_TYPE_NONE, null)
        holder.htmlTextView.setOnClickATagListener(object : OnClickATagListener {
            override fun onClick(widget: View?, spannedText: String?, href: String?): Boolean {
                return false
            }
        })
        holder.htmlTextView.blockQuoteStripWidth=0f
        holder.htmlTextView.blockQuoteGap=20f
        holder.htmlTextView.blockQuoteBackgroundColor= Color.parseColor("#E7E1E1")
        holder.htmlTextView.setClickableTableSpan(
            ShowAndReplyConversationAdapter.ClickableTableSpanImpl(
                holder.webView
            )
        )
        val drawTableLinkSpan = DrawTableLinkSpan()
        drawTableLinkSpan.tableLinkText = "[Show table]"
        holder.htmlTextView.setDrawTableLinkSpan(drawTableLinkSpan)
        val metrics = DisplayMetrics()
        holder.htmlTextView.setListIndentPx(metrics.density * 10)
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

      /*  if (list.get(position).message_parsed.contains("<img")) {
            var data = Jsoup.parse(list.get(position).message_parsed)
            var message=list.get(position).message_parsed
            var imagTag = data.select("img")
            for (image in imagTag){
                var img=image.toString().replace(">", " />")
                message=message.replace(img, "")
            holder.htmlTextView.setHtml(message)
            }
        } else {
            holder.htmlTextView.setHtml(list.get(position).message_parsed)
        }*/

//        if (string.contains("<blockquote class=")) {
//            if (!string.startsWith("<blockquote class=\"xfBb-quote")) {
//                var k = string.split("<blockquote class=\"xfBb-quote")
//                holder.messageUp.visibility = View.VISIBLE
//                upMessage = Jsoup.parse(k[0]).text()
//                holder.messageUp.setText(upMessage)
//            }
//            holder.blockQuoteLayout.visibility = View.VISIBLE
//            string = string.substring(string.indexOf("\">") + 2)
//            string = string.substring(0, string.indexOf("</blockquote>"))
//            holder.blockQuoteMessage.setText(Jsoup.parse(string).text())
//            var k: String = string1.replace(string, "")
//            var d: Document = Jsoup.parse(k)
//            holder.messageDown.setText(d.text().replace(upMessage, ""))
//        } else {
//            holder.blockQuoteLayout.visibility = View.GONE
//            holder.messageUp.visibility = View.GONE
//            holder.messageDown.setText(Jsoup.parse(list.get(position).message_parsed).text())
//        }
//        isReactedTo(holder, position)
        var str1 = Jsoup.parse(list.get(position).message_parsed).text()
        var date = Date((list.get(position).User.last_activity as Long) * 1000)
        var simple = SimpleDateFormat("dd yyyy")
        holder.postDate.setText("${DateFormatSymbols().getShortMonths()[date.month]} ${
            simple.format(date)
        }")
        holder.linearLayout.visibility = View.GONE
        holder.likeBtn.visibility = View.GONE
        if (list.get(position).User.avatar_urls.o == null) {
            holder.profileImage_tv.visibility = View.VISIBLE
            holder.ProfileImage.visibility = View.GONE
            holder.profileImage_tv.gravity = Gravity.CENTER
            holder.profileImage_tv.setText(list.get(position).User.username.get(0).toString())
        } else {
            Picasso.get().load(list.get(position).User.avatar_urls.o).placeholder(R.drawable.person)
                .into(holder.ProfileImage)
        }
        if (list.get(position).is_staff
        ) {
            holder.userTitle.setText(list.get(position).User.user_title)
        } else if (list.get(position).is_admin || list.get(position).User.user_title.toString()
                .equals("Administrator")
        ) {
            holder.member.visibility = View.GONE
            holder.admin.visibility = View.VISIBLE
            holder.founder.visibility = View.GONE
            holder.userTitle.setText(list.get(position).User.user_title)
        } else {
            holder.member.visibility = View.GONE
            holder.admin.visibility = View.GONE
            holder.founder.visibility = View.GONE
            holder.userTitle.setText(list.get(position).User.user_title)
        }
        holder.deleteBtn.visibility = View.GONE
        holder.replyBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var message=""
                if (string.contains("<blockquote class=")) {
                    string = string.substring(string.indexOf("\">") + 2)
                    string = string.substring(0, string.indexOf("</blockquote>"))
                    var k: String = string1.replace(string, "")
                    var d: Document = Jsoup.parse(k)
                    message=d.text().replace(upMessage, "")
                } else {
                    message=Jsoup.parse(list.get(position).message_parsed).text()
                }
                var transaction = MyDataClass.getTransaction()
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    PostReplyForConversation(message,
                        list.get(position).User.username,
                        list.get(position).conversation_id,
                        list.get(position).message_id,
                        list.get(position).user_id))
                transaction.addToBackStack(null).commit()
            }
        })
        holder.ProfileImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
//                MyDataClass.homeNestedScrollView.visibility = View.GONE
//                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
//                var transaction = MyDataClass.getTransaction()
//                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
//                    UserProfile(list, position))
//                transaction.addToBackStack(null).commit()
            }
        })
    }

   /* private fun callGetAttachmentPicApi(position: Int, holder: ShowAndReplyConversationViewHolder) {
        var listOfAttachmentPics: LinkedList<Bitmap> = LinkedList()
        var retrofit = RetrofitManager.getRetrofit1()
        var api = retrofit.create(HitApi::class.java)
        for (i in list.get(position).Attachments) {
            api.getAttachments(MyDataClass.api_key, MyDataClass.myUserId, i.attachment_id)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        if (response.isSuccessful) {
                            var byteArray = response.body()?.bytes()
                            var base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
                            var decodedString = Base64.decode(base64, Base64.DEFAULT)
                            var bitmap = BitmapFactory.decodeByteArray(decodedString,
                                0,
                                decodedString.size)
                            listOfAttachmentPics.add(bitmap)
                            holder.recyclerView.visibility = View.VISIBLE
                            holder.recyclerView.adapter = context?.let { ShowAttachmentImageAdapter(listOfAttachmentPics, it,position) }
                            holder.recyclerView.layoutManager = LinearLayoutManager(context)
//                                var path = saveBitmap(bitmap)
//                                Log.d("TAG", "saved in $path")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("TAG", "${t.localizedMessage}")
                    }
                })
//                Log.d("TAG","${i.thumbnail_url}")
//                list1.add(i.thumbnail_url)
        }
    }*/

    private fun saveBitmap(bitmap: Bitmap?): String {
        var path = Environment.getExternalStorageDirectory().absolutePath + "//file_name.png"
        var file = File(path)
        if (!file.exists()) {
            file.createNewFile()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
        }
        return path
    }

    override fun getItemCount(): Int {
        return list?.size!!
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
    class ShowAndReplyConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ProfileImage: CircleImageView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_UserProfileImage)
        var profileImage_tv: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_UserProfileImage_tv)
        var userName: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_userName_tv)
        var member: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_member_btn)
        var founder: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_founder_btn)
        var admin: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_admin_btn)
        var postDate: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_post_date_tv2)
        var position: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_position_tv)
        var htmlTextView: HtmlTextView =itemView.findViewById(R.id.show_posts_of_threads_custom_layout_htmlTextviewDown)
//        var messageDown: TextView =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_message_tvDown)
//        var messageUp: TextView =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_message_tvUp)
//        var blockQuoteLayout: LinearLayout =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_blockQuote_Layout)
        var webView: WebView=
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_webviewForTable)
        var userTitle: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_usertitle_Tv)
        var replyBtn: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_reply_btn)
        var fragment: FragmentContainerView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_Fragment)
        var likeCounts: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_like_counts)
               var linearLayout: LinearLayout =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_LinearLayout)
        var likeBtn: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_like_btn)
        var deleteBtn: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_deleteBtn)
    }

//    private fun isReactedTo(holder: ShowAndReplyConversationViewHolder, position: Int) {
//        if (list.get(position).is_reacted_to) {
//            if (list.get(position).reaction_score > 0) {
//                if ((list.get(position).reaction_score - 1) != 0) {
//                    holder.likeCounts.visibility = View.VISIBLE
//                    holder.likeCounts.setText("You,${list.get(position).reaction_score - 1} other")
//                } else {
//                    holder.likeCounts.visibility = View.VISIBLE
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
//            if (list.get(position).reaction_score > 0) {
//                holder.likeCounts.visibility = View.VISIBLE
//                holder.likeCounts.setText("${list.get(position).reaction_score}")
//            } else {
//                holder.likeCounts.visibility = View.GONE
//            }
//        }
//
//    }
// Function to parse image tags and enable click events
fun ImageClick(html: Spannable, position: Int) {
    for (span in html.getSpans(0, html.length, ImageSpan::class.java)) {
        val flags = html.getSpanFlags(span)
        val start = html.getSpanStart(span)
        val end = html.getSpanEnd(span)
        html.setSpan(object : URLSpan(span.source) {
            override fun onClick(v: View) {
                Log.d("TAG", "onClick: url is ${span.source}")
                var intent= Intent(context,ShowGridImageView::class.java)
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