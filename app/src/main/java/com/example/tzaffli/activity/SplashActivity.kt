package com.example.tzaffli.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.example.tzaffli.R
import com.example.tzaffli.pref.AppPrefTz
import com.example.tzaffli.webView.AffliChromeWebClient
import com.example.tzaffli.webView.AffliWebClient
import com.facebook.applinks.AppLinkData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    var webView: WebView? = null

    private var valueCallback: ValueCallback<Array<Uri>>? = null
    private val register = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode != RESULT_CANCELED) {
            valueCallback!!.onReceiveValue(
                arrayOf(
                    Uri.parse(
                        if (result.data != null) result.data!!
                            .dataString else null
                    )
                )
            )
        } else {
            valueCallback!!.onReceiveValue(null)
        }
        valueCallback = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Проверяем получал ли пользоваль раньше ссылку
        AppPrefTz.getStartUrl().subscribe{ startUrl ->
            if (startUrl.isEmpty()){
                // Инициализируем Appsflyer
                appsflyerIntegration()

                // Получение DeepLink из Facebook
                getDeepLink()
            } else {
                AppPrefTz.getLastUrl().subscribe { lastUrl ->
                    startWebView(lastUrl.ifEmpty { startUrl })
                }
            }
        }

    }

    private fun appsflyerIntegration() {
        val appsflyer = AppsFlyerLib.getInstance()
        appsflyer.setMinTimeBetweenSessions(0)

        val conversionListener: AppsFlyerConversionListener =
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                    // если пришел атьрибут имя компании
                    if (conversionData["campaign"] != null) {
                        // Записываем в Shared Pref
                        AppPrefTz.saveCampaign(
                            conversionData["campaign"].toString().lowercase(
                                Locale.ROOT
                            )
                        )
                    }
                }

                override fun onConversionDataFail(errorMessage: String) {}

                override fun onAppOpenAttribution(attributionData: Map<String, String>) {}

                override fun onAttributionFailure(errorMessage: String) {}
            }

        appsflyer.init("Y2NTHC2ZJoU5gZgSnRgUV5", conversionListener, this)
        appsflyer.start(this)
    }

    private fun getDeepLink(){
        // Ждем 10 сек и образаемся в Facebook
        Completable.timer(10, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe{
                val listener = AppLinkData.CompletionHandler {
                    val fbDeepLink = it?.targetUri
                    // Если DeepLink существует
                    if (fbDeepLink != null){
                        val deepLinkStr = fbDeepLink.toString().lowercase(
                            Locale.ROOT
                        )
                        // убираем fb://
                        generateLink(deepLinkStr.substring(deepLinkStr.indexOf("://")+3))
                    } else {
                        // Обращаемся за именем компании
                        AppPrefTz.getCampaign().subscribe{ compaign ->
                            // Если имя компании есть
                            if (compaign.isNotEmpty()){
                                generateLink(compaign)
                            } else {
                                generateLink("")
                            }

                        }
                    }
                }
                AppLinkData.fetchDeferredAppLinkData(this.applicationContext, listener)
            }
    }

    private fun generateLink(link: String){
        //Настраиваем конфиг FirebaseRemote
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        //Ставим слушатель на получение ссылки из FirebaseRemote
        remoteConfig.fetchAndActivate().addOnSuccessListener {
            //Здесь мы получили ссылку по уникальному тегу
            val url: String
            val nameRef: String
            // Если имя компании и диплинк не пришли
            if (link.isEmpty()){
                AppPrefTz.saveStatus("organic")
                url = ""
                nameRef = "organic_ref"
            } else {
                AppPrefTz.saveStatus("non-organic")
                val list = link.split("_")
                url = "?sub1=${list[0]}&sub1=${list[0]}&sub2=${list[1]}&sub3=${list[2]}&sub4=${list[3]}" +
                        "&sub5=${list[4]}&sub6=${list[5]}&"
                nameRef = "non_organic_ref"
            }
            var urlFirebase = remoteConfig.getString(nameRef)
            if (urlFirebase.isEmpty()){
                startActivity()
            } else {
                urlFirebase += url
                AppPrefTz.saveStartUrl(urlFirebase)
                startWebView(urlFirebase)
            }
        }.addOnFailureListener {
            startActivity()
        }
    }

    private fun startActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun startWebView(link: String){
        progressBar.visibility = View.GONE
        webView = WebView(this)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ).apply {
            gravity = Gravity.CENTER
        }
        webView?.layoutParams = params
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView?.webViewClient = AffliWebClient()
        webView?.webChromeClient = AffliChromeWebClient(this, valueCallback, register)
        val mWebSettings = webView?.settings
        mWebSettings?.domStorageEnabled = true
        mWebSettings?.javaScriptEnabled = true
        mWebSettings?.useWideViewPort = true
        mWebSettings?.databaseEnabled = true
        mWebSettings?.javaScriptCanOpenWindowsAutomatically = true
        mWebSettings?.cacheMode = WebSettings.LOAD_DEFAULT
        container.addView(webView)
        webView?.post {
            webView?.loadUrl(link)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView?.restoreState(savedInstanceState)
    }

    override fun onBackPressed() {
        if (webView != null && webView!!.canGoBack()) {
            webView?.goBack()
        } else {
            super.onBackPressed()
        }
    }
}