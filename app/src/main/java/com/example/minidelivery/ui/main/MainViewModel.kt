package com.example.minidelivery.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.minidelivery.R
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderRepository
import com.example.minidelivery.data.OrderStatus
import com.google.android.material.tabs.TabLayout

class MainViewModel : ViewModel() {
    private val repository = OrderRepository.getInstance()

    private val _currentOrder = MutableLiveData<Order?>()
    val currentOrder: LiveData<Order?> = _currentOrder

    val orderCardVisibility = currentOrder.map { it != null }

    private val _acceptButtonState = MutableLiveData<AcceptButtonState>()
    val acceptButtonState: LiveData<AcceptButtonState> = _acceptButtonState

    private val _navigateToOrderDetails = MutableLiveData<Order?>()
    val navigateToOrderDetails: LiveData<Order?> = _navigateToOrderDetails

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        repository.loadInitialData()
        loadProcessingOrders()
    }

    private fun loadProcessingOrders() {
        val orders = repository.getProcessingOrders()
        if (orders.isNotEmpty()) {
            _currentOrder.value = orders.first()
            _acceptButtonState.value = AcceptButtonState("접수", R.color.processing_color)
        } else {
            _currentOrder.value = null
        }
    }

    private fun loadDeliveringOrders() {
        val orders = repository.getDeliveringOrders()
        if (orders.isNotEmpty()) {
            _currentOrder.value = orders.first()
            _acceptButtonState.value = AcceptButtonState("완료", R.color.delivering_color)
        } else {
            _currentOrder.value = null
        }
    }

    fun onAcceptButtonClick() {
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

    fun updateOrderStatus(newStatus: OrderStatus) {
        _currentOrder.value?.let { order ->
            order.status = newStatus
            repository.updateOrder(order)
            when (newStatus) {
                OrderStatus.COOKING -> _acceptButtonState.value =
                    AcceptButtonState("조리중", R.color.processing_color)
                OrderStatus.COOKED -> _acceptButtonState.value =
                    AcceptButtonState("조리완료", R.color.delivering_color)
                OrderStatus.COMPLETED -> {
                    _currentOrder.value = null
                    _acceptButtonState.value = AcceptButtonState("완료", R.color.completed_color)
                }
                else -> {}
            }
        }
    }

    fun onOrderDetailsNavigated() {
        _navigateToOrderDetails.value = null
    }

    val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> loadProcessingOrders()
                1 -> loadDeliveringOrders()
            }
        }
        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    data class AcceptButtonState(val text: String, val colorResId: Int)
}