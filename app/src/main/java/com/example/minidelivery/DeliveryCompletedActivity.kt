package com.example.minidelivery

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DeliveryCompletedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_completed)

        findViewById<Button>(R.id.backToMainButton).setOnClickListener {
            finish()
        }
    }
}