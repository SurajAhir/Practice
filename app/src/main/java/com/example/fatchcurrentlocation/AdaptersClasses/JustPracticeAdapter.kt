package com.example.fatchcurrentlocation.AdaptersClasses

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fatchcurrentlocation.DataClasses.Pagination
import com.example.fatchcurrentlocation.DataClasses.Posts
import com.example.fatchcurrentlocation.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.sufficientlysecure.htmltextview.DrawTableLinkSpan
import org.sufficientlysecure.htmltextview.HtmlTextView
import org.sufficientlysecure.htmltextview.OnClickATagListener
import java.util.*

class JustPracticeAdapter(
    val _list: LinkedList<Posts>,
    val _context: Context?,
    val pagination: Pagination,
    val kFunction2: (Int) -> Unit,
) : RecyclerView.Adapter<JustPracticeAdapter.JustPracticeViewHolder>() {


    class JustPracticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView = itemView.findViewById(R.id.just_practice_layout_UserProfileImage)
        var htmlTextViewDown: HtmlTextView = itemView.findViewById(R.id.just_practice_layout_htmlTextviewDown)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JustPracticeViewHolder {
var view=LayoutInflater.from(_context).inflate(R.layout.just_practice_layout,parent,false)
        return JustPracticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: JustPracticeViewHolder, position: Int) {
        Picasso.get().load(_list.get(position).User.avatar_urls.o).placeholder(R.drawable.person).into(holder.profileImage)
        holder.htmlTextViewDown.setOnClickATagListener(object : OnClickATagListener {
            override fun onClick(widget: View?, spannedText: String?, href: String?): Boolean {
                return false
            }
        })
//        holder.htmlTextViewForBlockquote.setLayerType(HtmlTextView.LAYER_TYPE_NONE, null)
        holder.htmlTextViewDown.setLayerType(HtmlTextView.LAYER_TYPE_NONE, null)
        val drawTableLinkSpan = DrawTableLinkSpan()
        drawTableLinkSpan.tableLinkText = "[Show table]"
        holder.htmlTextViewDown.setDrawTableLinkSpan(drawTableLinkSpan)
        val metrics = DisplayMetrics()
        holder.htmlTextViewDown.setListIndentPx(metrics.density * 10)
        holder.htmlTextViewDown.blockQuoteStripWidth=1f
        holder.htmlTextViewDown.blockQuoteGap=20f
        holder.htmlTextViewDown.setLineSpacing(0f,1.6f)
//        holder.htmlTextViewDown.blockQuoteBackgroundColor= Color.parseColor("#E7E1E1" +
//                "")
        holder.htmlTextViewDown.setHtml(_list.get(position).message_parsed)
        var spannable= SpannableString(_list.get(position).message_parsed)
        Log.d("TAGA",spannable.toString())
    }

    override fun getItemCount(): Int {
        return _list.size
    }
}