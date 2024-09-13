package com.example.minidelivery.ui.done


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minidelivery.R

class DoneAdapter(private var orders: List<Done>) :
    RecyclerView.Adapter<DoneAdapter.ViewHolder>() {

    // ViewHolder 클래스 정의
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderSummary: TextView = view.findViewById(R.id.orderSummaryTextView)
        val address: TextView = view.findViewById(R.id.addressTextView)
        val price: TextView = view.findViewById(R.id.priceTextView)
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_completed_order, parent, false)
        return ViewHolder(view)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.orderSummary.text = order.summary
        holder.address.text = order.address
        holder.price.text = order.price
    }

    // 아이템 개수 반환
    override fun getItemCount() = orders.size

    // 주문 목록 업데이트
    fun updateOrders(newOrders: List<Done>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}