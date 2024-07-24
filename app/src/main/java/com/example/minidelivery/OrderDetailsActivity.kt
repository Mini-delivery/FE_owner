package com.example.minidelivery

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OrderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        val cookingTime = intent.getIntExtra("cookingTime", 0)
        val deliveryMethod = intent.getStringExtra("deliveryMethod") ?: ""

        findViewById<TextView>(R.id.cookingTimeTextView).text = "조리시간: $cookingTime 분"
        findViewById<TextView>(R.id.deliveryMethodTextView).text = "배달방법: $deliveryMethod"

        // Simulate delivery completion after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            showDeliveryCompletedDialog()
        }, 5000) // 5 seconds delay for demonstration
    }

    private fun showDeliveryCompletedDialog() {
        AlertDialog.Builder(this)
            .setTitle("배달 완료")
            .setMessage("주문하신 음식이 배달 완료되었습니다.")
            .setPositiveButton("확인") { _, _ ->
                startActivity(Intent(this, DeliveryCompletedActivity::class.java))
                finish()
            }
            .show()
    }
}