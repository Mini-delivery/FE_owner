package com.example.minidelivery.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String,
    val time: String,
    val summary: String,
    val address: String,
    val paymentStatus: String,
    val price: String,
    var status: OrderStatus
) : Parcelable

enum class OrderStatus {
    READY,
    COOKING,
    COOKED,
    DELIVERING,
    COMPLETED;
}

@Parcelize
data class OrderDetails(
    val summary: String,
    val address: String,
    val paymentStatus: String,
    val storeRequest: String,
    val deliveryRequest: String,
    val items: List<OrderItem>
) : Parcelable

@Parcelize
data class OrderItem(
    val menuName: String,
    val quantity: Int,
    val price: String
) : Parcelable