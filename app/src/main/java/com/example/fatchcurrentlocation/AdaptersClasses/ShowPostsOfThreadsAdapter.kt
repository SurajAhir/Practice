package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
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
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Pagination
import com.example.fatchcurrentlocation.DataClasses.Posts
import com.example.fatchcurrentlocation.Fragments.PostComments
import com.example.fatchcurrentlocation.Fragments.UserProfile
import com.example.fatchcurrentlocation.HitApi
import com.example.fatchcurrentlocation.RetrofitManager
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
import retrofit2.Retrofit
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


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
//        holder.htmlTextView.blockQuoteBackgroundColor=Color.parseColor("#F6F1F1")
//        holder.htmlTextView.blockQuoteStripWidth=0f
////        holder.htmlTextView.setHtml(list.get(position).message_parsed)
        holder.htmlTextViewDown.setOnClickATagListener(object : OnClickATagListener {
            override fun onClick(widget: View?, spannedText: String?, href: String?): Boolean {
                return false
            }
        })
//        holder.htmlTextViewForBlockquote.setOnClickATagListener(object : OnClickATagListener {
//            override fun onClick(widget: View?, spannedText: String?, href: String?): Boolean {
//                return false
//            }
//        })
//        holder.htmlTextViewUp.setOnClickATagListener(object : OnClickATagListener {
//            override fun onClick(widget: View?, spannedText: String?, href: String?): Boolean {
//                return false
//            }
//        })

        holder.webViewDown.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
//        holder.htmlTextViewForBlockquote.setLayerType(HtmlTextView.LAYER_TYPE_NONE, null)
        holder.htmlTextViewDown.setLayerType(HtmlTextView.LAYER_TYPE_NONE, null)

//        holder.htmlTextViewUp.setLayerType(HtmlTextView.LAYER_TYPE_NONE, null)
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
        holder.htmlTextViewDown.blockQuoteStripWidth=0f
        holder.htmlTextViewDown.blockQuoteGap=20f
        holder.htmlTextViewDown.blockQuoteBackgroundColor=Color.parseColor("#E7E1E1" +
                "")
        holder.htmlTextViewDown.setHtml(list.get(position).message_parsed)
//        if (string.contains("<blockquote class=")) {
//            if (!string.startsWith("<blockquote class=\"xfBb-quote")) {
//                var k = string.split("<blockquote class=\"xfBb-quote")
//                holder.htmlTextViewUp.visibility = View.VISIBLE
////                holder.webViewUp.loadDataWithBaseURL(null, k[0], "text/html", "utf-8", null)
//                holder.htmlTextViewUp.setHtml(k[0])
////                upMessage = Jsoup.parse(k[0]).text()
//
////                holder.messageUp.setText(upMessage)
//                upMessage = k[0]
//            }
//
//
//            holder.layoutForBlockquote.visibility = View.VISIBLE
//            string = string.substring(string.indexOf("\">") + 2)
//            string = string.substring(0, string.indexOf("</blockquote>"))
//            holder.htmlTextViewForBlockquote.setHtml(string)
//            var k: String = string1.replace(string, "")
//            var d: Document = Jsoup.parse(k)
//            holder.htmlTextViewDown.setHtml(k.replace(upMessage, ""))
//        } else {
//            holder.layoutForBlockquote.visibility = View.GONE
//            holder.htmlTextViewUp.visibility = View.GONE
//            holder.htmlTextViewDown.setHtml(list.get(position).message_parsed)
//        }

      if(list.get(position).User!=null){
          holder.userName.setText(list.get(position).User.username)
      }
        val tags: String = list.get(position).message_parsed
        if (list.get(position).attach_count > 0) {
//            var document: Document = Jsoup.parse(tags)
//            var elements: Elements = document.select("img")

//            for (i in elements) {
//                var src = i.attr("src")
//                var src1 = src.replace("https://www.technofino.in/community/attachments/", "")
//                var alt = i.attr("alt")
//                var image = "https://www.technofino.in/community/attachments/" + alt + "." + src1
//                list.add(image)
//            }
//            for (i in list.get(position).Attachments) {
//                list1.add(i.thumbnail_url)
////            }
//            holder.recyclerView.visibility = View.VISIBLE
//            holder.recyclerView.adapter = context?.let { ShowAttachmentImageAdapter(list1, it) }
//            holder.recyclerView.layoutManager = LinearLayoutManager(context)
            callGetAttachmentPicApi(position, holder)
        } else {
            holder.recyclerView.visibility = View.GONE
        }
