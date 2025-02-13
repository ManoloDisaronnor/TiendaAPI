package com.manuelsantos.tiendamanuel.ui.screen.carritoScreen

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.data.firebase.FirestoreViewModel
import com.manuelsantos.tiendamanuel.data.repositories.model.ProductoItem
import com.manuelsantos.tiendamanuel.scaffold.TopBar

@Composable
fun CarritoScreen(
    auth: AuthManager,
    viewModel: FirestoreViewModel,
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToProfile: () -> Unit
) {
    val listaCarrito by viewModel.carrito.observeAsState(emptyList())
    val progressBar by viewModel.isLoading.observeAsState(false)
    val user = auth.getCurrentUser()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getCarrito(auth.getCurrentUser()?.uid, context)
    }

    if (user != null) {
        Scaffold(
            topBar = {
                val nombre = user.displayName?.split(" ")?.firstOrNull() ?: "Invitado"
                TopBar(
                    "Carrito",
                    nombre,
                    24.sp,
                    auth,
                    viewModelFirestore = viewModel,
                    {
                        navigateToBack()
                    },
                    {
                        navigateToProfile()
                    },
                    {

                    },
                    {
                        navigateToLogin()
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                if (progressBar) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Text(
                        text = "Carrito de ${
                            user.displayName?.split(" ")?.firstOrNull() ?: "Invitado"
                        }",
                        style = MaterialTheme.typography.displayMedium,
                        fontSize = 40.sp
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(listaCarrito) { producto ->
                            GenerarLineaPedido(producto)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenerarLineaPedido(producto: ProductoItem) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val maxOffset = 100f

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Box del botón eliminar con esquinas redondeadas
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(150.dp)
                .align(Alignment.CenterStart)
                .background(
                    color = Color(0xFFFF0000),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        bottomStart = 12.dp
                    )
                ),
            contentAlignment = Alignment.Center  // Centra el contenido vertical y horizontalmente
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        Log.e("CarritoScreen", "Eliminar producto")
                    }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFFFFFFF),  // Cambié el color a blanco para mejor contraste
                        modifier = Modifier.size(32.dp)  // Icono un poco más grande
                    )
                }

                // Texto descriptivo debajo del icono
                Text(
                    text = "Eliminar",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Row principal con el contenido
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .offset(x = offsetX.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < maxOffset / 2) {
                                offsetX = 0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceIn(0f, maxOffset)
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = producto.title,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 18.sp
            )
        }
    }
}