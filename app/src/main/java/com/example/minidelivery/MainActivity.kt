package com.example.minidelivery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var orderButton: Button
    private var orderStatus: OrderStatus = OrderStatus.READY
    private var cookingTime: Int = 15
    private var deliveryMethod: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        orderButton = findViewById(R.id.orderButton)
        orderButton.setOnClickListener { handleOrderButtonClick() }
    }

    private fun handleOrderButtonClick() {
        when (orderStatus) {
            OrderStatus.READY -> DialogManager.showCookingTimeDialog(this) { time ->
                cookingTime = time
                DialogManager.showDeliveryMethodDialog(this) { method ->
                    deliveryMethod = method
                    updateOrderStatus(OrderStatus.COOKING)
                }
            }
            OrderStatus.COOKING -> {
                DialogManager.showDeliveryMethodDialog(this) { method ->
                    deliveryMethod = method
                    updateOrderStatus(OrderStatus.DELIVERING)
                }
            }
            OrderStatus.DELIVERING -> {
                startActivity(Intent(this, OrderDetailsActivity::class.java).apply {
                    putExtra("cookingTime", cookingTime)
                    putExtra("deliveryMethod", deliveryMethod)
                })
            }
            OrderStatus.COMPLETED -> {
                // Reset the order
                updateOrderStatus(OrderStatus.READY)
            }
        }
    }

    private fun updateOrderStatus(newStatus: OrderStatus) {
        orderStatus = newStatus
        orderButton.text = when (newStatus) {
            OrderStatus.READY -> "접수"
            OrderStatus.COOKING -> "조리완료"
            OrderStatus.DELIVERING -> "배달중"
            OrderStatus.COMPLETED -> "배달완료"
        }
    }
}