//
        isReactedTo(holder, position)
        var str1 = Jsoup.parse(list.get(position).message_parsed).text()
        holder.position.setText("#${list.get(position).position + 1}")
        var date = Date((list.get(position).post_date as Long) * 1000)
        var simple = SimpleDateFormat("dd yyyy")
        holder.postDate.setText("${DateFormatSymbols().getShortMonths()[date.month]} ${
            simple.format(date)
        }")

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
        holder.deletePost.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var retrofit = RetrofitManager.getRetrofit1()
                var api = retrofit.create(HitApi::class.java)
                context?.let {
                    AlertDialog.Builder(it).setTitle("Delete")
                        .setMessage("Are you sure want to delete this post")
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                api.deleteSpecificPost(MyDataClass.api_key,
                                    MyDataClass.myUserId,
                                    list.get(position).post_id)
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
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    PostComments(message,
                        list.get(position).User.username,
                        list.get(position).thread_id,
                        list.get(position).post_id,
                        list.get(position).user_id))
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
                MyDataClass.reactionDialog(list.get(position).post_id,
                    holder,
                    list.get(position).reaction_score)
                return true
            }
        })
        holder.ProfileImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (list.get(position).User.can_view_profile) {
                    MyDataClass.homeNestedScrollView.visibility = View.GONE
                    MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                    var transaction = MyDataClass.getTransaction()
                    transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                        UserProfile(list, position))
                    transaction.addToBackStack(null).commit()
                } else {
                    Toast.makeText(context,
                        "You don't have a permission to view this user's profile",
                        Toast.LENGTH_LONG).show()
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
                    transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                        UserProfile(list, position))
                    transaction.addToBackStack(null).commit()
                } else {
                    Toast.makeText(context,
                        "You don't have a permission to view this user's profile",
                        Toast.LENGTH_LONG).show()
                    return
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    private fun callGetAttachmentPicApi(position: Int, holder: ShowPostsOfThreadsViewHolder) {
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
                            MyDataClass.JumpedToImageList.clear()
                            MyDataClass.JumpedToImageList.addAll(list)
                            var byteArray = response.body()?.bytes()
                            var base64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
                            var decodedString = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                            var bitmap = BitmapFactory.decodeByteArray(decodedString,
                                0,
                                decodedString.size)
                            listOfAttachmentPics.add(bitmap)
                            holder.recyclerView.visibility = View.VISIBLE
                            holder.recyclerView.adapter = context?.let {
                                ShowAttachmentImageAdapter(listOfAttachmentPics,
                                    it,
                                    position)
                            }
                            holder.recyclerView.layoutManager = LinearLayoutManager(context)
//                                Log.d("TAG", bitmap.toString())
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
    }

    fun LikePost(holder: ShowPostsOfThreadsViewHolder, position: Int) {
        var retrofit: Retrofit = RetrofitManager.getRetrofit1()
        var api: HitApi = retrofit.create(HitApi::class.java)
        api.getReaponseOfReact("4xEmIhbiwmsneaJZ8gQ41pkfulOe0xI4",
            MyDataClass.myUserId,
            list.get(position).post_id,
            1).enqueue(object : Callback<Map<String, Any>> {
            @SuppressLint("ResourceAsColor")
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>,
            ) {
                if (response.body()?.get("action").toString().equals("insert")) {
                    holder.likeBtn.setTextColor(Color.parseColor("#0B18CC"))
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setText("Like")
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_like,
                        0,
                        0,
                        0)
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
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,
                        0,
                        0,
                        0)
                    if (list.get(position).reaction_score > 0) {
                        if ((list.get(position).reaction_score - 1) != 0) {
                            holder.likeCounts.visibility = View.VISIBLE
                            holder.likeCounts.setText("${list.get(position).reaction_score}")
                        } else {
                            holder.likeCounts.visibility = View.VISIBLE
                            holder.likeCounts.setText("${
                                list.get(position).reaction_score
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

    internal class ClickableTableSpanImpl(val webView: WebView) : ClickableTableSpan() {


        override fun newInstance(): ClickableTableSpan {
            return ClickableTableSpanImpl(webView)
        }

        override fun onClick(widget: View) {
            val tableHtml = getTableHtml()
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

        //        var webViewBlockquote:WebView=itemView.findViewById(R.id.show_posts_of_threads_custom_layout_webviewForBlockquote)
//        var webViewUp:WebView=itemView.findViewById(R.id.show_posts_of_threads_custom_layout_webviewUp)
//        var layoutForBlockquote: LinearLayout =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_LayoutForBlockquote)

        //        var messageUp: TextView =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_message_tvUp)
//        var blockQuoteLayout: LinearLayout =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_blockQuote_Layout)
//        var blockQuoteMessage: TextView =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_blockQuote_message_tv)
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
        var recyclerView: RecyclerView =
            itemView.findViewById(R.id.show_posts_of_threads_recyclerView)
        var deletePost: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_deleteBtn)
//        var htmlTextViewForBlockquote: HtmlTextView =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_htmlTextviewForBlockquote)
        var htmlTextViewDown: HtmlTextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_htmlTextviewDown)
//        var htmlTextViewUp: HtmlTextView =
//            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_htmlTextviewUp)
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
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_like,
                        0,
                        0,
                        0)
                }
                2 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#BF0404"))
                    holder.likeBtn.setText("Love")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_love_icon,
                        0,
                        0,
                        0)
                }
                3 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("haha")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_haha_icon,
                        0,
                        0,
                        0)
                }
                4 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("Wow")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wow_icon,
                        0,
                        0,
                        0)
                }
                5 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#FFC107"))
                    holder.likeBtn.setText("Sad")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sad_icon,
                        0,
                        0,
                        0)
                }
                6 -> {
                    holder.likeBtn.setTextColor(Color.parseColor("#FB2707"))
                    holder.likeBtn.setText("Angery")
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_angry_icon,
                        0,
                        0,
                        0)
                }
            }

        } else {
            holder.likeBtn.setTextColor(Color.parseColor("#FF000000"))
            holder.likeBtn.setTypeface(null, Typeface.NORMAL)
            holder.likeBtn.setText("Like")
            holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,
                0,
                0,
                0)
            if (list.get(position).reaction_score > 0) {
                holder.likeCounts.visibility = View.VISIBLE
                holder.likeCounts.setText("${list.get(position).reaction_score}")
            } else {
                holder.likeCounts.visibility = View.GONE
            }
        }

    }

}