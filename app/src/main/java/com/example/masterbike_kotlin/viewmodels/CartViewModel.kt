package com.example.masterbike_kotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masterbike_kotlin.data.api.SupabaseClient
import com.example.masterbike_kotlin.data.models.CartItem
import com.example.masterbike_kotlin.data.repositories.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double> = _totalAmount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadCartItems() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val items = cartRepository.getCartItems(userId)
                _cartItems.value = items
                calculateTotal(items)
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el carrito: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = cartRepository.addToCart(userId, productId, quantity)
                result.onSuccess { cartItem ->
                    loadCartItems() // Reload cart to show updated items
                }.onFailure { exception ->
                    _errorMessage.value = "Error al agregar al carrito: ${exception.localizedMessage}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCartItemQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = cartRepository.updateCartItemQuantity(cartItemId, quantity)
                result.onSuccess {
                    loadCartItems() // Reload cart to show updated totals
                }.onFailure { exception ->
                    _errorMessage.value = "Error al actualizar cantidad: ${exception.localizedMessage}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromCart(cartItemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = cartRepository.removeFromCart(cartItemId)
                result.onSuccess {
                    loadCartItems() // Reload cart to show updated items
                }.onFailure { exception ->
                    _errorMessage.value = "Error al eliminar del carrito: ${exception.localizedMessage}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCart() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = cartRepository.clearCart(userId)
                result.onSuccess {
                    _cartItems.value = emptyList()
                    _totalAmount.value = 0.0
                }.onFailure { exception ->
                    _errorMessage.value = "Error al vaciar el carrito: ${exception.localizedMessage}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
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