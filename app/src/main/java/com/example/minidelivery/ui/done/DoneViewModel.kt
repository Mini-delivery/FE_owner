package com.example.minidelivery.ui.done


import android.app.Activity
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minidelivery.R
import com.example.minidelivery.ui.main.MainActivity
import com.example.minidelivery.ui.delivery.ManageDeliveryActivity

class DoneViewModel : ViewModel() {
    // 완료된 주문 목록
    private val _completedOrders = MutableLiveData<List<Done>>(listOf())
    val completedOrders: LiveData<List<Done>> = _completedOrders

    // 새 주문 추가
    fun addNewOrder(order: Done) {
        val currentList = _completedOrders.value?.toMutableList() ?: mutableListOf()
        currentList.add(0, order)
        _completedOrders.value = currentList
    }

    // 홈으로 이동
    fun navigateToHome(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        activity.startActivity(intent)
        activity.finish()
    }

    // 배달 관리로 이동
    fun navigateToManageDelivery(activity: Activity) {
        val intent = Intent(activity, ManageDeliveryActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.fade_in, R.anim.fade_out)
        activity.startActivity(intent, options.toBundle())
    }

    // 뒤로가기 처리
    val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 홈으로 이동 로직 (Activity 참조 필요)
        }
    }
}