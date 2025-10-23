package com.example.masterbike_kotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masterbike_kotlin.data.api.SupabaseClient
import com.example.masterbike_kotlin.data.models.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor() : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadOrderHistory() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: run {
            _errorMessage.value = "Usuario no autenticado"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val userOrders = SupabaseClient.client.postgrest["orders"]
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                        order("created_at", ascending = false)
                    }
                    .decodeList<Order>()

                // Load order items for each order
                val ordersWithItems = userOrders.map { order ->
                    val orderItems = SupabaseClient.client.postgrest["order_items"]
                        .select {
                            filter {
                                eq("order_id", order.id)
                            }
                        }
                        .decodeList<com.example.masterbike_kotlin.data.models.OrderItem>()

                    // Load product details for each item
                    val itemsWithProducts = orderItems.map { item ->
                        val product = SupabaseClient.client.postgrest["products"]
                            .select {
                                filter {
                                    eq("id", item.productId)
                                }
                            }
                            .decodeSingleOrNull<com.example.masterbike_kotlin.data.models.Product>()

                        item.copy(product = product)
                    }

                    order.copy(items = itemsWithProducts)
                }

                _orders.value = ordersWithItems

            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el historial de pedidos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}