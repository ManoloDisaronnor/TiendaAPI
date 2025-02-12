package com.manuelsantos.tiendamanuel.data.firebase

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.manuelsantos.tiendamanuel.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthManager : ViewModel() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    private val _authState = MutableStateFlow<AuthRes<FirebaseUser?>>(AuthRes.Success(null))
    val authState: StateFlow<AuthRes<FirebaseUser?>> = _authState

    private val _progressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressBar: LiveData<Boolean> = _progressBar

    private val _progressBarAnonimous: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressBarAnonimous: LiveData<Boolean> = _progressBarAnonimous

    private val _progressBarGoogle: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressBarGoogle: LiveData<Boolean> = _progressBarGoogle

    private lateinit var googleSignInClient: GoogleSignInClient

    suspend fun createUserWithEmailAndPassword(email: String, password: String, usuario: String) {
        _progressBar.value = true
        viewModelScope.launch {
            _authState.value = try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                authResult.user?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(usuario)
                        .build()
                )?.await()
                AuthRes.Success(authResult.user)
            } catch (e: Exception) {
                AuthRes.Error(e.message ?: "Error al registrar el usuario")
            }
            _progressBar.value = false
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, passwd: String) {
        _progressBar.value = true
        viewModelScope.launch {
            _authState.value = try {
                val authResult = auth.signInWithEmailAndPassword(email, passwd).await()
                AuthRes.Success(authResult.user)
            } catch (e: Exception) {
                AuthRes.Error(e.message ?: "Error al iniciar sesion")
            }
            _progressBar.value = false
        }
    }


    suspend fun forgotPassword(email: String) {
        _progressBar.value = true
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthRes.Success(null)
            } catch (e: Exception) {
                _authState.value = AuthRes.Error(e.message ?: "Error al intentar reestablecer la contraseña")
            }
            _progressBar.value = false
        }
    }

    suspend fun signAnonimously() {
        _progressBarAnonimous.value = true
        viewModelScope.launch {
            try {
                auth.signInAnonymously().await()
                _authState.value = AuthRes.Success(null)
            } catch (e: Exception) {
                _authState.value = AuthRes.Error(e.message ?: "Error al intentar iniciar sesion anonima")
            }
            _progressBarAnonimous.value = false
        }
    }

    fun initializeGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        _progressBarGoogle.value = true
        viewModelScope.launch {
            _authState.value = try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                AuthRes.Success(authResult.user)
            } catch (e: Exception) {
                AuthRes.Error(e.message ?: "Error al iniciar sesión con Google")
            }
            _progressBarGoogle.value = false
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // FUNCION QUE SE USA PARA QUE EN EL LAUNCHED EFFECT CUANDO SE MUESTRE COMO ESTADO EL AUTH NO RECOJA
    // POR DEFECTO SIEMPRE EL VALOR SUCCESS, SINO QUE SE RECOJA EL VALOR IDLE
    // CADA VEZ QUE SE REALICE UNA OPERACION CON AUTHRES SE DEBERA LLAMAR A ESTA FUNCION PARA RESETEAR EL
    // ESTADO DE AUTH PARA LA SIGUIENTE OPERACION
    fun resetAuthState() {
        _authState.value = AuthRes.Idle
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    suspend fun updateDisplayName(newName: String) {
        val user = auth.currentUser
        if (user == null) {
            _authState.value = AuthRes.Error("Usuario no autenticado")
            return
        }

        _progressBar.value = true
        viewModelScope.launch {
            _authState.value = try {
                user.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build()
                ).await()
                AuthRes.Success(auth.currentUser)
            } catch (e: Exception) {
                AuthRes.Error(e.message ?: "Error al actualizar el nombre")
            }
            _progressBar.value = false
        }
    }

    sealed class AuthRes<out T> {
        data object Idle : AuthRes<Nothing>()
        data class Success<T>(val data: T) : AuthRes<T>()
        data class Error(val errorMessage: String) : AuthRes<Nothing>()
    }


}