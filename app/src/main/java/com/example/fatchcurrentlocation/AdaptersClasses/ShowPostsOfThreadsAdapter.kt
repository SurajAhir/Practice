package com.example.fatchcurrentlocation.AdaptersClasses

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.*
import com.example.fatchcurrentlocation.DataClasses.MyDataClass
import com.example.fatchcurrentlocation.DataClasses.Pagination
import com.example.fatchcurrentlocation.DataClasses.Posts
import com.example.fatchcurrentlocation.Fragments.PostComments
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.ReactionsConfigBuilder
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
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
    val supportFragmentManager: FragmentManager,
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
        holder.userName.setText(list.get(position).User.username)
        var string: String = list.get(position).message_parsed
        var string1: String = list.get(position).message_parsed
        var upMessage: String = ""
        if (string.contains("<blockquote class=")) {
            if (!string.startsWith("<blockquote class=\"xfBb-quote")) {
                var k = string.split("<blockquote class=\"xfBb-quote")
                holder.messageUp.visibility = View.VISIBLE
                upMessage = Jsoup.parse(k[0]).text()
                holder.messageUp.setText(upMessage)
            }
            holder.blockQuoteLayout.visibility = View.VISIBLE
            string = string.substring(string.indexOf("\">") + 2)
            string = string.substring(0, string.indexOf("</blockquote>"))
            holder.blockQuoteMessage.setText(Jsoup.parse(string).text())
            var k: String = string1.replace(string, "")
            var d: Document = Jsoup.parse(k)
            holder.messageDown.setText(d.text().replace(upMessage, ""))
        } else {
            holder.blockQuoteLayout.visibility = View.GONE
            holder.messageUp.visibility = View.GONE
            holder.messageDown.setText(Jsoup.parse(list.get(position).message_parsed).text())
        }
        var str1 = Jsoup.parse(list.get(position).message_parsed).text()
        holder.position.setText("#${list.get(position).position + 1}")
        var date = Date((list.get(position).post_date as Long) * 1000)
        var simple = SimpleDateFormat("dd yyyy")
        holder.postDate.setText("${DateFormatSymbols().getShortMonths()[date.month]} ${
            simple.format(date)
        }")
        Picasso.get().load(list?.get(position)?.User?.avatar_urls?.o).placeholder(R.drawable.person)
            .into(holder.ProfileImage)
        if (list.get(position).User.user_title.toString()
                .startsWith("Pro") || list.get(position).User.user_title.toString()
                .startsWith("Founder")
        ) {
            holder.userTitle.setText(list.get(position).User.user_title)
        } else {
            holder.member.visibility = View.GONE
            holder.admin.visibility = View.GONE
            holder.founder.visibility = View.GONE
            holder.userTitle.setText(list.get(position).User.user_title)
        }
        if (list.get(position).is_reacted_to) {
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

        }
        holder.replyBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var transaction = MyDataClass.getTransaction()
                MyDataClass.homeNestedScrollView.visibility = View.GONE
                MyDataClass.homeFragmentContainerView.visibility = View.VISIBLE
                transaction.replace(R.id.home_fragment_containerViewForShowDetails,
                    PostComments(holder.messageDown.text.toString(),
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
                MyDataClass.reactionDialog(list.get(position).post_id, holder)
                return true
            }
        })
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    fun LikePost(holder: ShowPostsOfThreadsViewHolder, position: Int) {
        Log.d("TAG", "threadid ${list.get(position).thread_id}")
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
                    Log.d("TAG", response.body()?.get("action").toString())
                    holder.likeBtn.setTextColor(Color.parseColor("#0B18CC"))
                    holder.likeBtn.setTypeface(null, Typeface.BOLD)
                    holder.likeBtn.setText("Like")
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_like,
                        0,
                        0,
                        0)
//                            holder.likeBtn.compoundDrawableTintMode=null
                } else {
                    Log.d("TAG", response.body()?.get("action").toString())
                    holder.likeBtn.setTextColor(Color.parseColor("#FF000000"))
                    holder.likeBtn.setTypeface(null, Typeface.NORMAL)
                    holder.likeBtn.setText("Like")
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,
                        0,
                        0,
                        0)

                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {

            }
        })
    }

    class ShowPostsOfThreadsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ProfileImage: CircleImageView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_UserProfileImage)
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
        var messageDown: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_message_tvDown)
        var messageUp: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_message_tvUp)
        var blockQuoteLayout: LinearLayout =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_blockQuote_Layout)
        var blockQuoteMessage: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_blockQuote_message_tv)
        var userTitle: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_usertitle_Tv)
        var replyBtn: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_reply_btn)
        var fragment: FragmentContainerView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_Fragment)
        var likeBtn: TextView =
            itemView.findViewById(R.id.show_posts_of_threads_custom_layout_like_btn)
    }


}