package cl.controlfinanciero.app

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.graphics.drawable.GradientDrawable
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private lateinit var root: FrameLayout
    private lateinit var webView: WebView
    private lateinit var loginPanel: LinearLayout
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private val homeUrl = "https://controlfinanciero.cl/"
    private val supabaseUrl = "https://xgxvdbgmwvncmfdcxgsf.supabase.co"
    private val supabaseKey = "sb_publishable_fJqOSLC7dhYKttuU1uAvcQ_AX-aH4PB"
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var sessionInjected = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        configureWebView()
        webView.loadUrl(homeUrl)
    }

    private fun buildUi() {
        root = FrameLayout(this).apply { setBackgroundColor(Color.rgb(5, 15, 27)) }

        webView = WebView(this).apply { visibility = View.INVISIBLE }
        root.addView(webView, FrameLayout.LayoutParams(-1, -1))

        loginPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(28), dp(24), dp(28), dp(24))
            background = rounded(Color.rgb(11, 27, 43), 24)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val logo = ImageView(this).apply {
            setImageResource(cl.controlfinanciero.app.R.drawable.ccf_logo)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        content.addView(logo, LinearLayout.LayoutParams(dp(82), dp(82)).apply { bottomMargin = dp(12) })

        val title = TextView(this).apply {
            text = "Centro de Control Financiero"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        content.addView(title, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(6) })

        val subtitle = TextView(this).apply {
            text = "Iniciar sesión"
            textSize = 15f
            setTextColor(Color.rgb(155, 190, 215))
            gravity = Gravity.CENTER
        }
        content.addView(subtitle, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(24) })

        emailInput = EditText(this).apply {
            hint = "Correo electrónico"
            setHintTextColor(Color.rgb(130, 155, 175))
            setTextColor(Color.WHITE)
            textSize = 15f
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setSingleLine(true)
            background = rounded(Color.rgb(7, 17, 31), 12)
            setPadding(dp(14), 0, dp(14), 0)
        }
        content.addView(emailInput, LinearLayout.LayoutParams(-1, dp(50)).apply { bottomMargin = dp(12) })

        passwordInput = EditText(this).apply {
            hint = "Contraseña"
            setHintTextColor(Color.rgb(130, 155, 175))
            setTextColor(Color.WHITE)
            textSize = 15f
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
            setSingleLine(true)
            background = rounded(Color.rgb(7, 17, 31), 12)
            setPadding(dp(14), 0, dp(14), 0)
        }
        content.addView(passwordInput, LinearLayout.LayoutParams(-1, dp(50)).apply { bottomMargin = dp(16) })

        loginButton = Button(this).apply {
            text = "Ingresar al sistema"
            textSize = 14f
            setTextColor(Color.WHITE)
            isAllCaps = false
            background = rounded(Color.rgb(22, 136, 232), 12)
            setOnClickListener { signIn() }
        }
        content.addView(loginButton, LinearLayout.LayoutParams(-1, dp(50)).apply { bottomMargin = dp(12) })

        val status = TextView(this).apply {
            text = "Acceso seguro · Supabase"
            textSize = 12f
            setTextColor(Color.rgb(145, 165, 185))
            gravity = Gravity.CENTER
        }
        content.addView(status, LinearLayout.LayoutParams(-1, -2))
        loginPanel.addView(content, LinearLayout.LayoutParams(-1, -2))

        val lp = FrameLayout.LayoutParams(dp(420), ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
            leftMargin = dp(20)
            rightMargin = dp(20)
        }
        root.addView(loginPanel, lp)
        setContentView(root)
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
        s.loadWithOverviewMode = true
        s.setSupportZoom(false)
        s.builtInZoomControls = false
        s.displayZoomControls = false
        s.textZoom = 100
        s.userAgentString = s.userAgentString + " CCFAndroid/2.3"
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (accessToken != null && refreshToken != null && !sessionInjected) {
                    injectSessionAndOpen()
                }
            }
        }
    }

    private fun signIn() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa correo y contraseña.", Toast.LENGTH_SHORT).show()
            return
        }

        loginButton.isEnabled = false
        loginButton.text = "Validando…"

        executor.execute {
            try {
                val result = authenticateNative(email, password)
                runOnUiThread {
                    accessToken = result.first
                    refreshToken = result.second
                    sessionInjected = false
                    loginPanel.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                    webView.loadUrl(homeUrl)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    loginButton.isEnabled = true
                    loginButton.text = "Ingresar al sistema"
                    Toast.makeText(this, friendlyError(e), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun authenticateNative(email: String, password: String): Pair<String, String> {
        val endpoint = URL("$supabaseUrl/auth/v1/token?grant_type=password")
        val conn = endpoint.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = 15000
        conn.readTimeout = 20000
        conn.doOutput = true
        conn.setRequestProperty("apikey", supabaseKey)
        conn.setRequestProperty("Authorization", "Bearer $supabaseKey")
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Accept", "application/json")

        val body = JSONObject().apply {
            put("email", email)
            put("password", password)
        }.toString()

        OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { it.write(body) }
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
        conn.disconnect()

        if (code !in 200..299) {
            val message = try { JSONObject(response).optString("msg").ifBlank { JSONObject(response).optString("error_description") } } catch (_: Exception) { "" }
            throw Exception(if (message.isNotBlank()) message else "No fue posible validar las credenciales. Código $code")
        }

        val json = JSONObject(response)
        val access = json.optString("access_token")
        val refresh = json.optString("refresh_token")
        if (access.isBlank() || refresh.isBlank()) throw Exception("Supabase no entregó una sesión válida.")
        return access to refresh
    }

    private fun injectSessionAndOpen() {
        val access = accessToken ?: return
        val refresh = refreshToken ?: return
        val script = """
            (async function(){
              try{
                if(!window.supabase || !window.supabase.createClient){
                  var s=document.createElement('script');
                  s.src='https://unpkg.com/@supabase/supabase-js@2';
                  s.onload=function(){ location.reload(); };
                  s.onerror=function(){ return; };
                  document.head.appendChild(s);
                  return 'loading';
                }
                var c=window.supabase.createClient(${JSONObject.quote(supabaseUrl)},${JSONObject.quote(supabaseKey)},{auth:{persistSession:true,autoRefreshToken:true,detectSessionInUrl:true}});
                var r=await c.auth.setSession({access_token:${JSONObject.quote(access)},refresh_token:${JSONObject.quote(refresh)}});
                if(r.error) return 'ERR:'+r.error.message;
                window.supabaseClient=c;
                window.supabaseAuthSession=r.data.session;
                return 'OK';
              }catch(e){return 'ERR:'+String(e);}
            })();
        """.trimIndent()

        webView.evaluateJavascript(script) { result ->
            val value = result.trim().removeSurrounding("\"").replace("\\\"", "\"")
            if (value == "OK") {
                sessionInjected = true
                applyCompactScale()
                handler.postDelayed({ webView.reload() }, 150)
            } else if (value.startsWith("ERR:")) {
                runOnUiThread {
                    Toast.makeText(this, value.removePrefix("ERR:"), Toast.LENGTH_LONG).show()
                    loginPanel.visibility = View.VISIBLE
                    webView.visibility = View.INVISIBLE
                    loginButton.isEnabled = true
                    loginButton.text = "Ingresar al sistema"
                }
            } else {
                handler.postDelayed({ injectSessionAndOpen() }, 500)
            }
        }
    }

    private fun applyCompactScale() {
        webView.evaluateJavascript("""
            (function(){
              if(!document.getElementById('ccf-android-compact-style')){
                var st=document.createElement('style'); st.id='ccf-android-compact-style';
                st.textContent='#app{zoom:.90!important}@supports not (zoom:.9){#app{transform:scale(.90);transform-origin:top left;width:111.111%}}';
                document.head.appendChild(st);
              }
              return 'compact';
            })();
        """.trimIndent(), null)
    }

    private fun friendlyError(e: Exception): String {
        val m = e.message.orEmpty()
        return when {
            m.contains("Invalid login credentials", true) -> "Correo o contraseña incorrectos."
            m.contains("Email not confirmed", true) -> "Debes confirmar tu correo antes de ingresar."
            m.isNotBlank() -> m
            else -> "No fue posible iniciar sesión."
        }
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        executor.shutdownNow()
        webView.stopLoading()
        webView.destroy()
        super.onDestroy()
    }
}
