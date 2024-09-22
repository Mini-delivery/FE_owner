package com.example.minidelivery.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderItem
import com.example.minidelivery.data.OrderRepository
import com.example.minidelivery.data.OrderStatus
import com.example.minidelivery.mqtt.MqttManager
import com.google.android.material.tabs.TabLayout

enum class SortOrder {
    LATEST, OLDEST // 정렬 순서 열거형
}

class MainViewModel(private val mqttManager: MqttManager) : ViewModel() {

    private val repository = OrderRepository.getInstance() // 리포지토리 인스턴스 가져오기

    private val _orders = MutableLiveData<List<Order>>() // 주문 목록 MutableLiveData
    val orders: LiveData<List<Order>> = _orders // 주문 목록 LiveData

    private val _navigateToDelivery = MutableLiveData<Boolean>() // 배달 화면 이동 MutableLiveData
    val navigateToDelivery: LiveData<Boolean> = _navigateToDelivery // 배달 화면 이동 LiveData

    private var currentTab = 0 // 현재 선택된 탭
    private var sortOrder = SortOrder.LATEST // 현재 정렬 순서

    init {

    }
    // 주문 추가 함수
    fun addOrder(newOrder: Order?) {
        val updatedOrders = _orders.value.orEmpty().toMutableList()
        if (newOrder != null && updatedOrders.none { it.id == newOrder.id }) {
            updatedOrders.add(newOrder)
        }
        _orders.value = updatedOrders
    }

    fun onTabSelected(position: Int) {
        currentTab = position
        loadOrders()
    }

    // 주문 목록 로드 함수
    fun loadOrders() {
        val orders = when (currentTab) {
            0 -> repository.getProcessingOrders() // 처리 중인 주문 가져오기
            1 -> repository.getCookingOrders() // 조리 중인 주문 가져오기
            else -> emptyList()
        }

        val sortedOrders = when (sortOrder) {
            SortOrder.LATEST -> orders.sortedByDescending { it.order_time } // 최신순 정렬
            SortOrder.OLDEST -> orders.sortedBy { it.order_time } // 과거순 정렬
        }

        _orders.value = sortedOrders // 정렬된 주문 목록 설정
    }

    // 주문 상태 업데이트 함수
    fun updateOrderStatus(order: Order) {
        val newStatus = when (order.status) {
            OrderStatus.READY -> OrderStatus.COOKING
            OrderStatus.COOKING -> OrderStatus.COOKED
            OrderStatus.COOKED -> OrderStatus.DELIVERING
            OrderStatus.DELIVERING -> OrderStatus.COMPLETED
            OrderStatus.COMPLETED -> OrderStatus.COMPLETED
        }

        Log.d("ViewModel", "Updating status to: $newStatus for order: ${order.id}")

        val updatedOrder = order.copy(status = newStatus) // 새로운 상태로 주문 복사

        // 주문 목록에서 해당 주문을 찾아 업데이트
        val updatedOrders = _orders.value.orEmpty().map {
            if (it.id == updatedOrder.id) updatedOrder else it
        }

        _orders.value = updatedOrders // 변경된 목록을 LiveData에 반영

        // 필요시 repository 업데이트
        repository.updateOrder(updatedOrder)

        if (newStatus == OrderStatus.DELIVERING) {
            Log.d("ViewModel", "Navigating to delivery screen")
            _navigateToDelivery.value = true // 배달 화면으로 이동 트리거
        }
    }


    // 정렬 순서 설정 함수
    fun setSortOrder(order: SortOrder) {
        sortOrder = order // 정렬 순서 설정
        loadOrders() // 주문 목록 다시 로드
    }

    // 탭 선택 리스너
    val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            currentTab = tab?.position ?: 0 // 현재 탭 업데이트
            loadOrders() // 주문 목록 다시 로드
        }
        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    // 배달 화면 이동 후 처리 함수
    fun onDeliveryNavigated() {
        _navigateToDelivery.value = false // 배달 화면 이동 후 플래그 리셋
    }
}