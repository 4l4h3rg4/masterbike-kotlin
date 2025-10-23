package com.example.masterbike_kotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masterbike_kotlin.data.models.Product
import com.example.masterbike_kotlin.data.models.ProductCategory
import com.example.masterbike_kotlin.data.repositories.CartRepository
import com.example.masterbike_kotlin.data.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _filteredProducts = MutableLiveData<List<Product>>()
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _selectedCategory = MutableLiveData<ProductCategory?>()
    val selectedCategory: LiveData<ProductCategory?> = _selectedCategory

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = productRepository.getAllProducts()
                _products.value = result
                _filteredProducts.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar productos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: ProductCategory?) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun searchProducts(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    private fun applyFilters() {
        val allProducts = _products.value ?: return
        val category = _selectedCategory.value
        val query = _searchQuery.value ?: ""

        var filtered = allProducts

        // Filter by category
        if (category != null) {
            filtered = filtered.filter { it.category == category }
        }

        // Filter by search query
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description?.contains(query, ignoreCase = true) == true
            }
        }

        _filteredProducts.value = filtered
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return

        viewModelScope.launch {
            try {
                val result = cartRepository.addToCart(userId, productId, quantity)
                result.onSuccess {
                    // Cart updated successfully
                }.onFailure { exception ->
                    _errorMessage.value = "Error al agregar al carrito: ${exception.localizedMessage}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
            }
        }
    }

    fun clearFilters() {
        _selectedCategory.value = null
        _searchQuery.value = ""
        _filteredProducts.value = _products.value
    }
}