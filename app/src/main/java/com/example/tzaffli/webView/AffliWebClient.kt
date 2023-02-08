package com.example.tzaffli.webView

import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.tzaffli.pref.AppPrefTz

class AffliWebClient() : WebViewClient() {


    override fun onPageFinished(view: WebView?, url: String) {
        CookieManager.getInstance().setAcceptThirdPartyCookies(view, true)
        AppPrefTz.saveLastUrl(url)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        CookieManager.getInstance().setAcceptThirdPartyCookies(view, true)
    }
}