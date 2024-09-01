package com.example.minidelivery.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OrderRepository private constructor() {
    // 처리 중인 주문 목록
    private val processingOrders = mutableListOf<Order>()

    // 배달 중인 주문 목록
    private val deliveringOrders = mutableListOf<Order>()

    // 현재 주문 상태 플로우
    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder

    // 초기 데이터 로드
    fun loadInitialData() {
        processingOrders.add(
            Order(
                id = "1",
                time = "15:00",
                summary = "연어 샐러드 외 1개",
                address = "삼선동 SK뷰 아파트 1301동 1804호",
                paymentStatus = "결제완료",
                price = "21,200원",
                status = OrderStatus.READY
            )
        )
        _currentOrder.value = processingOrders.firstOrNull()
    }

    // 처리 중인 주문 목록 반환
    fun getProcessingOrders(): List<Order> = processingOrders

    // 배달 중인 주문 목록 반환
    fun getDeliveringOrders(): List<Order> = deliveringOrders

    // 주문 업데이트
    fun updateOrder(order: Order) {
        when (order.status) {
            OrderStatus.COOKING -> {
                // 조리 중 상태로 업데이트
                processingOrders.find { it.id == order.id }?.status = OrderStatus.COOKING
            }

            OrderStatus.DELIVERING -> {
                // 배달 중 상태로 업데이트
                processingOrders.removeIf { it.id == order.id }
                deliveringOrders.add(order)
            }

            OrderStatus.COMPLETED -> {
                // 완료 상태로 업데이트
                deliveringOrders.removeIf { it.id == order.id }
            }

            else -> {}
        }
        _currentOrder.value = order
    }

    // ID로 주문 찾기
    fun getOrderById(id: String): Order? {
        return processingOrders.find { it.id == id } ?: deliveringOrders.find { it.id == id }
    }

    // 싱글톤 패턴 구현
    companion object {
        @Volatile
        private var instance: OrderRepository? = null

        fun getInstance(): OrderRepository {
            return instance ?: synchronized(this) {
                instance ?: OrderRepository().also { instance = it }
            }
        }
    }
}