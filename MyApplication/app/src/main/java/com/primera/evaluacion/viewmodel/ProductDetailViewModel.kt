package com.primera.evaluacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primera.evaluacion.data.ProductResponse
import com.primera.evaluacion.network.ProductRetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductDetailState(
    val producto: ProductResponse? = null,
    val cargando: Boolean = false,
    val error: String = ""
)

class ProductDetailViewModel : ViewModel() {

    private val apiService = ProductRetrofitClient.apiService

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state

    fun cargarProducto(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(cargando = true, error = "")
            try {
                val response = apiService.getProductById(id)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        producto = response.body()!!,
                        cargando = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        cargando = false,
                        error = "Error al cargar el producto"
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
