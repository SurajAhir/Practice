package com.example.fatchcurrentlocation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.fatchcurrentlocation.databinding.ActivityGoogleSignUpBinding


class GoogleSignUp : AppCompatActivity() {
    lateinit var binding: ActivityGoogleSignUpBinding
    var googleUrl =
        "https://accounts.google.com/o/oauth2/auth/oauthchooseaccount?access_type=online&type=web_server&client_id=687356292997-66rjhids03rcu73furtq00p7t9sujgfb.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Fwww.technofino.in%2Fcommunity%2Fconnected_account.php&response_type=code&scope=profile%20email&state=94a327a52c43adaca8c6b1764ad45060&flowName=GeneralOAuthFlow"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.googleSignUpWebView.loadUrl(googleUrl)
        binding.googleSignUpWebView.webViewClient=GoogleWebViewClient()
        val MyUA = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
//        settings.userAgentString="Chrome/56.0.0 Mobile"
        var settings=binding.googleSignUpWebView.settings
        settings.userAgentString=MyUA
        settings.allowContentAccess = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.javaScriptEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.setSupportZoom(true)
        settings.safeBrowsingEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccessFromFileURLs = true

    }


    class GoogleWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url != null) {
                view?.loadUrl(url)
            }
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("TAGA",url.toString())
        }
    }
}