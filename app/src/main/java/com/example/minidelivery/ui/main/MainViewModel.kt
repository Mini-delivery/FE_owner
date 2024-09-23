package com.example.minidelivery.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderItem
import com.example.minidelivery.data.OrderRepository
import com.example.minidelivery.data.OrderStatus
import com.example.minidelivery.mqtt.MqttManager
import com.google.android.material.tabs.TabLayout

enum class SortOrder {
    LATEST, OLDEST // 정렬 순서 열거형
}

class MainViewModel(private val mqttManager: MqttManager) : ViewModel() {
    private val repository = OrderRepository.getInstance()

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    private val _navigateToDelivery = MutableLiveData<Order?>()
    val navigateToDelivery: LiveData<Order?> = _navigateToDelivery

    private val _navigateToDone = MutableLiveData<Order?>()
    val navigateToDone: LiveData<Order?> = _navigateToDone

    init {
        loadOrders()
    }

    // 주문 목록 로드 함수
    fun loadOrders() {
        val allOrders = repository.getAllOrders()
        _orders.value = allOrders.filter { it.status != OrderStatus.COMPLETED }
    }


    fun updateOrderStatus(order: Order) {
        val newStatus = when (order.status) {
            OrderStatus.READY -> OrderStatus.COOKING
            OrderStatus.COOKING -> OrderStatus.COOKED
            OrderStatus.COOKED -> OrderStatus.DELIVERING
            OrderStatus.DELIVERING -> OrderStatus.COMPLETED
            OrderStatus.COMPLETED -> OrderStatus.DONE
            OrderStatus.DONE -> OrderStatus.DONE
        }

        val updatedOrder = order.copy(status = newStatus)
        repository.updateOrder(updatedOrder)

        val currentOrders = _orders.value.orEmpty().toMutableList()
        val index = currentOrders.indexOfFirst { it.id == updatedOrder.id }
        if (index != -1) {
            when (newStatus) {
                OrderStatus.COMPLETED -> {
                    _navigateToDelivery.value = updatedOrder  // Delivery Activity로 이동 트리거
                }
                OrderStatus.DONE -> {
                    currentOrders.removeAt(index)
                    _navigateToDone.value = updatedOrder  // Done Activity로 이동 트리거
                }
                else -> {
                    currentOrders[index] = updatedOrder
                }
            }
            _orders.value = currentOrders
        }
    }

    fun onDeliveryNavigated() {
        _navigateToDelivery.value = null
    }

    fun completeDelivery(order: Order) {
        updateOrderStatus(order.copy(status = OrderStatus.COMPLETED))
    }

    fun onDoneNavigated() {
        _navigateToDone.value = null
    }

    // 주문 추가 함수
    fun addOrder(newOrder: Order?) {
        if (newOrder != null) {
            val currentOrders = _orders.value.orEmpty().toMutableList()
            if (currentOrders.none { it.id == newOrder.id }) {
                currentOrders.add(newOrder)
                _orders.value = currentOrders
            }
        }
    }
}