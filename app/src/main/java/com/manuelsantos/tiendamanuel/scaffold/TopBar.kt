package com.manuelsantos.tiendamanuel.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    nombreProducto: String,
    nombre: String,
    onBackClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver Atras"
                )
            }
        },
        actions = {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color(0xffedf3fc), shape = CircleShape)
                    .clickable { expanded = !expanded }
                    .padding(12.dp)
                    .animateContentSize(
                        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Boton usuario",
                    tint = Color(0xff000000)
                )
                if (expanded) {
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                }
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(animationSpec = tween(100)),
                    exit = fadeOut(animationSpec = tween(250))
                ) {
                    Text(nombre, color = Color.Black)
                }
            }
            DropdownMenu(
                modifier = Modifier.width(150.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            Text("Perfil", textAlign = TextAlign.End)
                        }
                    },
                    onClick = {
                        expanded = false
                        // Acción para ir al perfil
                    }
                )
                DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            Text("Cerrar sesión", textAlign = TextAlign.End, color = Color.Red)
                        }
                    },
                    onClick = {
                        expanded = false
                        // Acción para cerrar sesión
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        )
    )
}
