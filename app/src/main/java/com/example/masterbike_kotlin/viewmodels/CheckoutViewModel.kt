package com.example.masterbike_kotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masterbike_kotlin.data.api.SupabaseClient
import com.example.masterbike_kotlin.data.models.*
import com.example.masterbike_kotlin.data.repositories.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _orderItems = MutableLiveData<List<CartItem>>()
    val orderItems: LiveData<List<CartItem>> = _orderItems

    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double> = _totalAmount

    private val _shippingAddress = MutableLiveData<String>()
    val shippingAddress: LiveData<String> = _shippingAddress

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _orderPlaced = MutableLiveData<Boolean>()
    val orderPlaced: LiveData<Boolean> = _orderPlaced

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadCheckoutData() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Load cart items
                val cartItems = cartRepository.getCartItems(userId)
                _orderItems.value = cartItems
                calculateTotal(cartItems)

                // Load default shipping address
                loadDefaultShippingAddress(userId)

            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar datos del checkout: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadDefaultShippingAddress(userId: String) {
        try {
            val addresses = SupabaseClient.client.postgrest["addresses"]
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("is_default", true)
                    }
                }
                .decodeList<Address>()

            if (addresses.isNotEmpty()) {
                val address = addresses.first()
                val fullAddress = "${address.street}, ${address.city}, ${address.state} ${address.zipCode}, ${address.country}"
                _shippingAddress.value = fullAddress
            }
        } catch (e: Exception) {
            // Ignore address loading errors
        }
    }

    fun placeOrder(shippingAddress: String, paymentMethod: String) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: run {
            _errorMessage.value = "Usuario no autenticado"
            return
        }

        val cartItems = _orderItems.value ?: run {
            _errorMessage.value = "No hay items en el carrito"
            return
        }

        if (cartItems.isEmpty()) {
            _errorMessage.value = "El carrito está vacío"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Simulate payment processing
                simulatePayment(paymentMethod)

                // Create order
                val totalAmount = _totalAmount.value ?: 0.0
                val order = createOrder(userId, totalAmount, shippingAddress)

                // Create order items
                createOrderItems(order.id, cartItems)

                // Clear cart
                cartRepository.clearCart(userId)

                _orderPlaced.value = true

            } catch (e: Exception) {
                _errorMessage.value = "Error al procesar el pedido: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun simulatePayment(paymentMethod: String) {
        // Simulate payment processing delay
        kotlinx.coroutines.delay(2000)

        // Simulate random payment success/failure (90% success rate)
        if (kotlin.random.Random.nextFloat() < 0.9f) {
            // Payment successful
        } else {
            throw Exception("Pago rechazado. Intente con otra tarjeta.")
        }
    }

    private suspend fun createOrder(userId: String, totalAmount: Double, shippingAddress: String): Order {
        val order = Order(
            id = "", // Will be generated
            userId = userId,
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            shippingAddressId = null, // We'll store address as text for simplicity
            createdAt = "",
            updatedAt = null
        )

        return SupabaseClient.client.postgrest["orders"]
            .insert(order)
            .decodeSingle<Order>()
    }

    private suspend fun createOrderItems(orderId: String, cartItems: List<CartItem>) {
        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                id = "",
                orderId = orderId,
                productId = cartItem.productId,
                quantity = cartItem.quantity,
                unitPrice = cartItem.product?.price ?: 0.0,
                product = cartItem.product
            )
        }

        SupabaseClient.client.postgrest["order_items"]
            .insert(orderItems)
    }

    private fun calculateTotal(cartItems: List<CartItem>) {
        val total = cartItems.sumOf { cartItem ->
            cartItem.product?.let { product ->
                product.price * cartItem.quantity
            } ?: 0.0
        }
        _totalAmount.value = total
    }
}