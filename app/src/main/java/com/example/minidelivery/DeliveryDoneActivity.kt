package com.example.minidelivery

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DeliveryDoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 부모 클래스 onCreate 호출
        setContentView(R.layout.activity_delivery_done) // 레이아웃 설정

        val orderSummary = intent.getStringExtra("orderSummary") // 주문 요약 가져오기
        val address = intent.getStringExtra("address") // 주소 가져오기

        findViewById<TextView>(R.id.orderSummaryTextView).text = orderSummary // 주문 요약 설정
        findViewById<TextView>(R.id.addressTextView).text = address // 주소 설정

        findViewById<Button>(R.id.backToMainButton).setOnClickListener {
            finish() // 액티비티 종료
        }
    }
}