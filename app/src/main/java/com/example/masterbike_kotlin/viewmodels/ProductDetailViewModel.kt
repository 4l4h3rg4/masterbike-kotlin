package com.example.masterbike_kotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masterbike_kotlin.data.api.SupabaseClient
import com.example.masterbike_kotlin.data.models.Product
import com.example.masterbike_kotlin.data.repositories.CartRepository
import com.example.masterbike_kotlin.data.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _addToCartSuccess = MutableLiveData<Boolean>()
    val addToCartSuccess: LiveData<Boolean> = _addToCartSuccess

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = productRepository.getProductById(productId)
                _product.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el producto: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(quantity: Int) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: run {
            _errorMessage.value = "Debes iniciar sesión para agregar productos al carrito"
            return
        }

        val product = _product.value ?: run {
            _errorMessage.value = "Producto no encontrado"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = cartRepository.addToCart(userId, product.id, quantity)
                result.onSuccess {
                    _addToCartSuccess.value = true
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
}