package cl.controlfinanciero.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private val homeUrl = "https://controlfinanciero.cl/"
    private val handler = Handler(Looper.getMainLooper())
    private var loginForced = false
    private var loginAttempts = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        webView.setBackgroundColor(Color.WHITE)
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setContentView(FrameLayout(this).apply {
            addView(webView)
        })

        configureWebView()
        if (savedInstanceState == null) webView.loadUrl(homeUrl) else webView.restoreState(savedInstanceState)
    }

    private fun configureWebView() {
        val s = webView.settings
        s.javaScriptEnabled = true
        s.domStorageEnabled = true
        s.databaseEnabled = true
        s.loadsImagesAutomatically = true
        s.javaScriptCanOpenWindowsAutomatically = true
        s.setSupportMultipleWindows(false)
        s.cacheMode = WebSettings.LOAD_DEFAULT
        s.userAgentString = s.userAgentString + " CCFAndroid/1.1"

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                forceProductionLoginView()
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = handleUrl(request.url.toString())
            @Deprecated("Deprecated in Android API 24")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = handleUrl(url)
        }
    }

    /**
     * The production site intentionally opens at the public landing page.
     * For the Android app, the required first screen is the existing CCF
     * authentication gate. We do not recreate or duplicate authentication;
     * we simply activate the site's existing "Iniciar sesión" action.
     */
    private fun forceProductionLoginView() {
        loginForced = false
        loginAttempts = 0
        handler.removeCallbacksAndMessages(null)
        tryOpenExistingLogin()
    }

    private fun tryOpenExistingLogin() {
        if (loginForced || isFinishing || isDestroyed) return
        loginAttempts++

        webView.evaluateJavascript(
            """
            (function(){
              if (document.getElementById('ccf-auth-gate')) return 'auth';
              var login = document.querySelector('[data-b230-open=\"login\"]');
              if (login) { login.click(); return 'clicked'; }
              return 'waiting';
            })();
            """.trimIndent()
        ) { result ->
            if (result == "\"auth\"" || result == "\"clicked\"") {
                loginForced = true
                return@evaluateJavascript
            }
            if (loginAttempts < 60) {
                handler.postDelayed({ tryOpenExistingLogin() }, 250L)
            }
        }
    }

    private fun handleUrl(url: String): Boolean {
        val uri = Uri.parse(url)
        val scheme = uri.scheme?.lowercase() ?: return false
        if (scheme == "http" || scheme == "https") {
            return !url.startsWith("https://controlfinanciero.cl", ignoreCase = true)
        }
        return try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            true
        } catch (_: ActivityNotFoundException) {
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Android API 33")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        webView.stopLoading()
        webView.destroy()
        super.onDestroy()
    }
}
