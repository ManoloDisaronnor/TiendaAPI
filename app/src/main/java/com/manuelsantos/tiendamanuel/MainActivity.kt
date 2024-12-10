package com.manuelsantos.tiendamanuel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.manuelsantos.tiendamanuel.ui.navegacion.Navegacion
import com.manuelsantos.tiendamanuel.ui.theme.TiendaManuelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiendaManuelTheme {
                Navegacion()
            }
        }
    }
}
