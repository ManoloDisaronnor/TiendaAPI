package com.manuelsantos.tiendamanuel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.data.firebase.FirestoreManager
import com.manuelsantos.tiendamanuel.data.firebase.FirestoreViewModel
import com.manuelsantos.tiendamanuel.navegacion.Navegacion
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosViewModel
import com.manuelsantos.tiendamanuel.ui.theme.TiendaManuelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiendaManuelTheme {
                // PRIMERO INICIALIZAR LA AUTENTIFICACIÓN Y CERRAR SESIÓN PARA EVITAR ERRORES
                val auth = AuthManager()
                auth.resetAuthState()
                auth.initializeGoogleSignIn(this)
                auth.signOut()
                // DECLARAR LOS VIEWMODELS Y EL MANEJADOR DE FIRESTORE PARA MANEJAR LA SINCRONIZACION DE DATOS EN LA NAVEGACIÓN
                val viewModelAPI = ProductosViewModel()
                val context = LocalContext.current
                val firestoreManager = FirestoreManager(auth, context)
                val factory = FirestoreViewModel.FirestoreViewModelFactory(firestoreManager)
                val viewModelFirestore = factory.create(FirestoreViewModel::class.java)

                Navegacion(auth, viewModelAPI, viewModelFirestore)
            }
        }
    }
}
