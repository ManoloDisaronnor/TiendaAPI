package com.manuelsantos.tiendamanuel.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class AuthManager {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    suspend fun createUserWithEmailAndPassword(email: String, password: String):
            AuthRes<FirebaseUser?> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            AuthRes.Success(authResult.user)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al crear el usuario")
        }
    }

    sealed class AuthRes<out T> {
        data class Success<T>(val data: T) : AuthRes<T>()
        data class Error(val errorMessage: String) : AuthRes<Nothing>()
    }
}