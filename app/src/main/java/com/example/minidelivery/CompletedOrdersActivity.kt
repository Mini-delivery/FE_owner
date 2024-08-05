package com.example.minidelivery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup

class CompletedOrdersActivity : AppCompatActivity() {
    // 뷰 요소들 선언
    private lateinit var chipGroup: ChipGroup
    private lateinit var orderSummaryTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var completedButton: Button
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_orders) // 레이아웃 설정

        initViews() // 뷰 초기화
        setupListeners() // 리스너 설정
        loadCompletedOrders() // 완료된 주문 로드

        bottomNavigation.selectedItemId = R.id.nav_history // 현재 화면에 해당하는 메뉴 아이템 선택

        // 뒤로가기 처리
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHome() // 홈으로 이동
            }
        })
    }

    private fun initViews() {
        // 뷰 요소들 초기화
        chipGroup = findViewById(R.id.chipGroup)
        orderSummaryTextView = findViewById(R.id.orderSummaryTextView)
        addressTextView = findViewById(R.id.addressTextView)
        priceTextView = findViewById(R.id.priceTextView)
        completedButton = findViewById(R.id.completedButton)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun navigateToManageDelivery() {
        val intent = Intent(this, ManageDeliveryActivity::class.java)
        ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent)
    }

    private fun setupListeners() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navigateToHome() // 홈으로 이동
                    true
                }
                R.id.nav_history -> {
                    // 현재 화면이므로 아무 동작 안 함
                    true
                }
                R.id.nav_delivery -> {
                    navigateToManageDelivery() // 배달 관리로 이동
                    true
                }
                R.id.nav_calendar -> {
                    // 일정 관리 화면으로 이동 (미구현)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadCompletedOrders() {
        // 실제로는 DB나 API에서 완료된 주문을 로드해야 함
        // 여기서는 예시 데이터만 설정
        orderSummaryTextView.setText(getString(R.string.order_summary))
        addressTextView.setText(getString(R.string.order_address))
        priceTextView.setText(getString(R.string.order_price, "21,200"))
    }
}