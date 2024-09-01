package com.example.minidelivery.data

// 주문 데이터 클래스
data class Order(
    val id: String,         // 주문 ID
    val time: String,       // 주문 시간
    val summary: String,    // 주문 요약
    val address: String,    // 배달 주소
    val paymentStatus: String, // 결제 상태
    val price: String,      // 주문 가격
    var status: OrderStatus // 주문 상태
)

// 주문 상태 열거형
enum class OrderStatus {
    READY,      // 준비됨
    COOKING,    // 조리 중
    DELIVERING, // 배달 중
    COMPLETED   // 완료됨
}

data class OrderDetails(
    val summary: String,
    val address: String,
    val paymentStatus: String,
    val storeRequest: String,
    val deliveryRequest: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val menuName: String,
    val quantity: Int,
    val price: String
)
