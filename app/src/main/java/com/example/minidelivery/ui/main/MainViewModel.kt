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

enum class SortOrder {
    LATEST, OLDEST
}

class MainViewModel : ViewModel() {
    private val repository = OrderRepository.getInstance()

    private val _currentOrder = MutableLiveData<Order?>()
    val currentOrder: LiveData<Order?> = _currentOrder

    val orderCardVisibility = currentOrder.map { it != null }

    private val _acceptButtonState = MutableLiveData<AcceptButtonState>()
    val acceptButtonState: LiveData<AcceptButtonState> = _acceptButtonState

    private var currentTab = 0
    private var sortOrder = SortOrder.LATEST

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        repository.loadInitialData()
        loadOrders()
    }

    private fun loadOrders() {
        val orders = when (currentTab) {
            0 -> repository.getProcessingOrders()
            1 -> repository.getCookingOrders()
            else -> emptyList()
        }

        val sortedOrders = when (sortOrder) {
            SortOrder.LATEST -> orders.sortedByDescending { it.time }
            SortOrder.OLDEST -> orders.sortedBy { it.time }
        }

        _currentOrder.value = sortedOrders.firstOrNull()
        updateAcceptButtonState()
    }


    fun onAcceptButtonClick() {
        _currentOrder.value?.let { order ->
            when (order.status) {
                OrderStatus.READY -> updateOrderStatus(OrderStatus.COOKING)
                OrderStatus.COOKING -> updateOrderStatus(OrderStatus.COOKED)
                OrderStatus.COOKED -> updateOrderStatus(OrderStatus.DELIVERING)
                OrderStatus.DELIVERING -> updateOrderStatus(OrderStatus.COMPLETED)
                OrderStatus.COMPLETED -> {} // 이미 완료된 주문은 추가 처리 없음
            }
        }
    }

    private fun updateOrderStatus(newStatus: OrderStatus) {
        _currentOrder.value?.let { order ->
            order.status = newStatus
            repository.updateOrder(order)
            updateAcceptButtonState()
            loadOrders()
        }
    }

    private fun updateAcceptButtonState() {
        _currentOrder.value?.let { order ->
            _acceptButtonState.value = when (order.status) {
                OrderStatus.READY -> AcceptButtonState("접수", R.color.processing_color)
                OrderStatus.COOKING -> AcceptButtonState("조리중", R.color.cooking_color)
                OrderStatus.COOKED -> AcceptButtonState("배달시작", R.color.cooked_color)
                OrderStatus.DELIVERING -> AcceptButtonState("배달완료", R.color.delivering_color)
                OrderStatus.COMPLETED -> AcceptButtonState("완료", R.color.completed_color)
            }
        }
    }

    fun setSortOrder(order: SortOrder) {
        sortOrder = order
        loadOrders()
    }


    val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            currentTab = tab?.position ?: 0
            loadOrders()
        }
        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    data class AcceptButtonState(val text: String, val colorResId: Int)
}