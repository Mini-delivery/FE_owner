package com.example.minidelivery.ui.done

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minidelivery.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup

class DoneActivity : AppCompatActivity() {
    // UI 컴포넌트 선언
    private lateinit var chipGroup: ChipGroup
    private lateinit var completedOrdersRecyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView

    // ViewModel 선언
    private lateinit var viewModel: DoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_orders)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(DoneViewModel::class.java)

        initViews()
        setupListeners()
        setupRecyclerView()

        // 들어온 주문 처리
        handleIncomingOrder()

        // 뒤로가기 처리 설정
        setupBackPressedCallback()
    }

    override fun onStart() {
        super.onStart()
        // 필요한 경우 추가 작업
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.nav_history
    }

    override fun onPause() {
        super.onPause()
        // 필요한 경우 상태 저장
    }

    override fun onStop() {
        super.onStop()
        // 필요한 경우 리소스 해제
    }

    override fun onDestroy() {
        super.onDestroy()
        // 리소스 정리
    }

    // 뷰 초기화
    private fun initViews() {
        chipGroup = findViewById(R.id.chipGroup)
        completedOrdersRecyclerView = findViewById(R.id.completedOrdersRecyclerView)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    // 리스너 설정
    private fun setupListeners() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    viewModel.navigateToHome(this)
                    true
                }
                R.id.nav_history -> true
                R.id.nav_delivery -> {
                    viewModel.navigateToManageDelivery(this)
                    true
                }
                else -> false
            }
        }
    }

    // 들어온 주문 처리
    private fun handleIncomingOrder() {
        if (intent.getBooleanExtra("isNewOrder", false)) {
            val summary = intent.getStringExtra("orderSummary") ?: return
            val address = intent.getStringExtra("address") ?: ""
            val price = intent.getStringExtra("price") ?: ""
            viewModel.addNewOrder(Done(summary, address, price))
        }
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {
        val adapter = DoneAdapter(emptyList()) // 빈 리스트로 초기화
        completedOrdersRecyclerView.adapter = adapter
        completedOrdersRecyclerView.layoutManager = LinearLayoutManager(this)

        // ViewModel의 데이터 변경 관찰
        viewModel.completedOrders.observe(this) { orders ->
            adapter.updateOrders(orders)
        }
    }

    // 뒤로가기 처리 설정
    private fun setupBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, viewModel.backPressedCallback)
    }
}