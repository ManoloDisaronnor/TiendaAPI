package com.manuelsantos.tiendamanuel.ui.screen.productosScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.manuelsantos.tiendamanuel.scaffold.TopBarTienda

@Composable
fun ProductosScreen(usuario: String) {
    Scaffold(
        topBar = { TopBarTienda(usuario) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {

        }

    }
}