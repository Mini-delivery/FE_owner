package com.example.minidelivery.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.minidelivery.R
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderStatus

class OrderAdapter(
    private val onItemClick: (Order) -> Unit,
    private val onStatusButtonClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.ViewHolder>(OrderDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val orderSummaryTextView: TextView = view.findViewById(R.id.orderSummaryTextView)
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
        val paymentStatusTextView: TextView = view.findViewById(R.id.paymentStatusTextView)
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        val statusButton: Button = view.findViewById(R.id.statusButton)
    }

    fun updateOrders(newOrders: List<Order>) {
        submitList(newOrders)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = getItem(position)
        holder.timeTextView.text = order.time
        holder.orderSummaryTextView.text = order.summary
        holder.addressTextView.text = order.address
        holder.paymentStatusTextView.text = order.paymentStatus
        holder.priceTextView.text = order.price
        updateStatusButton(holder.statusButton, order.status)

        holder.itemView.setOnClickListener { onItemClick(order) }
        holder.statusButton.setOnClickListener { onStatusButtonClick(order) }
    }

    private fun updateStatusButton(button: Button, status: OrderStatus) {
        val context = button.context
        when (status) {
            OrderStatus.READY -> {
                button.text = "접수"
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.processing_color))
            }
            OrderStatus.COOKING -> {
                button.text = "조리완료"
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.cooking_color))
            }
            OrderStatus.COOKED -> {
                button.text = "배달시작"
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.cooked_color))
            }
            OrderStatus.DELIVERING -> {
                button.text = "배달완료"
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.delivering_color))
            }
            OrderStatus.COMPLETED -> {
                button.text = "완료"
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.completed_color))
                button.isEnabled = false
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}