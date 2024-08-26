package com.example.minidelivery.ui.completedorders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minidelivery.ui.managedelivery.ManageDeliveryActivity
import com.example.minidelivery.R
import com.example.minidelivery.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup

class CompletedOrdersActivity : AppCompatActivity() {
    private lateinit var chipGroup: ChipGroup // 칩그룹 선언
    private lateinit var completedOrdersRecyclerView: RecyclerView // 완료된 주문 리사이클러뷰 선언
    private lateinit var bottomNavigation: BottomNavigationView // 하단 네비게이션 선언
    private val completedOrders = mutableListOf<CompletedOrder>() // 완료된 주문 리스트 선언

    data class CompletedOrder( // 완료된 주문 데이터 클래스 정의
        val summary: String, // 요약
        val address: String, // 주소
        val price: String // 가격
    )

    inner class CompletedOrdersAdapter(private val orders: List<CompletedOrder>) : // 완료된 주문 어댑터 정의
        RecyclerView.Adapter<CompletedOrdersAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) { // 뷰홀더 정의
            val orderSummary: TextView = view.findViewById(R.id.orderSummaryTextView) // 주문 요약 뷰 찾기
            val address: TextView = view.findViewById(R.id.addressTextView) // 주소 뷰 찾기
            val price: TextView = view.findViewById(R.id.priceTextView) // 가격 뷰 찾기
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { // 뷰홀더 생성
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_completed_order, parent, false) // 아이템 뷰 인플레이트
            return ViewHolder(view) // 뷰홀더 반환
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) { // 뷰홀더 바인딩
            val order = orders[position] // 현재 위치의 주문 가져오기
            holder.orderSummary.text = order.summary // 주문 요약 설정
            holder.address.text = order.address // 주소 설정
            holder.price.text = order.price // 가격 설정
        }

        override fun getItemCount() = orders.size // 아이템 개수 반환
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_orders)

        initViews()
        setupListeners()
        handleIncomingOrder() // 들어온 주문 처리
        setupRecyclerView()

        bottomNavigation.selectedItemId = R.id.nav_history

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHome()
            }
        })
    }

    private fun initViews() { // 뷰 초기화 함수
        chipGroup = findViewById(R.id.chipGroup) // 칩그룹 찾기
        completedOrdersRecyclerView = findViewById(R.id.completedOrdersRecyclerView) // 완료된 주문 리사이클러뷰 찾기
        bottomNavigation = findViewById(R.id.bottomNavigation) // 하단 네비게이션 찾기
    }

    private fun setupListeners() { // 리스너 설정 함수
        bottomNavigation.setOnItemSelectedListener { menuItem -> // 하단 네비게이션 아이템 선택 리스너
            when (menuItem.itemId) {
                R.id.nav_home -> { // 홈 메뉴 선택 시
                    navigateToHome() // 홈으로 이동
                    true
                }
                R.id.nav_history -> true // 현재 화면이므로 아무 동작 없음
                R.id.nav_delivery -> { // 배달 관리 메뉴 선택 시
                    navigateToManageDelivery() // 배달 관리로 이동
                    true
                }
                R.id.nav_calendar -> true // 캘린더 메뉴 (미구현)
                else -> false
            }
        }
    }

    private fun handleIncomingOrder() {
        if (intent.getBooleanExtra("isNewOrder", false)) {
            val summary = intent.getStringExtra("orderSummary") ?: return
            val address = intent.getStringExtra("address") ?: ""
            val price = intent.getStringExtra("price") ?: ""
            val newOrder = CompletedOrder(summary, address, price)
            completedOrders.add(0, newOrder) // 리스트 맨 앞에 새 주문 추가
            completedOrdersRecyclerView.adapter?.notifyItemInserted(0) // RecyclerView 갱신
        }
    }

    private fun setupRecyclerView() { // 리사이클러뷰 설정 함수
        val adapter = CompletedOrdersAdapter(completedOrders) // 어댑터 생성
        completedOrdersRecyclerView.adapter = adapter // 리사이클러뷰에 어댑터 설정
        completedOrdersRecyclerView.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저 설정

        Log.d("CompletedOrders", "Total items: ${completedOrders.size}")
        completedOrders.forEachIndexed { index, order ->
            Log.d("CompletedOrders", "Item $index: ${order.summary}, ${order.address}, ${order.price}")
        }
    }

    private fun navigateToHome() { // 홈으로 이동하는 함수
        val intent = Intent(this, MainActivity::class.java) // 인텐트 생성
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP // 플래그 설정
        startActivity(intent) // 액티비티 시작
        finish() // 현재 액티비티 종료
    }

    private fun navigateToManageDelivery() { // 배달 관리로 이동하는 함수
        val intent = Intent(this, ManageDeliveryActivity::class.java) // 인텐트 생성
        val options = ActivityOptionsCompat.makeCustomAnimation(this,
            R.anim.fade_in,
            R.anim.fade_out
        ) // 애니메이션 옵션 생성
        startActivity(intent, options.toBundle()) // 애니메이션과 함께 액티비티 시작
    }
}