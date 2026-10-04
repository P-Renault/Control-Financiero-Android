package cl.controlfinanciero.app

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : Activity() {
    private lateinit var webView: WebView

    private val homeUrl = "https://controlfinanciero.cl/"
    private val authUrl = "https://controlfinanciero.cl/CCF-AUTH-BOOT-FINAL.js?android=2.9"
    private var authStarted = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this).apply {
            setBackgroundColor(Color.rgb(5, 11, 20))
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        setContentView(webView)
        configureWebView()

        // B2.9: the APK does NOT start by rendering the production landing page.
        // It starts from the real CCF authentication engine.
        startFromRealAuthBoot()
    }

    @SuppressLint("SetJavaScriptEnabled")
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
        s.userAgentString = s.userAgentString + " CCFAndroid/2.9"

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()
                return handleUrl(url)
            }

            @Deprecated("Deprecated in Android API 24")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return handleUrl(url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                // When authentication redirects to production, let the REAL
                // production auth boot see the persisted Supabase session.
                if (url.startsWith(homeUrl)) {
                    webView.evaluateJavascript(
                        """
                        (function(){
                          try { window.__CCF_B23016_AUTH_BOOT__ = false; } catch(e) {}
                          var old=document.querySelectorAll('script[src*="CCF-AUTH-BOOT-FINAL.js"]');
                          for(var i=0;i<old.length;i++){ try{old[i].remove();}catch(e){} }
                          var s=document.createElement('script');
                          s.src='/CCF-AUTH-BOOT-FINAL.js?android=2.9&ts='+Date.now();
                          s.async=false;
                          document.head.appendChild(s);
                          return 'booted';
                        })();
                        """.trimIndent(),
                        null
                    )

                    // Compact only the real financial application, never the login portal.
                    webView.postDelayed({ applyCompactAfterLogin() }, 900L)
                }
            }
        }
    }

    /**
     * The key architectural correction:
     *
     * The first document loaded by the APK is a tiny CCF authentication shell
     * whose only job is to load the REAL CCF-AUTH-BOOT-FINAL.js.
     *
     * The production landing is therefore not the APK entry point.
     */
    private fun startFromRealAuthBoot() {
        authStarted = true

        val shell = """
            <!doctype html>
            <html lang="es">
            <head>
              <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
              <title>CCF · Acceso</title>
              <style>
                html,body{margin:0;width:100%;height:100%;background:#050b14;overflow:hidden}
                body{font-family:system-ui,-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif}
              </style>
            </head>
            <body>
              <script>
                // CCF-AUTH-BOOT-FINAL.js calls openApp() after a valid session.
                // In the standalone auth shell, connect() transfers control to
                // the real production application; Supabase persists the session.
                window.connect = async function(){
                  window.location.replace('${homeUrl}?android=2.9-authenticated');
                };
                window.refresh = async function(){};
              </script>
              <script src="${authUrl}" async="false"></script>
            </body>
            </html>
        """.trimIndent()

        // Base URL keeps the document on the production origin, so the real
        // authentication script and Supabase session behave normally.
        webView.loadDataWithBaseURL(
            homeUrl,
            shell,
            "text/html",
            "UTF-8",
            homeUrl
        )
    }

    private fun applyCompactAfterLogin() {
        webView.evaluateJavascript(
            """
            (function(){
              var gate=document.getElementById('ccf-auth-gate');
              var app=document.getElementById('app');
              if(gate || !app) return 'login';
              var style=document.getElementById('ccf-android-compact-style');
              if(!style){
                style=document.createElement('style');
                style.id='ccf-android-compact-style';
                style.textContent=`
                  #app{zoom:.92 !important;}
                  @supports not (zoom:.92){
                    #app{transform:scale(.92);transform-origin:top left;width:108.696% !important;}
                  }
                `;
                document.head.appendChild(style);
              }
              return 'app';
            })();
            """.trimIndent(),
            null
        )
    }

    private fun handleUrl(url: String): Boolean {
        // Keep all CCF navigation inside the APK.
        return if (url.startsWith(homeUrl) || url.startsWith(authUrl)) {
            false
        } else {
            false
        }
    }

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
