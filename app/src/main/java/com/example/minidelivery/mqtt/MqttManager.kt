package com.example.minidelivery.mqtt

import android.util.Log
import com.example.minidelivery.ui.main.MainActivity
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import java.nio.charset.StandardCharsets
import androidx.lifecycle.MutableLiveData

// JSON 데이터를 담을 데이터 클래스 예시
data class OrderData(
    val store_id: String,
    val user_id: String,
    val order_name: String,
    val address: String,
    val price: Int,
    val order_date: String
)

class MqttManager(mainActivity: MainActivity) {
    private val serverUri = "tcp://192.168.137.82:1883" // 브로커 주소
    private val topic = "json" // 구독할 토픽
    private lateinit var mqttClient: Mqtt3AsyncClient

    // LiveData로 데이터 관리
    val orderLiveData = MutableLiveData<OrderData?>()

    fun connect() {
        try {
            // Mqtt3Client 생성
            mqttClient = MqttClient.builder()
                .useMqttVersion3() // MQTT v3 사용
                .serverHost("192.168.137.82") // 호스트 주소
                .serverPort(1883) // 포트 번호
                .buildAsync() // 비동기 클라이언트 생성

            // 연결 시도
            mqttClient.connect().whenComplete { _, throwable ->
                if (throwable == null) {
                    Log.d("MqttManager", "Connected to MQTT broker")
                    subscribeToTopic(topic)
                } else {
                    Log.e("MqttManager", "Failed to connect to MQTT broker", throwable)
                }
            }
        } catch (e: Exception) {
            Log.e("MqttManager", "Error during connection", e)
        }
    }

    private fun subscribeToTopic(topic: String) {
        try {
            mqttClient.subscribeWith()
                .topicFilter(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback { publish ->
                    val message = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                    Log.d("MqttManager", "Message received: $message")

                    // JSON 데이터를 OrderData 객체로 변환
                    val orderData = parseJsonToOrderData(message)
                    if (orderData != null) {
                        Log.d("MqttManager", "Parsed Order Data: $orderData")
                        // LiveData로 데이터 업데이트
                        orderLiveData.postValue(orderData)
                    }
                }
                .send()
        } catch (e: Exception) {
            Log.e("MqttManager", "Error during subscription", e)
        }
    }

    private fun parseJsonToOrderData(json: String): OrderData? {
        return try {
            val gson = com.google.gson.Gson()
            gson.fromJson(json, OrderData::class.java)
        } catch (e: Exception) {
            Log.e("MqttManager", "Failed to parse JSON: $json", e)
            null
        }
    }

    fun disconnect() {
        mqttClient.disconnect().whenComplete { _, throwable ->
            if (throwable == null) {
                Log.d("MqttManager", "Disconnected from MQTT broker")
            } else {
                Log.e("MqttManager", "Failed to disconnect from MQTT broker", throwable)
            }
        }
    }
}
