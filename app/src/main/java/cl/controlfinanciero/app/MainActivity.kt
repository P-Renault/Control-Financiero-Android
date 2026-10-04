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
import android.net.Uri
import android.content.Intent
import android.content.ActivityNotFoundException
import android.util.Base64

class MainActivity : Activity() {
    private lateinit var webView: WebView

    private val homeUrl = "https://controlfinanciero.cl/"
    private val authJsBase64 = "LyogQ0NGIEIyLjMxLjIg4oCUIEFVVEggQ0xJRU5UIEJSSURHRSDigJQgRklYIDIwMjYuMDkuMjYKICAgQ29ycmVjY2nDs246CiAgIC0gVW4gw7puaWNvIGNsaWVudGUgU3VwYWJhc2UgYXV0ZW50aWNhZG8uCiAgIC0gQ2FyZ2Egcm9idXN0YSBkZSBAc3VwYWJhc2Uvc3VwYWJhc2UtanMgc2kgZWwgYnVuZGxlIHByaW5jaXBhbCBubyBxdWVkw7MgZGlzcG9uaWJsZS4KICAgLSBNZW5zYWplcyBkZSBlc3RhZG8gY2xhcm9zIGR1cmFudGUgaW5pY2lhbGl6YWNpw7NuLgogICAtIE5vIG1vZGlmaWNhIHRhYmxhcywgU1FMLCBSTFMgbmkgZGF0b3MgZmluYW5jaWVyb3MuCiovCigoKT0+ewondXNlIHN0cmljdCc7CmlmKHdpbmRvdy5fX0NDRl9CMjMwMTZfQVVUSF9CT09UX18pcmV0dXJuOwp3aW5kb3cuX19DQ0ZfQjIzMDE2X0FVVEhfQk9PVF9fPXRydWU7Cgpjb25zdCBWRVJTSU9OPSdCMi4zMS4yLUZJWC0yMDI2LjA5LjI2JzsKY29uc3QgU1VQQUJBU0VfVVJMPSdodHRwczovL3hneHZkYmdtd3ZuY21mZGN4Z3NmLnN1cGFiYXNlLmNvJzsKY29uc3QgU1VQQUJBU0VfS0VZPSdzYl9wdWJsaXNoYWJsZV9mSnFPU0xDN2RoWUt0dHVVMXVBdmNRX0FYLWFINFBCJzsKbGV0IGNsaWVudD1udWxsLCBtb2RlPSdsb2dpbic7Cgpjb25zdCAkPWlkPT5kb2N1bWVudC5nZXRFbGVtZW50QnlJZChpZCk7CmNvbnN0IHNsZWVwPW1zPT5uZXcgUHJvbWlzZShyPT5zZXRUaW1lb3V0KHIsbXMpKTsKCmZ1bmN0aW9uIGhpZGVMZWdhY3koKXsKICQoJ2NvbmZpZ1BhbmVsJyk/LmNsYXNzTGlzdC5hZGQoJ2hpZGRlbicpOwogJCgnYXBwJyk/LmNsYXNzTGlzdC5hZGQoJ2hpZGRlbicpOwogJCgnbG9nb3V0QnRuJyk/LmNsYXNzTGlzdC5hZGQoJ2hpZGRlbicpOwp9CgpmdW5jdGlvbiByZXZlYWxBcHAoKXsKICQoJ2NvbmZpZ1BhbmVsJyk/LmNsYXNzTGlzdC5hZGQoJ2hpZGRlbicpOwogY29uc3QgYXBwPSQoJ2FwcCcpOwogaWYoYXBwKXsKICAgYXBwLmNsYXNzTGlzdC5yZW1vdmUoJ2hpZGRlbicpOwogICBhcHAuY2xhc3NMaXN0LnJlbW92ZSgnYjIzMC1oaWRkZW4tYXBwJyk7CiAgIGFwcC5zdHlsZS5yZW1vdmVQcm9wZXJ0eSgnZGlzcGxheScpOwogICBhcHAucmVtb3ZlQXR0cmlidXRlKCdhcmlhLWhpZGRlbicpOwogfQogJCgnbG9nb3V0QnRuJyk/LmNsYXNzTGlzdC5yZW1vdmUoJ2hpZGRlbicpOwogJCgnY2NmLWF1dGgtZ2F0ZScpPy5yZW1vdmUoKTsKfQoKZnVuY3Rpb24gc3RhdHVzKHQsZXJyb3I9ZmFsc2UpewogY29uc3QgZT0kKCdjY2YtYXV0aC1zdGF0dXMnKTsKIGlmKGUpewogICBlLnRleHRDb250ZW50PXQ7CiAgIGUuc3R5bGUuY29sb3I9ZXJyb3I/JyNmZWNhY2EnOicjOWRiMGM3JzsKIH0KfQoKZnVuY3Rpb24gcG9ydGFsKCl7CiBpZigkKCdjY2YtYXV0aC1nYXRlJykpcmV0dXJuOwoKIGNvbnN0IGdhdGU9ZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnZGl2Jyk7CiBnYXRlLmlkPSdjY2YtYXV0aC1nYXRlJzsKIGdhdGUuaW5uZXJIVE1MPWAKIDxkaXYgY2xhc3M9ImNjZi1hdXRoLXNoZWxsIiByb2xlPSJkaWFsb2ciIGFyaWEtbGFiZWw9IkFjY2VzbyBhbCBDZW50cm8gZGUgQ29udHJvbCBGaW5hbmNpZXJvIj4KICA8ZGl2IGNsYXNzPSJjY2YtYXV0aC1icmFuZCI+CiAgIDxzcGFuPkNDRjwvc3Bhbj4KICAgPGRpdj4KICAgIDxzdHJvbmc+Q2VudHJvIGRlIENvbnRyb2wgRmluYW5jaWVybzwvc3Ryb25nPgogICAgPHNtYWxsPiR7VkVSU0lPTn0gwrcgQWNjZXNvIHNlZ3Vybzwvc21hbGw+CiAgIDwvZGl2PgogIDwvZGl2PgoKICA8ZGl2IGNsYXNzPSJjY2YtYXV0aC1jYXJkIj4KICAgPGgyIGlkPSJjY2YtYXV0aC10aXRsZSI+SW5pY2lhciBzZXNpw7NuPC9oMj4KICAgPHAgaWQ9ImNjZi1hdXRoLWhlbHAiPkFjY2VkZSBhIHR1IHNpc3RlbWEgZmluYW5jaWVybyB5IGNvbnRpbsO6YSBkb25kZSBsbyBkZWphc3RlLjwvcD4KCiAgIDxmb3JtIGlkPSJjY2YtYXV0aC1mb3JtIiBub3ZhbGlkYXRlPgogICAgPGxhYmVsPgogICAgICBDb3JyZW8gZWxlY3Ryw7NuaWNvCiAgICAgIDxpbnB1dCBpZD0iY2NmLWVtYWlsIiB0eXBlPSJlbWFpbCIgYXV0b2NvbXBsZXRlPSJlbWFpbCIgcmVxdWlyZWQ+CiAgICA8L2xhYmVsPgoKICAgIDxsYWJlbD4KICAgICAgQ29udHJhc2XDsWEKICAgICAgPGlucHV0IGlkPSJjY2YtcGFzc3dvcmQiIHR5cGU9InBhc3N3b3JkIiBhdXRvY29tcGxldGU9ImN1cnJlbnQtcGFzc3dvcmQiIG1pbmxlbmd0aD0iOCIgcmVxdWlyZWQ+CiAgICA8L2xhYmVsPgoKICAgIDxidXR0b24gdHlwZT0ic3VibWl0IiBpZD0iY2NmLXN1Ym1pdCI+SW5ncmVzYXIgYWwgc2lzdGVtYTwvYnV0dG9uPgogICA8L2Zvcm0+CgogICA8YnV0dG9uIHR5cGU9ImJ1dHRvbiIgY2xhc3M9ImNjZi1saW5rIiBpZD0iY2NmLXN3aXRjaCI+Q3JlYXIgdW5hIGN1ZW50YTwvYnV0dG9uPgogICA8ZGl2IGlkPSJjY2YtYXV0aC1zdGF0dXMiIGFyaWEtbGl2ZT0icG9saXRlIj5JbmljaWFsaXphbmRvIGFjY2Vzb+KApjwvZGl2PgogIDwvZGl2PgogPC9kaXY+YDsKCiBkb2N1bWVudC5ib2R5LmFwcGVuZENoaWxkKGdhdGUpOwoKIGNvbnN0IHN0eWxlPWRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoJ3N0eWxlJyk7CiBzdHlsZS5pZD0nY2NmLWF1dGgtYm9vdC1zdHlsZSc7CiBzdHlsZS50ZXh0Q29udGVudD1gCiAjY2NmLWF1dGgtZ2F0ZXsKICAgcG9zaXRpb246Zml4ZWQ7CiAgIGluc2V0OjA7CiAgIHotaW5kZXg6MjE0NzQ4MzY0NjsKICAgb3ZlcmZsb3c6YXV0bzsKICAgYmFja2dyb3VuZDoKICAgICByYWRpYWwtZ3JhZGllbnQoY2lyY2xlIGF0IDgwJSAxMCUscmdiYSgyMiwxMzYsMjMyLC4yKSx0cmFuc3BhcmVudCAzMCUpLAogICAgIGxpbmVhci1ncmFkaWVudCgxMzVkZWcsIzA1MGIxNCwjMDcxMTFmIDU1JSwjMDkxOTJiKTsKICAgY29sb3I6I2VlZjZmZjsKICAgZm9udC1mYW1pbHk6c3lzdGVtLXVpLC1hcHBsZS1zeXN0ZW0sQmxpbmtNYWNTeXN0ZW1Gb250LCJTZWdvZSBVSSIsc2Fucy1zZXJpZgogfQogLmNjZi1hdXRoLXNoZWxsewogICBtaW4taGVpZ2h0OjEwMCU7CiAgIGRpc3BsYXk6Z3JpZDsKICAgcGxhY2UtaXRlbXM6Y2VudGVyOwogICBwYWRkaW5nOjI0cHgKIH0KIC5jY2YtYXV0aC1jYXJkewogICB3aWR0aDptaW4oNDMwcHgsMTAwJSk7CiAgIHBhZGRpbmc6MjZweDsKICAgYm9yZGVyOjFweCBzb2xpZCByZ2JhKDE0OCwxNjMsMTg0LC4yKTsKICAgYm9yZGVyLXJhZGl1czoyMHB4OwogICBiYWNrZ3JvdW5kOiMwYjE4Mjg7CiAgIGJveC1zaGFkb3c6MCAzMHB4IDkwcHggcmdiYSgwLDAsMCwuNDUpCiB9CiAuY2NmLWF1dGgtYnJhbmR7CiAgIHBvc2l0aW9uOmZpeGVkOwogICB0b3A6MThweDsKICAgbGVmdDoyMnB4OwogICBkaXNwbGF5OmZsZXg7CiAgIGdhcDoxMHB4OwogICBhbGlnbi1pdGVtczpjZW50ZXIKIH0KIC5jY2YtYXV0aC1icmFuZD5zcGFuewogICBkaXNwbGF5OmdyaWQ7CiAgIHBsYWNlLWl0ZW1zOmNlbnRlcjsKICAgd2lkdGg6NDBweDsKICAgaGVpZ2h0OjQwcHg7CiAgIGJvcmRlci1yYWRpdXM6MTFweDsKICAgYmFja2dyb3VuZDojMTY4OGU4OwogICBmb250LXdlaWdodDo5MDAKIH0KIC5jY2YtYXV0aC1icmFuZCBzdHJvbmd7CiAgIGRpc3BsYXk6YmxvY2s7CiAgIGZvbnQtc2l6ZToxNHB4CiB9CiAuY2NmLWF1dGgtYnJhbmQgc21hbGx7CiAgIGRpc3BsYXk6YmxvY2s7CiAgIGNvbG9yOiM4ZmE0YmI7CiAgIGZvbnQtc2l6ZToxMHB4OwogICBtYXJnaW4tdG9wOjJweAogfQogLmNjZi1hdXRoLWNhcmQgaDJ7CiAgIG1hcmdpbjowIDAgN3B4OwogICBmb250LXNpemU6MjdweAogfQogLmNjZi1hdXRoLWNhcmQgcHsKICAgbWFyZ2luOjAgMCAyMHB4OwogICBjb2xvcjojOWRiMGM3OwogICBmb250LXNpemU6MTNweDsKICAgbGluZS1oZWlnaHQ6MS41CiB9CiAuY2NmLWF1dGgtY2FyZCBmb3JtewogICBkaXNwbGF5OmdyaWQ7CiAgIGdhcDoxMnB4CiB9CiAuY2NmLWF1dGgtY2FyZCBsYWJlbHsKICAgZGlzcGxheTpncmlkOwogICBnYXA6NnB4OwogICBjb2xvcjojY2JkOGU2OwogICBmb250LXNpemU6MTJweDsKICAgZm9udC13ZWlnaHQ6NzAwCiB9CiAuY2NmLWF1dGgtY2FyZCBpbnB1dHsKICAgd2lkdGg6MTAwJTsKICAgYm94LXNpemluZzpib3JkZXItYm94OwogICBoZWlnaHQ6NDZweDsKICAgcGFkZGluZzowIDEycHg7CiAgIGJvcmRlci1yYWRpdXM6MTBweDsKICAgYm9yZGVyOjFweCBzb2xpZCByZ2JhKDE0OCwxNjMsMTg0LC4yMik7CiAgIGJhY2tncm91bmQ6IzA3MTExZjsKICAgY29sb3I6I2ZmZjsKICAgZm9udDppbmhlcml0CiB9CiAuY2NmLWF1dGgtY2FyZCBidXR0b257CiAgIGhlaWdodDo0NnB4OwogICBib3JkZXI6MDsKICAgYm9yZGVyLXJhZGl1czoxMHB4OwogICBjdXJzb3I6cG9pbnRlcjsKICAgZm9udDo4MDAgMTNweCBzeXN0ZW0tdWkKIH0KIC5jY2YtYXV0aC1jYXJkIGZvcm0gYnV0dG9uewogICBiYWNrZ3JvdW5kOiMxNjg4ZTg7CiAgIGNvbG9yOiNmZmYKIH0KIC5jY2YtbGlua3sKICAgd2lkdGg6MTAwJTsKICAgbWFyZ2luLXRvcDoxMXB4OwogICBiYWNrZ3JvdW5kOnRyYW5zcGFyZW50OwogICBjb2xvcjojNTRjYWZmIWltcG9ydGFudAogfQogLmNjZi1hdXRoLWNhcmQgYnV0dG9uOmRpc2FibGVkewogICBvcGFjaXR5Oi42NTsKICAgY3Vyc29yOndhaXQKIH0KICNjY2YtYXV0aC1zdGF0dXN7CiAgIG1pbi1oZWlnaHQ6MjBweDsKICAgbWFyZ2luLXRvcDoxM3B4OwogICBjb2xvcjojOWRiMGM3OwogICBmb250LXNpemU6MTFweDsKICAgbGluZS1oZWlnaHQ6MS40NQogfQogQG1lZGlhKG1heC13aWR0aDo2MDBweCl7CiAgIC5jY2YtYXV0aC1icmFuZHsKICAgICBwb3NpdGlvbjpzdGF0aWM7CiAgICAganVzdGlmeS1jb250ZW50OmNlbnRlcjsKICAgICBtYXJnaW4tYm90dG9tOjEwcHgKICAgfQogICAuY2NmLWF1dGgtc2hlbGx7CiAgICAgYWxpZ24tY29udGVudDpjZW50ZXIKICAgfQogfQogYDsKIGRvY3VtZW50LmhlYWQuYXBwZW5kQ2hpbGQoc3R5bGUpOwoKICQoJ2NjZi1zd2l0Y2gnKS5vbmNsaWNrPSgpPT5zZXRNb2RlKG1vZGU9PT0nbG9naW4nPydyZWdpc3Rlcic6J2xvZ2luJyk7Cn0KCmZ1bmN0aW9uIHNldE1vZGUobmV4dCl7CiBtb2RlPW5leHQ9PT0ncmVnaXN0ZXInPydyZWdpc3Rlcic6J2xvZ2luJzsKCiBjb25zdCB0aXRsZT0kKCdjY2YtYXV0aC10aXRsZScpOwogY29uc3QgaGVscD0kKCdjY2YtYXV0aC1oZWxwJyk7CiBjb25zdCBzdWJtaXQ9JCgnY2NmLXN1Ym1pdCcpOwogY29uc3Qgc3c9JCgnY2NmLXN3aXRjaCcpOwogY29uc3QgcHc9JCgnY2NmLXBhc3N3b3JkJyk7CgogaWYobW9kZT09PSdyZWdpc3RlcicpewogICB0aXRsZS50ZXh0Q29udGVudD0nQ3JlYXIgY3VlbnRhJzsKICAgaGVscC50ZXh0Q29udGVudD0nQ3JlYSB0dSBhY2Nlc28gYWwgc2lzdGVtYSBmaW5hbmNpZXJvIGNvbiB1bmEgY29udHJhc2XDsWEgZGUgYWwgbWVub3MgOCBjYXJhY3RlcmVzLic7CiAgIHN1Ym1pdC50ZXh0Q29udGVudD0nQ3JlYXIgY3VlbnRhJzsKICAgc3cudGV4dENvbnRlbnQ9J1ZvbHZlciBhIGluaWNpYXIgc2VzacOzbic7CiAgIHB3LmF1dG9jb21wbGV0ZT0nbmV3LXBhc3N3b3JkJzsKICAgc3RhdHVzKCdDb21wbGV0YSBsb3MgZGF0b3MgcGFyYSBjcmVhciB0dSBjdWVudGEuJyk7CiB9ZWxzZXsKICAgdGl0bGUudGV4dENvbnRlbnQ9J0luaWNpYXIgc2VzacOzbic7CiAgIGhlbHAudGV4dENvbnRlbnQ9J0FjY2VkZSBhIHR1IHNpc3RlbWEgZmluYW5jaWVybyB5IGNvbnRpbsO6YSBkb25kZSBsbyBkZWphc3RlLic7CiAgIHN1Ym1pdC50ZXh0Q29udGVudD0nSW5ncmVzYXIgYWwgc2lzdGVtYSc7CiAgIHN3LnRleHRDb250ZW50PSdDcmVhciB1bmEgY3VlbnRhJzsKICAgcHcuYXV0b2NvbXBsZXRlPSdjdXJyZW50LXBhc3N3b3JkJzsKICAgc3RhdHVzKCdJbmdyZXNhIGNvbiB0dSBjdWVudGEuJyk7CiB9Cn0KCmZ1bmN0aW9uIGluc3RhbGxDbGllbnQoKXsKIGlmKCF3aW5kb3cuc3VwYWJhc2U/LmNyZWF0ZUNsaWVudClyZXR1cm4gZmFsc2U7CgogaWYoIWNsaWVudCl7CiAgIGNsaWVudD13aW5kb3cuc3VwYWJhc2UuY3JlYXRlQ2xpZW50KAogICAgIFNVUEFCQVNFX1VSTCwKICAgICBTVVBBQkFTRV9LRVksCiAgICAgewogICAgICAgYXV0aDp7CiAgICAgICAgIHBlcnNpc3RTZXNzaW9uOnRydWUsCiAgICAgICAgIGF1dG9SZWZyZXNoVG9rZW46dHJ1ZSwKICAgICAgICAgZGV0ZWN0U2Vzc2lvbkluVXJsOnRydWUKICAgICAgIH0KICAgICB9CiAgICk7CiB9Cgogd2luZG93LnN1cGFiYXNlQ2xpZW50PWNsaWVudDsKIHdpbmRvdy5kYj1jbGllbnQ7CiB3aW5kb3cuX19kYj1jbGllbnQ7CiB3aW5kb3cuX19CMjMyNjlfQ0xJRU5UX189Y2xpZW50Owogd2luZG93Ll9fQjIzMjcwX0NMSUVOVF9fPWNsaWVudDsKIHdpbmRvdy5fX0IyMzI3M19DTElFTlRfXz1jbGllbnQ7CgogaWYoIXdpbmRvdy5fX0NDRl9BVVRIX0NSRUFURUNMSUVOVF9QQVRDSEVEX18pewogICBjb25zdCBvcmlnaW5hbENyZWF0ZUNsaWVudD13aW5kb3cuc3VwYWJhc2UuY3JlYXRlQ2xpZW50LmJpbmQod2luZG93LnN1cGFiYXNlKTsKCiAgIHdpbmRvdy5zdXBhYmFzZS5jcmVhdGVDbGllbnQ9ZnVuY3Rpb24odXJsLGtleSxvcHRpb25zKXsKICAgICBpZigKICAgICAgIFN0cmluZyh1cmx8fCcnKT09PVNVUEFCQVNFX1VSTCAmJgogICAgICAgU3RyaW5nKGtleXx8JycpPT09U1VQQUJBU0VfS0VZCiAgICAgKXsKICAgICAgIHJldHVybiBjbGllbnQ7CiAgICAgfQogICAgIHJldHVybiBvcmlnaW5hbENyZWF0ZUNsaWVudCh1cmwsa2V5LG9wdGlvbnMpOwogICB9OwoKICAgd2luZG93Ll9fQ0NGX0FVVEhfQ1JFQVRFQ0xJRU5UX1BBVENIRURfXz10cnVlOwogfQoKIHRyeXsKICAgbG9jYWxTdG9yYWdlLnNldEl0ZW0oJ3NmX3VybCcsU1VQQUJBU0VfVVJMKTsKICAgbG9jYWxTdG9yYWdlLnNldEl0ZW0oJ3NmX2tleScsU1VQQUJBU0VfS0VZKTsKIH1jYXRjaChfKXt9CgogcmV0dXJuIHRydWU7Cn0KCi8qIENhcmdhIGFsdGVybmF0aXZhIHBhcmEgZXZpdGFyIHF1ZWRhciBhdHJhcGFkbyBlbgogICAiSW5pY2lhbGl6YW5kbyBhY2Nlc2/igKYiIGN1YW5kbyBlbCBidW5kbGUgcHJpbmNpcGFsIGRlCiAgIFN1cGFiYXNlIG5vIGVzdMOhIGRpc3BvbmlibGUuICovCmZ1bmN0aW9uIGxvYWRTdXBhYmFzZUZhbGxiYWNrKCl7CiByZXR1cm4gbmV3IFByb21pc2UoKHJlc29sdmUscmVqZWN0KT0+ewogICBpZih3aW5kb3cuc3VwYWJhc2U/LmNyZWF0ZUNsaWVudCl7CiAgICAgcmVzb2x2ZSh0cnVlKTsKICAgICByZXR1cm47CiAgIH0KCiAgIGlmKHdpbmRvdy5fX0NDRl9TVVBBQkFTRV9GQUxMQkFDS19MT0FESU5HX18pewogICAgIGxldCBuPTA7CgogICAgIGNvbnN0IHRpbWVyPXNldEludGVydmFsKCgpPT57CiAgICAgICBpZih3aW5kb3cuc3VwYWJhc2U/LmNyZWF0ZUNsaWVudCl7CiAgICAgICAgIGNsZWFySW50ZXJ2YWwodGltZXIpOwogICAgICAgICByZXNvbHZlKHRydWUpOwogICAgICAgfWVsc2UgaWYoKytuPjgwKXsKICAgICAgICAgY2xlYXJJbnRlcnZhbCh0aW1lcik7CiAgICAgICAgIHJlamVjdChuZXcgRXJyb3IoJ05vIHNlIHB1ZG8gY2FyZ2FyIGxhIGJpYmxpb3RlY2EgZGUgU3VwYWJhc2UuJykpOwogICAgICAgfQogICAgIH0sMTAwKTsKCiAgICAgcmV0dXJuOwogICB9CgogICB3aW5kb3cuX19DQ0ZfU1VQQUJBU0VfRkFMTEJBQ0tfTE9BRElOR19fPXRydWU7CgogICBjb25zdCBzY3JpcHQ9ZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnc2NyaXB0Jyk7CiAgIHNjcmlwdC5zcmM9J2h0dHBzOi8vdW5wa2cuY29tL0BzdXBhYmFzZS9zdXBhYmFzZS1qc0AyJzsKICAgc2NyaXB0LmFzeW5jPXRydWU7CgogICBzY3JpcHQub25sb2FkPSgpPT57CiAgICAgaWYod2luZG93LnN1cGFiYXNlPy5jcmVhdGVDbGllbnQpewogICAgICAgcmVzb2x2ZSh0cnVlKTsKICAgICB9ZWxzZXsKICAgICAgIHJlamVjdChuZXcgRXJyb3IoCiAgICAgICAgICdTdXBhYmFzZSBzZSBjYXJnw7MsIHBlcm8gbm8gZXhwdXNvIGNyZWF0ZUNsaWVudC4nCiAgICAgICApKTsKICAgICB9CiAgIH07CgogICBzY3JpcHQub25lcnJvcj0oKT0+ewogICAgIHJlamVjdChuZXcgRXJyb3IoCiAgICAgICAnTm8gc2UgcHVkbyBjYXJnYXIgbGEgYmlibGlvdGVjYSBkZSBTdXBhYmFzZS4gUmV2aXNhIGxhIGNvbmV4acOzbiBhIEludGVybmV0IG8gbG9zIGJsb3F1ZWFkb3JlcyBkZWwgbmF2ZWdhZG9yLicKICAgICApKTsKICAgfTsKCiAgIGRvY3VtZW50LmhlYWQuYXBwZW5kQ2hpbGQoc2NyaXB0KTsKIH0pOwp9Cgphc3luYyBmdW5jdGlvbiB3YWl0Q2xpZW50KCl7CiBmb3IobGV0IGk9MDtpPDMwO2krKyl7CiAgIGlmKGluc3RhbGxDbGllbnQoKSlyZXR1cm4gY2xpZW50OwogICBhd2FpdCBzbGVlcCgxMDApOwogfQoKIHN0YXR1cygnQ2FyZ2FuZG8gY29tcG9uZW50ZSBkZSBhY2Nlc2/igKYnKTsKCiB0cnl7CiAgIGF3YWl0IGxvYWRTdXBhYmFzZUZhbGxiYWNrKCk7CiB9Y2F0Y2goZSl7CiAgIHRocm93IGU7CiB9CgogZm9yKGxldCBpPTA7aTwzMDtpKyspewogICBpZihpbnN0YWxsQ2xpZW50KCkpcmV0dXJuIGNsaWVudDsKICAgYXdhaXQgc2xlZXAoMTAwKTsKIH0KCiB0aHJvdyBuZXcgRXJyb3IoJ0xhIGJpYmxpb3RlY2EgZGUgU3VwYWJhc2Ugbm8gc2UgY2FyZ8OzLicpOwp9Cgphc3luYyBmdW5jdGlvbiBvcGVuQXBwKCl7CiB0cnl7CiAgIGlmKHR5cGVvZiB3aW5kb3cuY29ubmVjdD09PSdmdW5jdGlvbicpewogICAgIGF3YWl0IHdpbmRvdy5jb25uZWN0KCk7CiAgIH0KIH1jYXRjaChlKXsKICAgY29uc29sZS53YXJuKCdbQ0NGIEFVVEhdIGNvbm5lY3QnLGUpOwogfQoKIHJldmVhbEFwcCgpOwoKIHRyeXsKICAgaWYodHlwZW9mIHdpbmRvdy5yZWZyZXNoPT09J2Z1bmN0aW9uJyl7CiAgICAgYXdhaXQgd2luZG93LnJlZnJlc2goKTsKICAgfQogfWNhdGNoKGUpewogICBjb25zb2xlLndhcm4oJ1tDQ0YgQVVUSF0gcmVmcmVzaCcsZSk7CiB9Cn0KCmFzeW5jIGZ1bmN0aW9uIHN1Ym1pdEF1dGgoKXsKIGNvbnN0IGJ0bj0kKCdjY2Ytc3VibWl0Jyk7CiBjb25zdCBlbWFpbD0kKCdjY2YtZW1haWwnKT8udmFsdWUudHJpbSgpOwogY29uc3QgcGFzc3dvcmQ9JCgnY2NmLXBhc3N3b3JkJyk/LnZhbHVlfHwnJzsKCiBpZighZW1haWx8fCFwYXNzd29yZCl7CiAgIHN0YXR1cygnQ29tcGxldGEgY29ycmVvIHkgY29udHJhc2XDsWEuJyx0cnVlKTsKICAgcmV0dXJuOwogfQoKIGlmKHBhc3N3b3JkLmxlbmd0aDw4KXsKICAgc3RhdHVzKCdMYSBjb250cmFzZcOxYSBkZWJlIHRlbmVyIGFsIG1lbm9zIDggY2FyYWN0ZXJlcy4nLHRydWUpOwogICByZXR1cm47CiB9CgogYnRuLmRpc2FibGVkPXRydWU7Cgogc3RhdHVzKAogICBtb2RlPT09J3JlZ2lzdGVyJwogICAgID8gJ0NyZWFuZG8gY3VlbnRh4oCmJwogICAgIDogJ1ZhbGlkYW5kbyBjcmVkZW5jaWFsZXPigKYnCiApOwoKIHRyeXsKICAgaWYobW9kZT09PSdyZWdpc3RlcicpewogICAgIGNvbnN0IHtkYXRhLGVycm9yfT1hd2FpdCBjbGllbnQuYXV0aC5zaWduVXAoewogICAgICAgZW1haWwsCiAgICAgICBwYXNzd29yZCwKICAgICAgIG9wdGlvbnM6ewogICAgICAgICBlbWFpbFJlZGlyZWN0VG86CiAgICAgICAgICAgd2luZG93LmxvY2F0aW9uLm9yaWdpbit3aW5kb3cubG9jYXRpb24ucGF0aG5hbWUKICAgICAgIH0KICAgICB9KTsKCiAgICAgaWYoZXJyb3IpdGhyb3cgZXJyb3I7CgogICAgIGlmKGRhdGE/LnNlc3Npb24pewogICAgICAgYXdhaXQgb3BlbkFwcCgpOwogICAgIH1lbHNlewogICAgICAgc3RhdHVzKAogICAgICAgICAnQ3VlbnRhIGNyZWFkYS4gUmV2aXNhIHR1IGNvcnJlbyBwYXJhIGNvbmZpcm1hciBsYSBjdWVudGEgeSBsdWVnbyBpbmljaWEgc2VzacOzbi4nCiAgICAgICApOwogICAgIH0KCiAgIH1lbHNlewogICAgIGNvbnN0IHtkYXRhLGVycm9yfT1hd2FpdCBjbGllbnQuYXV0aC5zaWduSW5XaXRoUGFzc3dvcmQoewogICAgICAgZW1haWwsCiAgICAgICBwYXNzd29yZAogICAgIH0pOwoKICAgICBpZihlcnJvcil0aHJvdyBlcnJvcjsKCiAgICAgaWYoIWRhdGE/LnNlc3Npb24pewogICAgICAgdGhyb3cgbmV3IEVycm9yKAogICAgICAgICAnU3VwYWJhc2Ugbm8gZW50cmVnw7MgdW5hIHNlc2nDs24gYWN0aXZhLicKICAgICAgICk7CiAgICAgfQoKICAgICBhd2FpdCBvcGVuQXBwKCk7CiAgIH0KCiB9Y2F0Y2goZSl7CiAgIGNvbnNvbGUuZXJyb3IoJ1tDQ0YgQVVUSF0gYXV0aCcsZSk7CiAgIHN0YXR1cygKICAgICBlPy5tZXNzYWdlfHwnTm8gZnVlIHBvc2libGUgY29tcGxldGFyIGxhIG9wZXJhY2nDs24uJywKICAgICB0cnVlCiAgICk7CiB9ZmluYWxseXsKICAgYnRuLmRpc2FibGVkPWZhbHNlOwogfQp9CgpmdW5jdGlvbiBpbnN0YWxsRm9ybXMoKXsKICQoJ2NjZi1hdXRoLWZvcm0nKT8uYWRkRXZlbnRMaXN0ZW5lcigKICAgJ3N1Ym1pdCcsCiAgIGU9PnsKICAgICBlLnByZXZlbnREZWZhdWx0KCk7CiAgICAgc3VibWl0QXV0aCgpOwogICB9CiApOwoKIGNvbnN0IGxvZ291dD0kKCdsb2dvdXRCdG4nKTsKCiBpZigKICAgbG9nb3V0ICYmCiAgICFsb2dvdXQuZGF0YXNldC5jY2ZGaW5hbEF1dGgKICl7CiAgIGxvZ291dC5kYXRhc2V0LmNjZkZpbmFsQXV0aD0nMSc7CgogICBsb2dvdXQuYWRkRXZlbnRMaXN0ZW5lcigKICAgICAnY2xpY2snLAogICAgIGFzeW5jIGU9PnsKICAgICAgIGUucHJldmVudERlZmF1bHQoKTsKICAgICAgIGUuc3RvcEltbWVkaWF0ZVByb3BhZ2F0aW9uKCk7CgogICAgICAgdHJ5ewogICAgICAgICBhd2FpdCBjbGllbnQ/LmF1dGg/LnNpZ25PdXQoe3Njb3BlOidsb2NhbCd9KTsKICAgICAgIH1maW5hbGx5ewogICAgICAgICBsb2NhdGlvbi5yZWxvYWQoKTsKICAgICAgIH0KICAgICB9LAogICAgIHRydWUKICAgKTsKIH0KfQoKYXN5bmMgZnVuY3Rpb24gYm9vdCgpewogaGlkZUxlZ2FjeSgpOwogcG9ydGFsKCk7CiBpbnN0YWxsRm9ybXMoKTsKCiBzdGF0dXMoJ0NvbmVjdGFuZG8gY29uIGVsIHNlcnZpY2lvIGRlIGFjY2Vzb+KApicpOwoKIHRyeXsKICAgYXdhaXQgd2FpdENsaWVudCgpOwoKICAgc3RhdHVzKCdWZXJpZmljYW5kbyBzZXNpw7Nu4oCmJyk7CgogICBjb25zdCB7ZGF0YSxlcnJvcn09YXdhaXQgY2xpZW50LmF1dGguZ2V0U2Vzc2lvbigpOwoKICAgaWYoZXJyb3IpdGhyb3cgZXJyb3I7CgogICBpZihkYXRhPy5zZXNzaW9uKXsKICAgICBhd2FpdCBvcGVuQXBwKCk7CiAgIH1lbHNlewogICAgIHN0YXR1cygKICAgICAgICdTaW4gc2VzacOzbiBhY3RpdmEuIEluaWNpYSBzZXNpw7NuIG8gY3JlYSB1bmEgY3VlbnRhLicKICAgICApOwogICB9CgogfWNhdGNoKGUpewogICBjb25zb2xlLmVycm9yKCdbQ0NGIEFVVEhdIGJvb3QnLGUpOwoKICAgc3RhdHVzKAogICAgIGU/Lm1lc3NhZ2V8fAogICAgICdObyBmdWUgcG9zaWJsZSBpbmljaWFsaXphciBlbCBhY2Nlc28uJywKICAgICB0cnVlCiAgICk7CiB9Cn0KCmlmKGRvY3VtZW50LnJlYWR5U3RhdGU9PT0nbG9hZGluZycpewogZG9jdW1lbnQuYWRkRXZlbnRMaXN0ZW5lcigKICAgJ0RPTUNvbnRlbnRMb2FkZWQnLAogICBib290LAogICB7b25jZTp0cnVlfQogKTsKfWVsc2V7CiBib290KCk7Cn0KCn0pKCk7Cg=="

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this).apply {
            setBackgroundColor(Color.rgb(5, 11, 20))
        }

        setContentView(webView)
        configureWebView()

        // B3.1: the APK entry point is the REAL CCF login engine.
        // It never loads the production landing as its first document.
        startAtRealCCFLogin()
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
        s.userAgentString = s.userAgentString + " CCFAndroid/3.0"

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = handleUrl(request.url.toString())

            @Deprecated("Deprecated in Android API 24")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = handleUrl(url)

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                // After the REAL CCF login succeeds, the auth engine redirects
                // here. At that point the persisted Supabase session is already
                // available to the production application.
                if (url.startsWith(homeUrl)) {
                    applyMobileCompact()
                }
            }
        }
    }

    private fun startAtRealCCFLogin() {
        val authJs = String(
            Base64.decode(authJsBase64, Base64.DEFAULT),
            Charsets.UTF_8
        )

        val shell = """
            <!doctype html>
            <html lang="es">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
              <title>CCF · Iniciar sesión</title>
              <style>
                html,body{margin:0;width:100%;min-height:100%;background:#050b14;}
                body{font-family:system-ui,-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;}
              </style>
            </head>
            <body>
              <script>
                // The REAL CCF auth boot calls window.connect() after a valid
                // Supabase session. Only then do we enter the production app.
                window.connect = async function() {
                  window.location.replace("$homeUrl?android=ccf-authenticated");
                };
                window.refresh = async function() {};
              </script>
              <script>
                $authJs
              </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(
            homeUrl,
            shell,
            "text/html",
            "UTF-8",
            homeUrl
        )
    }

    private fun applyMobileCompact() {
        webView.evaluateJavascript(
            """
            (function(){
              var style=document.getElementById('ccf-android-compact-style');
              if(!style){
                style=document.createElement('style');
                style.id='ccf-android-compact-style';
                style.textContent=`
                  #app{zoom:.70 !important;width:95% !important;height:75vh !important;}
                  @supports not (zoom:.70){
                    #app{transform:scale(.70);transform-origin:top left;width:95% !important;height:75vh !important;}
                  }
                `;
                document.head.appendChild(style);
              }
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

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
