package com.example.minidelivery.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OrderRepository private constructor() {

    // 처리 중인 주문 목록
    private val processingOrders = mutableListOf<Order>()
    // 조리 중인 주문 목록
    private val cookingOrders = mutableListOf<Order>()
    // 조리 완료된 주문 목록
    private val cookedOrders = mutableListOf<Order>()
    // 배달 중인 주문 목록
    private val deliveringOrders = mutableListOf<Order>()
    // 배달 완료된 주문 목록
    private val completedOrders = mutableListOf<Order>()
    // 취소된 주문 목록
    private val cancelledOrders = mutableListOf<Order>()


    // 초기 데이터 로드
    fun loadInitialData() {
        // 초기 데이터 로드 (더미 데이터)
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
    }


    // 처리 중인 주문 목록 반환
    fun getProcessingOrders(): List<Order> = processingOrders
    // 조리 중인 주문 목록 반환
    fun getCookingOrders(): List<Order> = cookingOrders
    // 조리 완료된 주문 목록 반환
    fun getCookedOrders(): List<Order> = cookedOrders
    // 배달 중인 주문 목록 반환
    fun getDeliveringOrders(): List<Order> = deliveringOrders
    // 배달 완료된 주문 목록 반환
    fun getCompletedOrders(): List<Order> = completedOrders



    // 주문 업데이트
    fun updateOrder(order: Order) {
        when (order.status) {
            OrderStatus.COOKING -> {
                processingOrders.removeIf { it.id == order.id }
                cookingOrders.add(order)
            }
            OrderStatus.COOKED -> {
                cookingOrders.removeIf { it.id == order.id }
                cookedOrders.add(order)
            }
            OrderStatus.DELIVERING -> {
                cookedOrders.removeIf { it.id == order.id }
                deliveringOrders.add(order)
            }
            OrderStatus.COMPLETED -> {
                deliveringOrders.removeIf { it.id == order.id }
                completedOrders.add(order)
            }
            else -> {}
        }
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