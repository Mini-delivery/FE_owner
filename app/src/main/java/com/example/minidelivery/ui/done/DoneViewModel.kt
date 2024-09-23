package com.example.minidelivery.ui.done


import android.app.Activity
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minidelivery.R
import com.example.minidelivery.data.Order
import com.example.minidelivery.ui.main.MainActivity
import com.example.minidelivery.ui.delivery.DeliveryActivity
import com.example.minidelivery.ui.main.SortOrder

class DoneViewModel : ViewModel() {
    // 완료된 주문 목록
    private val _completedOrders = MutableLiveData<List<Done>>(listOf())
    val completedOrders: LiveData<List<Done>> = _completedOrders

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    sealed class NavigationEvent {
        object ToHome : NavigationEvent()
        object ToDelivery : NavigationEvent()
    }

    private var sortOrder = SortOrder.LATEST

    enum class SortOrder {
        LATEST, OLDEST
    }

    fun setSortOrder(order: SortOrder) {
        sortOrder = order
        sortOrders()
    }

    private fun sortOrders() {
        val sortedList = when (sortOrder) {
            SortOrder.LATEST -> _completedOrders.value?.sortedByDescending { it.timestamp }
            SortOrder.OLDEST -> _completedOrders.value?.sortedBy { it.timestamp }
        }
        _completedOrders.value = sortedList ?: emptyList()
    }

    fun addCompletedOrder(order: Order) {
        val currentList = _completedOrders.value.orEmpty().toMutableList()
        val doneOrder = Done(
            summary = order.order_name,
            address = order.address,
            price = order.price.toString()
        )
        currentList.add(0, doneOrder)
        _completedOrders.value = currentList
    }

    fun navigateToHome() {
        _navigationEvent.value = NavigationEvent.ToHome
    }

    fun navigateToManageDelivery() {
        _navigationEvent.value = NavigationEvent.ToDelivery
    }
}