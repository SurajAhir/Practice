package com.example.fatchcurrentlocation

import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.QuoteSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.example.fatchcurrentlocation.HtmlmageWork.ImageGetter
import com.example.fatchcurrentlocation.HtmlmageWork.QuoteSpanClass
import com.example.fatchcurrentlocation.databinding.ActivityJustPracticeBinding


class JustPractice : AppCompatActivity() {
    lateinit var binding: ActivityJustPracticeBinding
    var htmlString="<img src='https://www.technofino.in/community/data/attachments/0/22-33a7a8e827f96946569726ca7e3dbc7b.jpg'/>We can't really have a forum without allowing members to share their credit card portfolios and talk about their favourite credit card. :cool:<br />\n<br />\n---<br />\n<br />\nI will go first,  here's my collection, from least used to most used.<br />\n<br />\n<div class='xfBb-table'>\n<table style='width: 100%'><tr><td>Slice Pay card</td></tr><tr><td>Amazon Pay ICICI bank card</td></tr><tr><td>Citi Cashback card</td></tr><tr><td>IDFC First Select card</td></tr><tr><td>AMEX Membership Rewards card</td></tr><tr><td>HDFC Diners Club Black card</td></tr><tr><td>Onecard Metal card</td></tr><tr><td>Axis Magnus card</td></tr></table>\n</div><br />\n<img src=\"https://www.technofino.in/community/attachments/22/\" alt=\"cards.jpeg\" />"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityJustPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
binding.displayHtml.setOnClickListener {

        displayHtml(htmlString)

}


    }
    private fun displayHtml(html: String) {

        // Creating object of ImageGetter class you just created
        val imageGetter = ImageGetter(resources, binding.htmlViewer)

        val styledText =
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter,null)

        replaceQuoteSpans(styledText as Spannable)
        ImageClick(styledText as Spannable)
        // setting the text after formatting html and downloading and setting images
        binding.htmlViewer.text = styledText

        // to enable image/link clicking
        binding.htmlViewer.movementMethod = LinkMovementMethod.getInstance()
    }
    private fun replaceQuoteSpans(spannable: Spannable)
    {
        val quoteSpans: Array<QuoteSpan> =
            spannable.getSpans(0, spannable.length - 1, QuoteSpan::class.java)

        for (quoteSpan in quoteSpans)
        {
            val start: Int = spannable.getSpanStart(quoteSpan)
            val end: Int = spannable.getSpanEnd(quoteSpan)
            val flags: Int = spannable.getSpanFlags(quoteSpan)
            spannable.removeSpan(quoteSpan)
            spannable.setSpan(
                QuoteSpanClass(
                    // background color
                    ContextCompat.getColor(this, R.color.colorPrimary),
                    // strip color
                    ContextCompat.getColor(this, R.color.colorAccent),
                    // strip width
                    10F, 30F
                ),
                start, end, flags
            )
        }
    }
    // Function to parse image tags and enable click events
    fun ImageClick(html: Spannable) {
        for (span in html.getSpans(0, html.length, ImageSpan::class.java)) {
            val flags = html.getSpanFlags(span)
            val start = html.getSpanStart(span)
            val end = html.getSpanEnd(span)
            html.setSpan(object : URLSpan(span.source) {
                override fun onClick(v: View) {
                    Log.d("TAG", "onClick: url is ${span.source}")
                }
            }, start, end, flags)
        }

    }
}