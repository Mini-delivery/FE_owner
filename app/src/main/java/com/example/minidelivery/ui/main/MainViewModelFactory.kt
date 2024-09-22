package com.example.minidelivery.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.minidelivery.mqtt.MqttManager

class MainViewModelFactory(private val mqttManager: MqttManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(mqttManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
