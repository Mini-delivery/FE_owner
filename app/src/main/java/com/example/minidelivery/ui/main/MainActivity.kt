package com.example.minidelivery.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.minidelivery.R
import com.example.minidelivery.data.OrderStatus

class MainActivity : AppCompatActivity() {
    // ViewModel 선언
    private lateinit var viewModel: MainViewModel

    // UI 컴포넌트 관리 클래스 선언
    private lateinit var uiComponents: MainUiState

    // 주문 상세 요청 코드
    val ORDER_DETAILS_REQUEST_CODE = 1001

    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // UI 컴포넌트 초기화
        uiComponents = MainUiState(this)

        // 뷰 초기화 및 리스너 설정
        uiComponents.initViews()
        uiComponents.setupListeners(viewModel)
        // ViewModel 관찰 설정
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        // 초기 데이터 로드
        viewModel.loadInitialData()
    }

    override fun onResume() {
        super.onResume()
        // 홈 메뉴 아이템 선택
        uiComponents.bottomNavigation.selectedItemId = R.id.nav_home
    }

    override fun onPause() {
        super.onPause()
        // 필요한 경우 상태 저장
    }

    override fun onStop() {
        super.onStop()
        // 필요한 경우 리소스 해제
    }

    override fun onDestroy() {
        super.onDestroy()
        // 리소스 정리
    }

    // ViewModel 관찰 설정
    private fun observeViewModel() {
        viewModel.currentOrder.observe(this) { order ->
            order?.let { uiComponents.updateOrderDetails(it) }
        }

        viewModel.orderCardVisibility.observe(this) { visibility ->
            uiComponents.orderCardView.visibility = if (visibility) View.VISIBLE else View.GONE
        }

        viewModel.acceptButtonState.observe(this) { state ->
            uiComponents.updateAcceptButtonState(state)
        }
    }

    // 주문 상세 화면에서 돌아왔을 때 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ORDER_DETAILS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newStatus = data?.getStringExtra("newStatus")?.let { OrderStatus.valueOf(it) }
            newStatus?.let { viewModel.updateOrderStatus(it) }
        }
    }

    // 완료된 주문 화면으로 이동
    fun navigateToCompletedOrders() {
        uiComponents.navigateToCompletedOrders()
    }

    // 배달 관리 화면으로 이동
    fun navigateToManageDelivery() {
        uiComponents.navigateToManageDelivery()
    }
}