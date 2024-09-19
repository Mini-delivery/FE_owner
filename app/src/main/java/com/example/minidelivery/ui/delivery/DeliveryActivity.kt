package com.example.minidelivery.ui.delivery

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.minidelivery.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import okhttp3.*
import java.io.IOException // IOException import 추가

// MainActivity와 CompletedOrdersActivity의 패키지 경로에 맞게 import 추가
import com.example.minidelivery.ui.main.MainActivity
import com.example.minidelivery.ui.done.DoneActivity

class DeliveryActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var webView: WebView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var btnLeft: Button
    private lateinit var btnRight: Button
    private lateinit var btnGo: Button
    private lateinit var btnBack: Button
    private lateinit var btnStop: Button
    private lateinit var btnAuto: Button
    private lateinit var btnRefresh: Button

    private val client = OkHttpClient()
    private val handler = Handler(Looper.getMainLooper())
    private var isSendingCommand = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery)

        initViews()
        setupListeners()
        setupWebView()

        bottomNavigation.selectedItemId = R.id.nav_delivery
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        webView = findViewById(R.id.webview)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        btnLeft = findViewById(R.id.btn_turn_left)
        btnRight = findViewById(R.id.btn_turn_right)
        btnGo = findViewById(R.id.btn_go)
        btnBack = findViewById(R.id.btn_back)
        btnStop = findViewById(R.id.btn_stop)
        btnAuto = findViewById(R.id.btn_auto)
        btnRefresh = findViewById(R.id.btn_refresh)
    }

    private fun setupListeners() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navigateToHome()
                    true
                }
                R.id.nav_done -> {
                    navigateToCompletedOrders()
                    true
                }
                R.id.nav_delivery -> true
                else -> false
            }
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        btnRefresh.setOnClickListener {
            webView.reload() // 웹뷰 새로고침
        }

        btnStop.setOnClickListener {
            sendCommand("stop", "Stop command sent")
        }

        btnAuto.setOnClickListener {
            sendCommand("auto", "Auto mode activated")
        }

        // Go, Back, Left, Right 터치 리스너 설정
        setupTouchListener(btnGo, "go")
        setupTouchListener(btnBack, "back")
        setupTouchListener(btnLeft, "left")
        setupTouchListener(btnRight, "right")
    }

    private fun setupTouchListener(button: Button, command: String) {
        button.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isSendingCommand) {
                        isSendingCommand = true
                        handler.post(sendCommandRepeatedly(command))
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isSendingCommand = false
                    handler.removeCallbacksAndMessages(null)
                    true
                }
                else -> false
            }
        }
    }

    private fun sendCommandRepeatedly(command: String): Runnable {
        return object : Runnable {
            override fun run() {
                sendCommand(command, "$command command sent")
                if (isSendingCommand) {
                    handler.postDelayed(this, 500) // 500ms마다 명령 전송
                }
            }
        }
    }

    private fun setupWebView() {
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        val streamingUrl = "http://192.168.137.237:5000"
        webView.loadUrl(streamingUrl)
    }

    private fun sendCommand(command: String, successMessage: String) {
        val url = "http://192.168.137.34:5000/control"
        val json = """
            {"command": "$command"}
        """.trimIndent()

        val body = RequestBody.create(MediaType.parse("application/json"), json)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@DeliveryActivity, "명령 전송 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    Toast.makeText(this@DeliveryActivity, successMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(this,
            R.anim.fade_in,
            R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun navigateToCompletedOrders() {
        val intent = Intent(this, DoneActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(this,
            R.anim.fade_in,
            R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
    }
}
