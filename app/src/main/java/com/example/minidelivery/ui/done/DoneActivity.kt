package com.example.minidelivery.ui.done

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minidelivery.data.Order
import com.example.minidelivery.R
import com.example.minidelivery.ui.delivery.DeliveryActivity
import com.example.minidelivery.ui.main.MainActivity
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
        setContentView(R.layout.activity_done)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(DoneViewModel::class.java)

        initViews()
        setupListeners()
        setupRecyclerView()
        observeViewModel()
        handleCompletedOrder() // 완료된 주문 처리
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.nav_done
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
                    viewModel.navigateToHome()
                    true
                }
                R.id.nav_done -> true
                R.id.nav_delivery -> {
                    viewModel.navigateToManageDelivery()
                    true
                }
                else -> false
            }
        }

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.latestChip -> viewModel.setSortOrder(DoneViewModel.SortOrder.LATEST)
                R.id.oldestChip -> viewModel.setSortOrder(order = DoneViewModel.SortOrder.OLDEST)
            }
        }
    }

    private fun handleCompletedOrder() {
        intent.getParcelableExtra<Order>("completedOrder")?.let { order ->
            viewModel.addCompletedOrder(order)
        }
    }

    private fun observeViewModel() {
        viewModel.completedOrders.observe(this) { orders ->
            (completedOrdersRecyclerView.adapter as DoneAdapter).updateOrders(orders)
        }

        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                is DoneViewModel.NavigationEvent.ToHome -> navigateToHome()
                is DoneViewModel.NavigationEvent.ToDelivery -> navigateToDelivery()
            }
        }
    }

    // Main Activity로 이동
    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    // Delivery Activity로 이동
    private fun navigateToDelivery() {
        val intent = Intent(this, DeliveryActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent, options.toBundle())
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
}