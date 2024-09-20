package com.example.minidelivery.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderItem
import com.example.minidelivery.data.OrderRepository
import com.example.minidelivery.data.OrderStatus
import com.google.android.material.tabs.TabLayout

enum class SortOrder {
    LATEST, OLDEST // 정렬 순서 열거형
}

class MainViewModel : ViewModel() {
    private val repository = OrderRepository.getInstance() // 리포지토리 인스턴스 가져오기

    private val _orders = MutableLiveData<List<Order>>() // 주문 목록 MutableLiveData
    val orders: LiveData<List<Order>> = _orders // 주문 목록 LiveData

    private val _navigateToDelivery = MutableLiveData<Boolean>() // 배달 화면 이동 MutableLiveData
    val navigateToDelivery: LiveData<Boolean> = _navigateToDelivery // 배달 화면 이동 LiveData

    private var currentTab = 0 // 현재 선택된 탭
    private var sortOrder = SortOrder.LATEST // 현재 정렬 순서

    init {
        loadInitialData() // 초기 데이터 로드
    }

    // 초기 데이터 로드 함수
    private fun loadInitialData() {
        // 임의의 주문 데이터 생성
        val dummyOrders = listOf(
            Order(
                id = "1", // 주문 ID
                time = "오후 12시 18분", // 주문 시간
                summary = "스타벅스 | 아메리카노 외 3개", // 주문 요약
                address = "삼선동 SK뷰 아파트 1301동 1804호", // 배달 주소
                paymentStatus = "결제완료", // 결제 상태
                price = "21,200원", // 주문 가격
                status = OrderStatus.READY, // 주문 상태
                storeRequest = "리뷰이벤트 참여합니다!", // 가게 요청사항
                deliveryRequest = "문 앞에 놓아주세요!", // 배달 요청사항
                items = listOf( // 주문 항목 리스트
                    OrderItem("Iced Caffe Americano", 1, "4,500원"),
                    OrderItem("Iced Black Glazed Latte", 1, "6,700원"),
                    OrderItem("Lavender Cafe Breve", 1, "7,000원"),
                    OrderItem("Mango Passion Tea Blended", 1, "6,800원")
                )
            ),
            Order(
                id = "2", // 주문 ID
                time = "오후 2시 47분", // 주문 시간
                summary = "스타벅스 | 아메리카노 외 3개", // 주문 요약
                address = "삼선동 SK뷰 아파트 1301동 1804호", // 배달 주소
                paymentStatus = "결제완료", // 결제 상태
                price = "21,200원", // 주문 가격
                status = OrderStatus.READY, // 주문 상태
                storeRequest = "리뷰이벤트 참여합니다!", // 가게 요청사항
                deliveryRequest = "문 앞에 놓아주세요!", // 배달 요청사항
                items = listOf( // 주문 항목 리스트
                    OrderItem("Iced Caffe Americano", 1, "4,500원"),
                    OrderItem("Iced Black Glazed Latte", 1, "6,700원"),
                    OrderItem("Lavender Cafe Breve", 1, "7,000원"),
                    OrderItem("Mango Passion Tea Blended", 1, "6,800원")
                )
            )
        )

        // 리포지토리에 더미 데이터 추가
        dummyOrders.forEach { repository.addOrder(it) }

        // 주문 목록 업데이트
        _orders.value = dummyOrders
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
            SortOrder.LATEST -> orders.sortedByDescending { it.time } // 최신순 정렬
            SortOrder.OLDEST -> orders.sortedBy { it.time } // 과거순 정렬
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

        val updatedOrder = order.copy(status = newStatus) // 새로운 상태로 주문 복사
        repository.updateOrder(updatedOrder) // 리포지토리 업데이트
        loadOrders() // 주문 목록 다시 로드

        if (newStatus == OrderStatus.DELIVERING) {
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