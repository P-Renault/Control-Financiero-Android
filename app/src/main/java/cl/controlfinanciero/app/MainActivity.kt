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
    private var bootAttempts = 0
    private var pageReady = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        webView.setBackgroundColor(Color.rgb(5, 11, 20))
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        setContentView(FrameLayout(this).apply { addView(webView) })
        configureWebView()

        if (savedInstanceState == null) {
            webView.loadUrl(homeUrl)
        } else {
            webView.restoreState(savedInstanceState)
            handler.postDelayed({ forceLoginBoot() }, 300L)
        }
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
        s.useWideViewPort = true
        s.loadWithOverviewMode = false
        s.setSupportZoom(false)
        s.builtInZoomControls = false
        s.displayZoomControls = false
        s.textZoom = 100
        s.userAgentString = s.userAgentString + " CCFAndroid/2.1"

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                pageReady = true
                forceLoginBoot()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = handleUrl(request.url.toString())

            @Deprecated("Deprecated in Android API 24")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = handleUrl(url)
        }
    }

    /**
     * Production login is the real CCF authentication portal.
     * We do NOT fake a login and we do NOT navigate to a different page.
     * We load the production page only as the host, immediately hide the
     * landing/app and explicitly boot CCF-AUTH-BOOT-FINAL.js.
     */
    private fun forceLoginBoot() {
        if (!pageReady || isFinishing || isDestroyed) return
        bootAttempts = 0
        handler.removeCallbacksAndMessages(null)
        injectAuthBoot()
    }

    private fun injectAuthBoot() {
        if (isFinishing || isDestroyed) return
        bootAttempts++

        webView.evaluateJavascript(
            """
            (function(){
              'use strict';

              function ensureLock(){
                if(!document.getElementById('ccf-android-startup-style')){
                  var st=document.createElement('style');
                  st.id='ccf-android-startup-style';
                  st.textContent=`
                    html,body{background:#050b14 !important;}
                    body.ccf-android-startup-lock #app,
                    body.ccf-android-startup-lock header,
                    body.ccf-android-startup-lock main,
                    body.ccf-android-startup-lock nav,
                    body.ccf-android-startup-lock .landing,
                    body.ccf-android-startup-lock [class*="landing"]{
                      visibility:hidden !important;
                    }
                    #ccf-android-startup-loading{
                      position:fixed;inset:0;z-index:2147483645;
                      display:flex;align-items:center;justify-content:center;
                      background:#050b14;color:#fff;font:600 15px system-ui;
                    }
                    #app.ccf-android-compact{zoom:.90 !important;}
                    @supports not (zoom:.9){
                      #app.ccf-android-compact{
                        transform:scale(.90);transform-origin:top left;width:111.111%;
                      }
                    }
                  `;
                  document.head.appendChild(st);
                }
                document.body.classList.add('ccf-android-startup-lock');
                if(!document.getElementById('ccf-android-startup-loading')){
                  var l=document.createElement('div');
                  l.id='ccf-android-startup-loading';
                  l.textContent='Cargando acceso seguro…';
                  document.body.appendChild(l);
                }
              }

              function inject(){
                ensureLock();

                // The production page may already have executed an older
                // auth boot and left its one-time guard set. Reset only this
                // boot guard so the real production login portal is created.
                try { window.__CCF_B23016_AUTH_BOOT__ = false; } catch(e) {}

                var old=document.querySelectorAll('script[src*="CCF-AUTH-BOOT-FINAL.js"]');
                for(var i=0;i<old.length;i++){
                  try{old[i].remove();}catch(e){}
                }

                var s=document.createElement('script');
                s.src='/CCF-AUTH-BOOT-FINAL.js?android=2.1&ts='+Date.now();
                s.async=false;
                s.onload=function(){check();};
                s.onerror=function(){check();};
                document.head.appendChild(s);
                check();
              }

              function check(){
                var gate=document.getElementById('ccf-auth-gate');
                var loading=document.getElementById('ccf-android-startup-loading');
                if(gate){
                  if(loading) loading.remove();
                  document.body.classList.remove('ccf-android-startup-lock');
                  return 'login';
                }
                return 'waiting';
              }

              inject();
              return check();
            })();
            """.trimIndent(),
            null
        )

        handler.postDelayed({ verifyLoginPortal() }, 350L)
    }

    private fun verifyLoginPortal() {
        if (isFinishing || isDestroyed) return

        webView.evaluateJavascript(
            """
            (function(){
              var gate=document.getElementById('ccf-auth-gate');
              var loading=document.getElementById('ccf-android-startup-loading');
              var app=document.getElementById('app');
              if(gate){
                if(loading) loading.remove();
                document.body.classList.remove('ccf-android-startup-lock');
                if(app) app.classList.add('hidden');
                return 'login';
              }
              return 'waiting';
            })();
            """.trimIndent(),
            null
        )

        if (bootAttempts < 40) {
            bootAttempts++
            handler.postDelayed({ verifyLoginPortal() }, 500L)
        }
    }

    private fun applyCompactAfterLogin() {
        if (isFinishing || isDestroyed) return
        webView.evaluateJavascript(
            """
            (function(){
              var gate=document.getElementById('ccf-auth-gate');
              var app=document.getElementById('app');
              var loading=document.getElementById('ccf-android-startup-loading');
              if(!gate){
                if(loading) loading.remove();
                document.body.classList.remove('ccf-android-startup-lock');
                if(app) app.classList.add('ccf-android-compact');
              }
              return !!gate;
            })();
            """.trimIndent(),
            null
        )
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
