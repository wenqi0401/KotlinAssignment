package com.example.myapplication.orderData

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OrderRepository(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("orders", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveOrder(order: Order) {
        val orders = getAllOrders().toMutableList()
        val existingIndex = orders.indexOfFirst { it.orderId == order.orderId }
        if (existingIndex != -1) {
            orders[existingIndex] = order
        } else {
            orders.add(order)
        }
        saveOrdersToStorage(orders)
    }

    fun getAllOrders(): List<Order> {
        val json = sharedPreferences.getString("orders_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Order>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getUserOrders(phoneNumber: String): List<Order> {
        return getAllOrders().filter { it.phoneNumber == phoneNumber }
    }

    fun getOrderById(orderId: String): Order? {
        return getAllOrders().find { it.orderId == orderId }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        val orders = getAllOrders().toMutableList()
        val index = orders.indexOfFirst { it.orderId == orderId }
        if (index != -1) {
            val updatedOrder = orders[index].copy(status = newStatus)
            orders[index] = updatedOrder
            saveOrdersToStorage(orders)
        }
    }

    private fun saveOrdersToStorage(orders: List<Order>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(orders)
        editor.putString("orders_list", json)
        editor.apply()
    }
}
