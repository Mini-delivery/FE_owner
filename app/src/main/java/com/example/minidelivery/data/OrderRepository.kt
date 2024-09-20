package com.example.minidelivery.data

class OrderRepository private constructor() {
    private val orders = mutableListOf<Order>() // 모든 주문을 저장할 리스트

    // 주문 추가 함수
    fun addOrder(order: Order) {
        orders.add(order) // 주문 리스트에 새 주문 추가
    }

    // 모든 주문 목록 반환 함수
    fun getAllOrders(): List<Order> = orders.toList()

    // 처리 중인 주문 목록 반환 함수
    fun getProcessingOrders(): List<Order> =
        orders.filter { it.status == OrderStatus.READY || it.status == OrderStatus.COOKING }

    // 조리 중인 주문 목록 반환 함수
    fun getCookingOrders(): List<Order> = orders.filter { it.status == OrderStatus.COOKING }

    // 배달 중인 주문 목록 반환 함수
    fun getDeliveringOrders(): List<Order> = orders.filter { it.status == OrderStatus.DELIVERING }

    // 완료된 주문 목록 반환 함수
    fun getCompletedOrders(): List<Order> = orders.filter { it.status == OrderStatus.COMPLETED }

    // 주문 업데이트 함수
    fun updateOrder(updatedOrder: Order) {
        val index = orders.indexOfFirst { it.id == updatedOrder.id }
        if (index != -1) {
            orders[index] = updatedOrder
        }
    }

    // ID로 주문 찾기 함수
    fun getOrderById(id: String): Order? = orders.find { it.id == id }

    companion object {
        @Volatile
        private var instance: OrderRepository? = null

        // 싱글톤 인스턴스 반환 함수
        fun getInstance(): OrderRepository {
            return instance ?: synchronized(this) {
                instance ?: OrderRepository().also { instance = it }
            }
        }
    }
}