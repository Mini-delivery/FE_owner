package com.example.minidelivery.data

// API 응답 데이터 클래스
data class ResponseData(
    val id: Int,            // 주문 ID
    val orderTime: String,  // 주문 시간
    val foodName: String,   // 음식 이름
    val address: String,    // 주소
    val amount: String      // 금액
)