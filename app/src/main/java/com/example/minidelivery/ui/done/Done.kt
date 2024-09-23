package com.example.minidelivery.ui.done

// 완료된 주문 데이터 클래스
data class Done(
    val summary: String,
    val address: String,
    val price: String,
    val timestamp: Long = System.currentTimeMillis() // 정렬을 위한 타임스탬프 추가
)