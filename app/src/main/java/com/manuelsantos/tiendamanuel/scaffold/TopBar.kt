package com.manuelsantos.tiendamanuel.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    nombreProducto: String,
    nombre: String,
    auth: AuthManager,
    onBackClick: () -> Unit,
    navigateToLogin: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val numeroCarrito = 0 // Número hardcodeado
    TopAppBar(
        title = {
            Text(
                text = nombreProducto,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 13.sp
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
                if (numeroCarrito > 0) {
                    Box(modifier = Modifier) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Boton usuario",
                            tint = Color(0xff000000),
                        )

                        // Badge rojo con número
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = 8.dp, y = (-8).dp)
                                .background(Color.Red, CircleShape)
                                .size(8.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Boton usuario",
                        tint = Color(0xff000000),
                    )
                }

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
                modifier = Modifier.width(175.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    modifier = Modifier.align(Alignment.End),
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Perfil", textAlign = TextAlign.End)
                            Spacer(modifier = Modifier.padding(8.dp))
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil",
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        // Acción para ir al perfil
                    }
                )
                DropdownMenuItem(
                    modifier = Modifier.align(Alignment.End),
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Carrito", textAlign = TextAlign.End, fontSize = 15.sp)
                            Spacer(modifier = Modifier.padding(8.dp))
                            Box{
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Carrito",
                                )
                                if (numeroCarrito > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 8.dp, y = (-4).dp)
                                            .background(Color.Red, CircleShape)
                                            .size(18.dp)
                                    ) {
                                        Text(
                                            text = numeroCarrito.toString(),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }

                        }
                    },
                    onClick = {
                        expanded = false
                        // Acción para ir al perfil
                    }
                )
                DropdownMenuItem(
                    modifier = Modifier.align(Alignment.End),
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Cerrar sesión", textAlign = TextAlign.End, color = Color.Red)
                            Spacer(modifier = Modifier.padding(8.dp))
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = Color.Red
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        auth.signOut()
                        navigateToLogin()
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
