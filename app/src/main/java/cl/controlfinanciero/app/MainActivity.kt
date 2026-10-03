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
    private var pageReady = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        webView.setBackgroundColor(Color.WHITE)
        webView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setContentView(FrameLayout(this).apply { addView(webView) })
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
        s.useWideViewPort = true
        s.loadWithOverviewMode = false
        s.setSupportZoom(false)
        s.builtInZoomControls = false
        s.displayZoomControls = false
        s.textZoom = 100
        s.userAgentString = s.userAgentString + " CCFAndroid/2.0"
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                pageReady = true
                forceProductionLoginView()
                applyMobileScaleAndLoginGuard()
            }
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = handleUrl(request.url.toString())
            @Deprecated("Deprecated in Android API 24")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = handleUrl(url)
        }
    }

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
              function hasAuth(){ return !!document.getElementById('ccf-auth-gate'); }
              if (hasAuth()) return 'auth';

              // Ensure the real production authentication boot is loaded.
              // It uses the existing Supabase client and does not alter financial logic.
              if (!window.__CCF_B23016_AUTH_BOOT__) {
                var existing = document.querySelector('script[src*="CCF-AUTH-BOOT-FINAL.js"]');
                if (!existing) {
                  var s=document.createElement('script');
                  s.src='/CCF-AUTH-BOOT-FINAL.js?android=2.0';
                  s.async=false;
                  document.head.appendChild(s);
                }
              }

              if (hasAuth()) return 'auth';

              var selectors = [
                '[data-b230-open="login"]',
                '[data-action="login"]',
                '#loginBtn','#loginButton','.login-btn','.btn-login',
                'a[href*="login"]','button','a'
              ];
              for (var si=0; si<selectors.length; si++) {
                var els=document.querySelectorAll(selectors[si]);
                for (var i=0; i<els.length; i++) {
                  var el=els[i];
                  var txt=(el.innerText||el.textContent||'').replace(/\\s+/g,' ').trim().toLowerCase();
                  if (txt.indexOf('entrar al sistema')>=0 || txt.indexOf('iniciar sesión')>=0 || txt.indexOf('iniciar sesion')>=0 || txt==='entrar') {
                    try { el.click(); } catch(e) { try { el.dispatchEvent(new MouseEvent('click',{bubbles:true,cancelable:true,view:window})); } catch(_){} }
                    return 'clicked';
                  }
                }
              }
              return 'waiting';
            })();
            """.trimIndent()
        ) { result ->
            if (result == "\"auth\"" || result == "\"clicked\"") {
                loginForced = true
                applyMobileScaleAndLoginGuard()
                return@evaluateJavascript
            }
            if (loginAttempts < 160) handler.postDelayed({ tryOpenExistingLogin() }, 250L)
        }
    }

    private fun applyMobileScaleAndLoginGuard() {
        if (!pageReady || isFinishing || isDestroyed) return
        webView.evaluateJavascript(
            """
            (function(){
              if (!document.getElementById('ccf-android-guard-style')) {
                var st=document.createElement('style');
                st.id='ccf-android-guard-style';
                st.textContent=`
                  /* Android APK: keep the real auth portal as the first view. */
                  body.ccf-android-login-lock #app,
                  body.ccf-android-login-lock header,
                  body.ccf-android-login-lock main,
                  body.ccf-android-login-lock nav,
                  body.ccf-android-login-lock .landing,
                  body.ccf-android-login-lock [class*="landing"]{
                    visibility:hidden !important;
                  }
                  /* Compact mobile scale after authentication. */
                  #app.ccf-android-compact{
                    zoom:.90 !important;
                  }
                  @supports not (zoom:.9){
                    #app.ccf-android-compact{transform:scale(.90);transform-origin:top left;width:111.111%;}
                  }
                `;
                document.head.appendChild(st);
              }

              function compact(){
                var app=document.getElementById('app');
                var gate=document.getElementById('ccf-auth-gate');
                if (gate) {
                  document.body.classList.add('ccf-android-login-lock');
                  if (app) app.classList.remove('ccf-android-compact');
                } else {
                  document.body.classList.remove('ccf-android-login-lock');
                  if (app) app.classList.add('ccf-android-compact');
                }
              }
              compact();
              if (!window.__CCF_ANDROID_GUARD_OBSERVER__) {
                var obs=new MutationObserver(compact);
                obs.observe(document.documentElement,{subtree:true,childList:true,attributes:true,attributeFilter:['class','style','aria-hidden']});
                window.__CCF_ANDROID_GUARD_OBSERVER__=obs;
              }
              return 'guarded';
            })();
            """.trimIndent()
        )
    }

    private fun handleUrl(url: String): Boolean {
        val uri = Uri.parse(url)
        val scheme = uri.scheme?.lowercase() ?: return false
        if (scheme == "http" || scheme == "https") return !url.startsWith("https://controlfinanciero.cl", ignoreCase = true)
        return try { startActivity(Intent(Intent.ACTION_VIEW, uri)); true } catch (_: ActivityNotFoundException) { true }
    }

    override fun onSaveInstanceState(outState: Bundle) { webView.saveState(outState); super.onSaveInstanceState(outState) }
    @Deprecated("Deprecated in Android API 33") override fun onBackPressed() { if (webView.canGoBack()) webView.goBack() else super.onBackPressed() }
    override fun onDestroy() { handler.removeCallbacksAndMessages(null); webView.stopLoading(); webView.destroy(); super.onDestroy() }
}
