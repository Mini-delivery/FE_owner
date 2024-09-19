package com.example.minidelivery.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minidelivery.data.OrderDetails
import com.example.minidelivery.data.OrderItem
import com.example.minidelivery.data.OrderRepository
import com.example.minidelivery.data.OrderStatus

class DetailViewModel : ViewModel() {
    private val repository = OrderRepository.getInstance() // 리포지토리 인스턴스 가져오기

    private val _orderDetails = MutableLiveData<OrderDetails>() // 주문 상세 정보 LiveData
    val orderDetails: LiveData<OrderDetails> = _orderDetails

    private val _orderStatus = MutableLiveData<OrderStatus>() // 주문 상태 LiveData
    val orderStatus: LiveData<OrderStatus> = _orderStatus

    private val _navigateToDelivery = MutableLiveData<Boolean>() // 배달 화면 이동 LiveData
    val navigateToDelivery: LiveData<Boolean> = _navigateToDelivery

    private var currentOrderId: String? = null // 현재 주문 ID를 저장할 변수 추가

    fun loadOrderDetails(orderId: String?, orderStatusString: String?) {
        currentOrderId = orderId // 현재 주문 ID 저장

        // 실제 데이터를 로드하는 로직으로 변경⭐️
        val order = repository.getOrderById(orderId ?: "") // 주문 ID로 주문 정보 가져오기
        order?.let {
            _orderDetails.value = OrderDetails(
                summary = it.summary,
                address = it.address,
                paymentStatus = it.paymentStatus,
                storeRequest = "리뷰이벤트 참여합니다~", // 실제 데이터로 대체 필요
                deliveryRequest = "문 앞에 놓아주세요!", // 실제 데이터로 대체 필요
                items = listOf(
                    OrderItem("연어 샐러드", 1, "12,000원"),
                    OrderItem("우삼겹 샐러드", 1, "10,200원")
                ) // 실제 주문 항목 데이터로 대체 필요
            )
            _orderStatus.value = it.status // 주문 상태 설정
        }
    }

    fun updateOrderStatus() {
        val currentStatus = _orderStatus.value ?: return
        val newStatus = when (currentStatus) {
            OrderStatus.READY -> OrderStatus.COOKING
            OrderStatus.COOKING -> OrderStatus.DELIVERING
            OrderStatus.DELIVERING -> OrderStatus.COMPLETED
            OrderStatus.COMPLETED -> OrderStatus.COMPLETED
            else -> OrderStatus.READY // COOKED 상태 추가
        }
        _orderStatus.value = newStatus // 새 상태 설정

        if (newStatus == OrderStatus.DELIVERING) {
            _navigateToDelivery.value = true // 배달 화면으로 이동 트리거
        }

        // 리포지토리에 주문 상태 업데이트
        currentOrderId?.let { id ->
            repository.getOrderById(id)?.let { order ->
                repository.updateOrder(order.copy(status = newStatus))
            }
        }
    }

    fun onBackButtonClicked(activity: DetailActivity) {
        _orderStatus.value?.let { status ->
            activity.finishWithResult(status) // 결과 설정 및 액티비티 종료
        }
    }
}