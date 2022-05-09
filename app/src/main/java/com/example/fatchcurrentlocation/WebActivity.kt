package com.example.fatchcurrentlocation

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity


class WebActivity : AppCompatActivity() {
    val EXTRA_TABLE_HTML = "EXTRA_TABLE_HTML"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val tableHtml = intent.getStringExtra(EXTRA_TABLE_HTML)
        val webView = findViewById<WebView>(R.id.web_view)
        webView.loadData(tableHtml!!, "text/html", "UTF-8")
    }
}