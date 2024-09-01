package com.example.minidelivery.ui.orderdetails

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minidelivery.data.OrderDetails
import com.example.minidelivery.data.OrderItem
import com.example.minidelivery.data.OrderStatus

class OrderDetailsViewModel : ViewModel() {
    private val _orderDetails = MutableLiveData<OrderDetails>()
    val orderDetails: LiveData<OrderDetails> = _orderDetails

    private val _orderStatus = MutableLiveData<OrderStatus>()
    val orderStatus: LiveData<OrderStatus> = _orderStatus

    private val _navigateToCompletedOrders = MutableLiveData<Boolean>()
    val navigateToCompletedOrders: LiveData<Boolean> = _navigateToCompletedOrders

    fun loadOrderDetails(orderId: String?, orderStatusString: String?) {
        // 여기서 실제 데이터를 로드하는 로직을 구현해야 합니다.
        // 현재는 더미 데이터를 사용합니다.
        _orderDetails.value = OrderDetails(
            summary = "연어 샐러드 외 1개",
            address = "삼선동 SK뷰 아파트 1301동 1804호",
            paymentStatus = "결제완료 21,200원",
            storeRequest = "리뷰이벤트 참여합니다~",
            deliveryRequest = "문 앞에 놓아주세요!",
            items = listOf(
                OrderItem("연어 샐러드", 1, "12,000원"),
                OrderItem("우삼겹 샐러드", 1, "10,200원")
            )
        )
        _orderStatus.value = orderStatusString?.let { OrderStatus.valueOf(it) } ?: OrderStatus.READY
    }

    fun updateOrderStatus() {
        val currentStatus = _orderStatus.value ?: return
        val newStatus = when (currentStatus) {
            OrderStatus.READY -> OrderStatus.COOKING
            OrderStatus.COOKING -> OrderStatus.DELIVERING
            OrderStatus.DELIVERING -> OrderStatus.COMPLETED
            OrderStatus.COMPLETED -> OrderStatus.COMPLETED
        }
        _orderStatus.value = newStatus

        if (newStatus == OrderStatus.COMPLETED) {
            _navigateToCompletedOrders.value = true
        }
    }

    fun onBackButtonClicked(activity: Activity) {
        val resultIntent = Intent().putExtra("newStatus", _orderStatus.value?.name)
        activity.setResult(Activity.RESULT_OK, resultIntent)
        activity.finish()
    }
}