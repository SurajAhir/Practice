package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Pagination
import com.example.fatchcurrentlocation.DataClasses.Posts
import com.example.fatchcurrentlocation.Fragments.PostComments
import com.example.fatchcurrentlocation.Fragments.UserProfile
import com.example.fatchcurrentlocation.HtmlmageWork.ImageGetter
import com.example.fatchcurrentlocation.services.HitApi
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.services.RetrofitManager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.sufficientlysecure.htmltextview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.QuoteSpan
import android.text.style.URLSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.persistableBundleOf
import com.example.fatchcurrentlocation.HtmlmageWork.QuoteSpanClass
import com.example.fatchcurrentlocation.ShowGridImageView
import com.example.fatchcurrentlocation.databinding.ActivityJustPracticeBinding


class ShowPostsOfThreadsAdapter(
    val _list: LinkedList<Posts>,
    val _context: Context?,
    val pagination: Pagination,
    val kFunction2: (Int) -> Unit,
) : RecyclerView.Adapter<ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder>() {
    var list = _list
    var context = _context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShowPostsOfThreadsViewHolder {
        var view =
            LayoutInflater.from(context)
                .inflate(R.layout.show_posts_of_threads_custom_layout, parent, false)
        return ShowPostsOfThreadsViewHolder(view)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: ShowPostsOfThreadsViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.htmlTextViewDown.setOnClickATagListener(object : OnClickATagListener {
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
        holder.htmlTextViewDown.setLayerType(HtmlTextView.LAYER_TYPE_NONE, null)
        holder.htmlTextViewDown.setClickableTableSpan(ClickableTableSpanImpl(holder.webViewDown))
        val drawTableLinkSpan = DrawTableLinkSpan()
        drawTableLinkSpan.tableLinkText = "[Show table]"
        holder.htmlTextViewDown.setDrawTableLinkSpan(drawTableLinkSpan)
        val metrics = DisplayMetrics()
        holder.htmlTextViewDown.setListIndentPx(metrics.density * 10)
        var string: String = list.get(position).message_parsed.replace("(<(/)img>)|(<img. +?>)", "")
        var string1: String =
            list.get(position).message_parsed.replace("(<(/)img>)|(<img. +?>)", "")
        var upMessage: String = ""
        holder.htmlTextViewDown.blockQuoteStripWidth = 0f
        holder.htmlTextViewDown.blockQuoteGap = 20f
        holder.htmlTextViewDown.setLineSpacing(0f, 1.6f)
        holder.htmlTextViewDown.blockQuoteBackgroundColor = Color.parseColor("#BCE7E1E1")
//        if (list.get(position).message_parsed.contains("<img")) {
//            var data = Jsoup.parse(list.get(position).message_parsed)
//            var message=list.get(position).message_parsed
//            var imagTag = data.select("img")
//            for (image in imagTag){
//                var img=image.toString().replace(">", " />")
//                message=message.replace(img, "")
//                holder.htmlTextViewDown.setHtml(message)
//            }
//        } else {
//            holder.htmlTextViewDown.setHtml(list.get(position).message_parsed)
//        }



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
                    val imageGetter = context?.resources?.let { ImageGetter(it, holder.htmlTextViewDown) }

                    val styledText =
                        HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter,null)

//                    replaceQuoteSpans(styledText as Spannable)
                    ImageClick(styledText as Spannable,position)
                    // setting the text after formatting html and downloading and setting images
                    holder.htmlTextViewDown.text = styledText
                    holder.htmlTextViewDown.movementMethod = LinkMovementMethod.getInstance()
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
                val imageGetter = context?.resources?.let { ImageGetter(it, holder.htmlTextViewDown) }

                val styledText =
                    HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter,null)

//                replaceQuoteSpans(styledText as Spannable)
                ImageClick(styledText as Spannable,position)
                // setting the text after formatting html and downloading and setting images
                holder.htmlTextViewDown.text = styledText
                holder.htmlTextViewDown.movementMethod = LinkMovementMethod.getInstance()
            }else{

            holder.htmlTextViewDown.setHtml(list.get(position).message_parsed)
            }
        }

        if (list.get(position).User != null) {
            holder.userName.setText(list.get(position).User.username)
        }
        val tags: String = list.get(position).message_parsed

        isReactedTo(holder, position)
        var str1 = Jsoup.parse(list.get(position).message_parsed).text()
        holder.position.setText("#${list.get(position).position + 1}")
        var date = Date((list.get(position).post_date as Long) * 1000)
        var simple = SimpleDateFormat("dd yyyy")
        holder.postDate.setText(
            "${DateFormatSymbols().getShortMonths()[date.month]} ${
                simple.format(date)
            }"
        )

        if (list.get(position).User.avatar_urls.o == null) {
            holder.profileImage_tv.visibility = View.VISIBLE
            holder.ProfileImage.visibility = View.GONE
            holder.profileImage_tv.gravity = Gravity.CENTER
            holder.profileImage_tv.setText(list.get(position).User.username.get(0).toString())
        } else {
            Picasso.get().load(list.get(position).User.avatar_urls.o).placeholder(R.drawable.person)
                .into(holder.ProfileImage)
        }
        if (list.get(position).is_staff || list.get(position).User.user_title.toString()
                .startsWith("Founder")
        ) {
            holder.userTitle.setText(list.get(position).User.user_title)
        } else {
            holder.member.visibility = View.GONE
            holder.admin.visibility = View.GONE
            holder.founder.visibility = View.GONE
            holder.userTitle.setText(list.get(position).User.user_title)
        }
        if (list.get(position).user_id != MyDataClass.myUserId) {
            holder.deletePost.visibility = View.GONE
        } else {
            if (position == 0) {
                holder.deletePost.visibility = View.GONE
            } else {
                holder.deletePost.visibility = View.VISIBLE
            }
        }
        if (list.get(position).User.user_title.equals("Founder")) {
            holder.founder.visibility = View.GONE
        }
        holder.deletePost.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                context?.let {
                    AlertDialog.Builder(it).setTitle("Delete")
                        .setMessage("Are you sure want to delete this post")
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                api.deleteSpecificPost(
                                    MyDataClass.api_key,
                                    MyDataClass.myUserId,
                                    list.get(position).post_id
                                )
                                    .enqueue(object : Callback<Map<String, Boolean>> {
                                        override fun onResponse(
                                            call: Call<Map<String, Boolean>>,
                                            response: Response<Map<String, Boolean>>,
                                        ) {
                                            if (response.isSuccessful) {
                                                kFunction2(list.get(position).thread_id)
                                                list.clear()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<Map<String, Boolean>>,
                                            t: Throwable,
                                        ) {

                                        }
                                    })
                            }
                        }).setNegativeButton("No", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                p0?.dismiss()
                            }
                        }).show()
                }
            }
        })
        holder.replyBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var message = ""
                if (string.contains("<blockquote class=")) {
                    string = string.substring(string.indexOf("\">") + 2)
                    string = string.substring(0, string.indexOf("</blockquote>"))
                    var k: String = string1.replace(string, "")
                    var d: Document = Jsoup.parse(k)
                    message = d.text().replace(upMessage, "")
                } else {
                    message = Jsoup.parse(list.get(position).message_parsed).text()
                }
                var transaction = MyDataClass.getTransaction()
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                transaction.replace(
                    R.id.home_fragment_containerViewForShowDetails,
                    PostComments(
                        message,
                        list.get(position).User.username,
                        list.get(position).thread_id,
                        list.get(position).post_id,
                        list.get(position).user_id
                    )
                )
                transaction.addToBackStack(null).commit()
            }
        })
        holder.likeBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                LikePost(holder, position)
            }
        })
        holder.likeBtn.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                MyDataClass.reactionDialog(
                    list.get(position).post_id,
                    holder,
                    list.get(position).reaction_score
                )
                return true
            }
        })
        holder.ProfileImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (list.get(position).User.can_view_profile) {
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    var transaction = MyDataClass.getTransaction()
                    transaction.replace(
                        R.id.home_fragment_containerViewForShowDetails,
                        UserProfile(list, position)
                    )
                    transaction.addToBackStack(null).commit()
                } else {
                    Toast.makeText(
                        context,
                        "You don't have a permission to view this user's profile",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }

        })
        holder.profileImage_tv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (list.get(position).User.can_view_profile) {
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    var transaction = MyDataClass.getTransaction()
                    transaction.replace(
                        R.id.home_fragment_containerViewForShowDetails,
                        UserProfile(list, position)
                    )
                    transaction.addToBackStack(null).commit()
                } else {
                    Toast.makeText(
                        context,
                        "You don't have a permission to view this user's profile",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }
    fun LikePost(holder: ShowPostsOfThreadsViewHolder, position: Int) {
        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getReaponseOfReact(
            MyDataClass.api_key,
            MyDataClass.myUserId,
            list.get(position).post_id,
            1
        ).enqueue(object : Callback<Map<String, Any>> {
            @SuppressLint("ResourceAsColor")
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>,
            ) {
                if (response.body()?.get("action").toString().equals("insert")) {
                    holder.likeBtn.setTextColor(Color.parseColor("#0B18CC"))
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setText("Like")
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_like,
                        0,
                        0,
                        0
                    )
                    if (list.get(position).reaction_score > 0) {
                        if ((list.get(position).reaction_score - 1) != 0) {
                            holder.likeCounts.visibility = View.VISIBLE
                            holder.likeCounts.setText("You,${list.get(position).reaction_score} other")
                        } else {
                            holder.likeCounts.visibility = View.VISIBLE
                            holder.likeCounts.setText("You,${list.get(position).reaction_score} other")
                        }
                    } else {
                        holder.likeCounts.visibility = View.VISIBLE
                        holder.likeCounts.setText("You")
                    }
                } else {
                    holder.likeBtn.setTextColor(Color.parseColor("#FF000000"))
                    holder.likeBtn.setTypeface(null, Typeface.NORMAL)
                    holder.likeBtn.setText("Like")
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_like,
                        0,
                        0,
                        0
                    )
                    if (list.get(position).reaction_score > 0) {
                        if ((list.get(position).reaction_score - 1) != 0) {
                            holder.likeCounts.visibility = View.VISIBLE
                            holder.likeCounts.setText("${list.get(position).reaction_score}")
                        } else {
                            holder.likeCounts.visibility = View.VISIBLE
                            holder.likeCounts.setText(
                                "${
                                    list.get(position).reaction_score
                                }"
                            )
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

    class ShowPostsOfThreadsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ProfileImage: CircleImageView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_UserProfileImage)
        var profileImage_tv: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_UserProfileImage_tv)

        //        var editTextForBlock:TextView=itemView.findViewById(R.id.editextFrorBloack)
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

        //        var messageDown: TextView =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_message_tvDown)
        var webViewDown: WebView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_webviewForTable)
        var userTitle: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_usertitle_Tv)
        var replyBtn: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_reply_btn)
        var fragment: FragmentContainerView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_Fragment)
        var likeBtn: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_like_btn)
        var likeCounts: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_like_counts)
        var deletePost: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_deleteBtn)
        var htmlTextViewDown: HtmlTextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_htmlTextviewDown)
    }

    private fun isReactedTo(holder: ShowPostsOfThreadsViewHolder, position: Int) {
        if (list.get(position).is_reacted_to) {
            if (list.get(position).reaction_score > 0) {
                if ((list.get(position).reaction_score - 1) != 0) {
                    holder.likeCounts.visibility = View.VISIBLE
                    holder.likeCounts.setText("You,${list.get(position).reaction_score - 1} other")
                } else {
                    holder.likeCounts.visibility = View.VISIBLE
                    holder.likeCounts.setText("You")
                }
            } else {
                holder.likeCounts.visibility = View.GONE
            }
            when (list.get(position).visitor_reaction_id) {
                1 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#0B18CC"))
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setText("Like")
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_like,
                        0,
                        0,
                        0
                    )
                }
                2 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#BF0404"))
                    holder.likeBtn.setText("Love")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_love_icon,
                        0,
                        0,
                        0
                    )
                }
                3 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("haha")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_haha_icon,
                        0,
                        0,
                        0
                    )
                }
                4 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("Wow")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_wow_icon,
                        0,
                        0,
                        0
                    )
                }
                5 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("Sad")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_sad_icon,
                        0,
                        0,
                        0
                    )
                }
                6 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#FB2707"))
                    holder.likeBtn.setText("Angery")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_angry_icon,
                        0,
                        0,
                        0
                    )
                }
            }

        } else {
            holder.likeBtn.setTextColor(Color.parseColor("#FF000000"))
            holder.likeBtn.setTypeface(null, Typeface.NORMAL)
            holder.likeBtn.setText("Like")
            holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_like,
                0,
                0,
                0
            )
            if (list.get(position).reaction_score > 0) {
                holder.likeCounts.visibility = View.VISIBLE
                holder.likeCounts.setText("${list.get(position).reaction_score}")
            } else {
                holder.likeCounts.visibility = View.GONE
            }
        }

    }
    // Function to parse image tags and enable click events
    fun ImageClick(html: Spannable,position: Int) {
        for (span in html.getSpans(0, html.length, ImageSpan::class.java)) {
            val flags = html.getSpanFlags(span)
            val start = html.getSpanStart(span)
            val end = html.getSpanEnd(span)
            html.setSpan(object : URLSpan(span.source) {
                override fun onClick(v: View) {
                    Log.d("TAG", "onClick: url is ${span.source}")
                    var intent=Intent(context,ShowGridImageView::class.java)
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