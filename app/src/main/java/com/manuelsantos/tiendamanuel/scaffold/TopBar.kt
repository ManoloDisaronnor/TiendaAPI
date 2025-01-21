package com.manuelsantos.tiendamanuel.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    nombreProducto: String,
    nombre: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = nombreProducto,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 14.sp
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver Atras"
                )
            }
        },
        actions = {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color(0xffedf3fc), shape = CircleShape)
                    .clickable {  }
                    .padding(12.dp)
            ) {
                Text(nombre, color = Color.Black)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Boton usuario",
                    tint = Color(0xff000000)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        )
    )
}

