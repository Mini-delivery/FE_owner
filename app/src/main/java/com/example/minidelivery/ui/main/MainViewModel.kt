package com.example.minidelivery.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minidelivery.R
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderRepository
import com.example.minidelivery.data.OrderStatus

class MainViewModel : ViewModel() {
    // 주문 저장소
    private val repository = OrderRepository.getInstance()

    // 현재 주문 LiveData
    private val _currentOrder = MutableLiveData<Order>()
    val currentOrder: LiveData<Order> = _currentOrder

    // 주문 카드 가시성 LiveData
    private val _orderCardVisibility = MutableLiveData<Boolean>()
    val orderCardVisibility: LiveData<Boolean> = _orderCardVisibility

    // 접수 버튼 상태 LiveData
    private val _acceptButtonState = MutableLiveData<AcceptButtonState>()
    val acceptButtonState: LiveData<AcceptButtonState> = _acceptButtonState

    // 초기 데이터 로드
    fun loadInitialData() {
        repository.loadInitialData()
        loadProcessingOrders()
    }

    // 처리 중인 주문 로드
    fun loadProcessingOrders() {
        val orders = repository.getProcessingOrders()
        if (orders.isNotEmpty()) {
            _currentOrder.value = orders.first()
            _orderCardVisibility.value = true
            _acceptButtonState.value = AcceptButtonState("접수", R.color.processing_color)
        } else {
            _orderCardVisibility.value = false
        }
    }

    // 배달 중인 주문 로드
    fun loadDeliveringOrders() {
        val orders = repository.getDeliveringOrders()
        if (orders.isNotEmpty()) {
            _currentOrder.value = orders.first()
            _orderCardVisibility.value = true
            _acceptButtonState.value = AcceptButtonState("완료", R.color.delivering_color)
        } else {
            _orderCardVisibility.value = false
        }
    }

    // 접수 버튼 클릭 처리
    fun handleAcceptButtonClick() {
        _currentOrder.value?.let { order ->
            when (order.status) {
                OrderStatus.READY -> updateOrderStatus(OrderStatus.COOKING)
                OrderStatus.COOKING -> {
                    updateOrderStatus(OrderStatus.DELIVERING)
                    loadProcessingOrders()
                }

                OrderStatus.DELIVERING -> {
                    updateOrderStatus(OrderStatus.COMPLETED)
                    loadDeliveringOrders()
                }

                else -> {}
            }
        }
    }

    // 주문 상태 업데이트
    fun updateOrderStatus(newStatus: OrderStatus) {
        _currentOrder.value?.let { order ->
            order.status = newStatus
            repository.updateOrder(order)
            when (newStatus) {
                OrderStatus.COOKING -> _acceptButtonState.value =
                    AcceptButtonState("조리완료", R.color.processing_color)

                OrderStatus.DELIVERING -> _acceptButtonState.value =
                    AcceptButtonState("배달중", R.color.delivering_color)

                OrderStatus.COMPLETED -> {
                    _orderCardVisibility.value = false
                    _acceptButtonState.value = AcceptButtonState("완료", R.color.completed_color)
                }

                else -> {}
            }
        }
    }

    // 접수 버튼 상태 데이터 클래스
    data class AcceptButtonState(val text: String, val colorResId: Int)
}