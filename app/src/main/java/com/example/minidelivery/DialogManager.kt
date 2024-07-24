package com.example.minidelivery

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

object DialogManager {
    fun showCookingTimeDialog(context: Context, onTimeSelected: (Int) -> Unit) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_cooking_time)

        var time = 15
        val timeTextView: TextView = dialog.findViewById(R.id.timeTextView)
        val minusButton: Button = dialog.findViewById(R.id.minusButton)
        val plusButton: Button = dialog.findViewById(R.id.plusButton)
        val confirmButton: Button = dialog.findViewById(R.id.confirmButton)

        fun updateTimeDisplay() {
            timeTextView.text = "$time 분"
        }

        minusButton.setOnClickListener {
            if (time > 15) time -= 5
            updateTimeDisplay()
        }

        plusButton.setOnClickListener {
            if (time < 50) time += 5
            updateTimeDisplay()
        }

        confirmButton.setOnClickListener {
            onTimeSelected(time)
            dialog.dismiss()
        }

        updateTimeDisplay()
        dialog.show()
    }

    fun showDeliveryMethodDialog(context: Context, onMethodSelected: (String) -> Unit) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_delivery_method)

        val methods = listOf("배달", "포장", "매장식사")
        val methodsContainer: LinearLayout = dialog.findViewById(R.id.methodsContainer)

        methods.forEach { method ->
            val button = Button(context)
            button.text = method
            button.setOnClickListener {
                onMethodSelected(method)
                dialog.dismiss()
            }
            methodsContainer.addView(button)
        }

        dialog.show()
    }
}