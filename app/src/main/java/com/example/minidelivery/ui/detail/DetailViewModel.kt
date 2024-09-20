package com.example.minidelivery.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderItem
import com.example.minidelivery.data.OrderRepository
import com.example.minidelivery.data.OrderStatus
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val repository = OrderRepository.getInstance()

    private val _selectedOrder = MutableLiveData<Order>()
    val selectedOrder: LiveData<Order> = _selectedOrder

    private val _orderList = MutableLiveData<List<Order>>()
    val orderList: LiveData<List<Order>> = _orderList

    private val _orderItems = MutableLiveData<List<OrderItem>>()
    val orderItems: LiveData<List<OrderItem>> = _orderItems

    private val _navigateToDelivery = MutableLiveData<Boolean>()
    val navigateToDelivery: LiveData<Boolean> = _navigateToDelivery

    fun loadOrderDetails(orderId: String?) {
        orderId?.let {
            val order = repository.getOrderById(it)
            order?.let {
                _selectedOrder.value = it
                _orderItems.value = it.items
            }
        }
    }

    fun loadOrderList() {
        viewModelScope.launch {
            try {
                val orders = repository.getAllOrders()
                _orderList.value = orders ?: emptyList()
            } catch (e: Exception) {
                // 에러 처리
                Log.e("DetailViewModel", "Error loading orders", e)
                _orderList.value = emptyList()
            }
        }
    }

    fun selectOrder(order: Order) {
        _selectedOrder.value = order
        _orderItems.value = order.items
    }

    fun updateOrderStatus(order: Order? = null) {
        val orderToUpdate = order ?: _selectedOrder.value ?: return
        val newStatus = when (orderToUpdate.status) {
            OrderStatus.READY -> OrderStatus.COOKING
            OrderStatus.COOKING -> OrderStatus.COOKED
            OrderStatus.COOKED -> OrderStatus.DELIVERING
            OrderStatus.DELIVERING -> OrderStatus.COMPLETED
            OrderStatus.COMPLETED -> return
        }

        val updatedOrder = orderToUpdate.copy(status = newStatus)
        repository.updateOrder(updatedOrder)
        _selectedOrder.value = updatedOrder

        if (newStatus == OrderStatus.DELIVERING) {
            _navigateToDelivery.value = true
        }

        loadOrderList() // Refresh the order list
    }
}