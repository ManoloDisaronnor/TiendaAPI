package com.manuelsantos.tiendamanuel.data.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthManager: ViewModel() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    private val _authState = MutableStateFlow<AuthRes<FirebaseUser?>>(AuthRes.Success(null))
    val authState: StateFlow<AuthRes<FirebaseUser?>> = _authState

    private val _progressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressBar: LiveData<Boolean> = _progressBar

    suspend fun createUserWithEmailAndPassword(email: String, password: String) {
        _progressBar.value = true
        viewModelScope.launch {
            _authState.value = try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                AuthRes.Success(authResult.user)
            } catch (e: Exception) {
                AuthRes.Error(e.message ?: "Error al crear el usuario")
            }
            _progressBar.value = false
        }
    }

    sealed class AuthRes<out T> {
        data class Success<T>(val data: T) : AuthRes<T>()
        data class Error(val errorMessage: String) : AuthRes<Nothing>()
    }
}