package com.example.minidelivery.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.minidelivery.R
import com.example.minidelivery.ui.done.DoneActivity
import com.example.minidelivery.ui.delivery.DeliveryActivity
import com.example.minidelivery.ui.detail.DetailActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.chip.ChipGroup

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var tabLayout: TabLayout
    private lateinit var chipGroup: ChipGroup
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var orderCardView: CardView
    private lateinit var acceptButton: Button
    private lateinit var timeTextView: TextView
    private lateinit var orderSummaryTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var paymentStatusTextView: TextView
    private lateinit var priceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupUI()
        observeViewModel()
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        chipGroup = findViewById(R.id.chipGroup)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        orderCardView = findViewById(R.id.orderCardView)
        acceptButton = findViewById(R.id.acceptButton)
        timeTextView = findViewById(R.id.timeTextView)
        orderSummaryTextView = findViewById(R.id.orderSummaryTextView)
        addressTextView = findViewById(R.id.addressTextView)
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView)
        priceTextView = findViewById(R.id.priceTextView)
    }

    private fun setupUI() {
        tabLayout.addOnTabSelectedListener(viewModel.tabSelectedListener)

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipLatest -> viewModel.setSortOrder(SortOrder.LATEST)
                R.id.chipOldest -> viewModel.setSortOrder(SortOrder.OLDEST)
            }
        }

        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, DoneActivity::class.java))
                    true
                }
                R.id.nav_delivery -> {
                    startActivity(Intent(this, DeliveryActivity::class.java))
                    true
                }
                else -> false
            }
        }

        acceptButton.setOnClickListener {
            viewModel.onAcceptButtonClick()
        }

        orderCardView.setOnClickListener {
            viewModel.currentOrder.value?.let { order ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("order", order)
                }
                startActivityForResult(intent, ORDER_DETAILS_REQUEST_CODE)
            }
        }
    }


    private fun observeViewModel() {
        viewModel.currentOrder.observe(this) { order ->
            order?.let {
                timeTextView.text = it.time
                orderSummaryTextView.text = it.summary
                addressTextView.text = it.address
                paymentStatusTextView.text = it.paymentStatus
                priceTextView.text = it.price
            }
        }

        viewModel.orderCardVisibility.observe(this) { isVisible ->
            orderCardView.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        viewModel.acceptButtonState.observe(this) { state ->
            acceptButton.text = state.text
            acceptButton.setBackgroundColor(getColor(state.colorResId))
        }
    }

    companion object {
        const val ORDER_DETAILS_REQUEST_CODE = 1001
    }
}