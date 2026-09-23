package com.primera.evaluacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primera.evaluacion.data.ProductResponse
import com.primera.evaluacion.network.ProductRetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductListState(
    val productos: List<ProductResponse> = emptyList(),
    val cargando: Boolean = false,
    val error: String = ""
)

class ProductListViewModel : ViewModel() {

    private val apiService = ProductRetrofitClient.apiService

    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state

    fun cargarProductos() {
        viewModelScope.launch {
            _state.value = _state.value.copy(cargando = true, error = "")
            try {
                val response = apiService.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        productos = response.body()!!,
                        cargando = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        cargando = false,
                        error = "Error al cargar productos"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    cargando = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
}
