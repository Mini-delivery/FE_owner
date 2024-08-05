package com.example.minidelivery

import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class ManageDeliveryActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout // 탭 레이아웃 선언
    private lateinit var webView: WebView // 웹뷰 선언
    private lateinit var bottomNavigation: BottomNavigationView // 하단 네비게이션 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 부모 클래스 onCreate 호출
        setContentView(R.layout.activity_manage_delivery) // 레이아웃 설정

        initViews() // 뷰 초기화
        setupListeners() // 리스너 설정
        setupWebView() // 웹뷰 설정

        bottomNavigation.selectedItemId = R.id.nav_delivery // 현재 화면에 해당하는 메뉴 아이템 선택


        // 라즈베리파이 실시간 송출
        // WebView 설정
        webView = findViewById(R.id.webview)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // 필요에 따라 JavaScript 허용

        // 스트리밍 URL 설정 (예: http://<your_ip>:8000/stream.mjpg)
        val streamingUrl = "http://192.168.137.36:8000/index.html"
        webView.loadUrl(streamingUrl)


        // 뒤로가기 처리
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHome() // 홈으로 이동
            }
        })
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout) // 탭 레이아웃 초기화
        webView = findViewById(R.id.webview) // 웹뷰 초기화
        bottomNavigation = findViewById(R.id.bottomNavigation) // 하단 네비게이션 초기화
    }

    private fun setupListeners() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navigateToHome() // 홈으로 이동
                    true
                }
                R.id.nav_history -> {
                    navigateToCompletedOrders() // 완료된 주문으로 이동
                    true
                }
                R.id.nav_delivery -> {
                    // 이미 현재 화면이므로 아무 동작 안 함
                    true
                }
                R.id.nav_calendar -> {
                    // 일정관리 화면으로 이동 (미구현)
                    true
                }
                else -> false
            }
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // 탭 선택 시 동작 (현재는 "배달중" 탭만 있으므로 추가 동작 없음)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupWebView() {
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // JavaScript 활성화

        val streamingUrl = "http://192.168.137.36:8000/index.html" // 스트리밍 URL 설정
        webView.loadUrl(streamingUrl) // URL 로드
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun navigateToCompletedOrders() {
        val intent = Intent(this, CompletedOrdersActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent, options.toBundle())
    }
}