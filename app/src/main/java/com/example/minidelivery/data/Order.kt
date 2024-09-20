package com.example.minidelivery.data // 패키지 선언

import android.os.Parcelable // Parcelable 인터페이스 import
import kotlinx.parcelize.Parcelize // Parcelize 어노테이션 import

@Parcelize // Parcelable 구현을 자동으로 생성
data class Order( // 주문 데이터 클래스 정의
    val id: String, // 주문 ID
    val time: String, // 주문 시간
    val summary: String, // 주문 요약
    val address: String, // 배달 주소
    val paymentStatus: String, // 결제 상태
    val price: String, // 주문 가격
    var status: OrderStatus, // 주문 상태 (변경 가능)
    val storeRequest: String, // 가게 요청사항 추가
    val deliveryRequest: String, // 배달 요청사항 추가
    val items: List<OrderItem> // 주문 항목 리스트 추가
) : Parcelable // Parcelable 인터페이스 구현

enum class OrderStatus { // 주문 상태 열거형 정의
    READY,      // 접수 대기
    COOKING,    // 조리중
    COOKED,     // 조리완료
    DELIVERING, // 배달중
    COMPLETED   // 배달완료
}

@Parcelize // Parcelable 구현을 자동으로 생성
data class OrderDetails( // 주문 상세 정보 데이터 클래스 정의
    val summary: String, // 주문 요약
    val address: String, // 배달 주소
    val paymentStatus: String, // 결제 상태
    val storeRequest: String, // 가게 요청사항
    val deliveryRequest: String, // 배달 요청사항
    val items: List<OrderItem> // 주문 항목 리스트
) : Parcelable // Parcelable 인터페이스 구현

@Parcelize // Parcelable 구현을 자동으로 생성
data class OrderItem( // 주문 항목 데이터 클래스 정의
    val menuName: String, // 메뉴 이름
    val quantity: Int, // 수량
    val price: String // 가격
) : Parcelable // Parcelable 인터페이스 구현