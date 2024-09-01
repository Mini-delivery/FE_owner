package com.example.minidelivery.ui.completedorders

// 완료된 주문 데이터 클래스
data class CompletedOrder(
    val summary: String, // 주문 요약
    val address: String, // 배달 주소
    val price: String    // 주문 가격
)