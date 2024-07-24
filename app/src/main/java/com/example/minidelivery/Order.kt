package com.example.minidelivery

data class Order(
    val id: String,
    val time: String,
    var summary: String,
    val address: String,
    var paymentStatus: String,
    val price: String,
    var status: OrderStatus
)