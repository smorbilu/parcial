package com.primera.evaluacion.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LoginState(
    val usuario: String = "",
    val password: String = "",
    val mensaje: String = "",
    val loginExito: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onUsuarioChange(nuevoUsuario: String) {
        _state.value = _state.value.copy(usuario = nuevoUsuario)
    }

    fun onPasswordChange(nuevoPassword: String) {
        _state.value = _state.value.copy(password = nuevoPassword)
    }

    fun onLoginClic() {
        val usuario = _state.value.usuario
        val password = _state.value.password

        if (usuario == "admin" && password == "admin") {
            _state.value = _state.value.copy(
                loginExito = true,
                mensaje = "Bienvenido"
            )
        } else {
            _state.value = _state.value.copy(
                loginExito = false,
                mensaje = "Credenciales incorrectas"
            )
        }
    }
}
