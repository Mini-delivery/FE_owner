package com.example.minidelivery.ui.main

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.example.minidelivery.R
import com.example.minidelivery.data.Order
import com.example.minidelivery.ui.completedorders.CompletedOrdersActivity
import com.example.minidelivery.ui.managedelivery.ManageDeliveryActivity
import com.example.minidelivery.ui.orderdetails.OrderDetailsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class MainUiState(private val activity: MainActivity) {
    // UI 컴포넌트 선언
    lateinit var acceptButton: Button
    lateinit var tabLayout: TabLayout
    lateinit var timeTextView: TextView
    lateinit var orderSummaryTextView: TextView
    lateinit var addressTextView: TextView
    lateinit var paymentStatusTextView: TextView
    lateinit var priceTextView: TextView
    lateinit var bottomNavigation: BottomNavigationView
    lateinit var orderCardView: View

    // 뷰 초기화
    fun initViews() {
        acceptButton = activity.findViewById(R.id.acceptButton)
        tabLayout = activity.findViewById(R.id.tabLayout)
        timeTextView = activity.findViewById(R.id.timeTextView)
        orderSummaryTextView = activity.findViewById(R.id.orderSummaryTextView)
        addressTextView = activity.findViewById(R.id.addressTextView)
        paymentStatusTextView = activity.findViewById(R.id.paymentStatusTextView)
        priceTextView = activity.findViewById(R.id.priceTextView)
        bottomNavigation = activity.findViewById(R.id.bottomNavigation)
        orderCardView = activity.findViewById(R.id.orderCardView)
    }

    // 리스너 설정
    fun setupListeners(viewModel: MainViewModel) {
        acceptButton.setOnClickListener { viewModel.handleAcceptButtonClick() }

        orderCardView.setOnClickListener {
            val intent = Intent(activity, OrderDetailsActivity::class.java)
            viewModel.currentOrder.value?.let {
                intent.putExtra("orderId", it.id)
                intent.putExtra("orderStatus", it.status.name)
            }
            activity.startActivityForResult(intent, activity.ORDER_DETAILS_REQUEST_CODE)
        }

        setupTabLayout(viewModel)
        setupBottomNavigation()
    }

    // 탭 레이아웃 설정
    private fun setupTabLayout(viewModel: MainViewModel) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.loadProcessingOrders()
                    1 -> viewModel.loadDeliveringOrders()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // 하단 네비게이션 설정
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    activity.navigateToCompletedOrders()
                    true
                }

                R.id.nav_delivery -> {
                    activity.navigateToManageDelivery()
                    true
                }

                R.id.nav_calendar -> {
                    // 일정 관리로 이동 (미구현)
                    true
                }

                else -> false
            }
        }
    }

    // 주문 상세 정보 업데이트
    fun updateOrderDetails(order: Order) {
        timeTextView.text = order.time
        orderSummaryTextView.text = order.summary
        addressTextView.text = order.address
        paymentStatusTextView.text = order.paymentStatus
        priceTextView.text = order.price
    }

    // 접수 버튼 상태 업데이트
    fun updateAcceptButtonState(state: MainViewModel.AcceptButtonState) {
        acceptButton.text = state.text
        acceptButton.setBackgroundColor(ContextCompat.getColor(activity, state.colorResId))
    }

    // 완료된 주문 화면으로 이동
    fun navigateToCompletedOrders() {
        val intent = Intent(activity, CompletedOrdersActivity::class.java)
        intent.putExtra("isNewOrder", false)
        ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.fade_in, R.anim.fade_out)
        activity.startActivity(intent)
    }

    // 배달 관리 화면으로 이동
    fun navigateToManageDelivery() {
        val intent = Intent(activity, ManageDeliveryActivity::class.java)
        ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.fade_in, R.anim.fade_out)
        activity.startActivity(intent)
    }
}