package com.example.masterbike_kotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masterbike_kotlin.data.api.SupabaseClient
import com.example.masterbike_kotlin.data.models.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor() : ViewModel() {

    private val _addresses = MutableLiveData<List<Address>>()
    val addresses: LiveData<List<Address>> = _addresses

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _addressAdded = MutableLiveData<Boolean>()
    val addressAdded: LiveData<Boolean> = _addressAdded

    private val _addressDeleted = MutableLiveData<Boolean>()
    val addressDeleted: LiveData<Boolean> = _addressDeleted

    private val _defaultAddressSet = MutableLiveData<Boolean>()
    val defaultAddressSet: LiveData<Boolean> = _defaultAddressSet

    fun loadAddresses() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: run {
            _errorMessage.value = "Usuario no autenticado"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val userAddresses = SupabaseClient.client.postgrest["addresses"]
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                        order("is_default", ascending = false)
                        order("created_at", ascending = false)
                    }
                    .decodeList<Address>()

                _addresses.value = userAddresses

            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar direcciones: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addAddress(street: String, city: String, state: String, zipCode: String, country: String) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: run {
            _errorMessage.value = "Usuario no autenticado"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Check if this is the first address, make it default
                val existingAddresses = _addresses.value ?: emptyList()
                val isDefault = existingAddresses.isEmpty()

                val address = Address(
                    id = "", // Will be generated
                    userId = userId,
                    street = street,
                    city = city,
                    state = state,
                    zipCode = zipCode,
                    country = country,
                    isDefault = isDefault
                )

                SupabaseClient.client.postgrest["addresses"]
                    .insert(address)

                _addressAdded.value = true

            } catch (e: Exception) {
                _errorMessage.value = "Error al agregar dirección: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                SupabaseClient.client.postgrest["addresses"]
                    .delete {
                        filter {
                            eq("id", addressId)
                        }
                    }

                _addressDeleted.value = true

            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar dirección: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setDefaultAddress(addressId: String) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: run {
            _errorMessage.value = "Usuario no autenticado"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // First, set all addresses to non-default
                SupabaseClient.client.postgrest["addresses"]
                    .update(mapOf("is_default" to false)) {
                        filter {
                            eq("user_id", userId)
                        }
                    }

                // Then set the selected address as default
                SupabaseClient.client.postgrest["addresses"]
                    .update(mapOf("is_default" to true)) {
                        filter {
                            eq("id", addressId)
                        }
                    }

                _defaultAddressSet.value = true

            } catch (e: Exception) {
                _errorMessage.value = "Error al establecer dirección predeterminada: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}