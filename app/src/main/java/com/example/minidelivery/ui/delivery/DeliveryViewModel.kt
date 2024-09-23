package com.example.minidelivery.ui.delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderRepository
import com.example.minidelivery.data.OrderStatus

class DeliveryViewModel : ViewModel() {
    private val repository = OrderRepository.getInstance() // 리포지토리 인스턴스 가져오기

    private val _navigateToDone = MutableLiveData<Boolean>() // 완료 화면 이동 LiveData
    val navigateToDone: LiveData<Boolean> = _navigateToDone

    // 배달 완료 처리 함수
    fun finishDelivery() {
        val deliveringOrders = repository.getDeliveringOrders() // 배달 중인 주문 가져오기
        deliveringOrders.firstOrNull()?.let { order ->
            repository.updateOrder(order.copy(status = OrderStatus.COMPLETED)) // 주문 상태를 배달완료로 변경
            _navigateToDone.value = true // 완료 화면으로 이동 트리거
        }
    }

    fun completeDelivery(order: Order) {
        repository.updateOrder(order.copy(status = OrderStatus.COMPLETED))
        _navigateToDone.value = true
    }

    fun onNavigatedToDone() {
        _navigateToDone.value = false
    }
}