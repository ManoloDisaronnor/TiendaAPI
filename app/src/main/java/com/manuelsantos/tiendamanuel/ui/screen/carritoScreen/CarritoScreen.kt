package com.manuelsantos.tiendamanuel.ui.screen.carritoScreen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.data.firebase.FirestoreViewModel
import com.manuelsantos.tiendamanuel.data.repositories.db.ProductoItemDB
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
    val productoEliminando by viewModel.eliminadoCarritoLoading.observeAsState(false)

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(bottom = 80.dp)
                ) {
                    if (progressBar) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (listaCarrito.isNotEmpty()) {
                        Text(
                            text = "Carrito de ${
                                user.displayName?.split(" ")?.firstOrNull() ?: "Invitado"
                            }",
                            style = MaterialTheme.typography.displayMedium,
                            fontSize = 40.sp
                        )
                        Spacer(modifier = Modifier.padding(bottom = 16.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(listaCarrito) { producto ->
                                GenerarLineaPedido(producto, viewModel, user.uid, context)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay elementos", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            viewModel.vaciarCarrito(user.uid, context)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        if (productoEliminando) {
                            CircularProgressIndicator()
                        } else {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Borrar"
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text("Vaciar carrito")
                        }
                    }
                    Button(
                        onClick = {
                            Log.e("CarritoScreen", "Comprar productos")
                        },
                        enabled = listaCarrito.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Default.ShoppingCartCheckout,
                            contentDescription = "Factura"
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Pasar a factura")
                    }
                }
            }
        }
    }
}

@Composable
fun GenerarLineaPedido(
    producto: ProductoItemDB,
    viewModelFirestore: FirestoreViewModel,
    idUsuario: String,
    context: Context
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val maxOffset = 100f

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
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
                )
                .clickable {
                    if (offsetX > 0f) {
                        viewModelFirestore.eliminarLineaCarrito(producto, idUsuario, context)
                        for (i in 0 until 100) {
                            offsetX -= 1f
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFFFFFFF),
                    modifier = Modifier.size(32.dp)
                )
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
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(125.dp)
                    .padding(vertical = 8.dp)
            ) {
                AsyncImage(
                    model = producto.producto.image,
                    contentDescription = producto.producto.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                item {
                    Text(
                        text = producto.producto.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calificación: ${producto.producto.rating.rate} (${producto.producto.rating.count} votos)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${producto.producto.price}€",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 16.sp,
                        color = Color(0xFF002D85)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = String.format("Total: %.2f€", producto.producto.price * producto.unidades),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            color = Color(0xFF002D85)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Borrar",
                                tint = Color(0xFF1A1A1A),
                                modifier = Modifier
                                    .size(15.dp)
                                    .background(Color(0xB5DEDEDE))
                                    .clickable {
                                        if (producto.unidades > 1) {
                                            viewModelFirestore.restarUnidadCarrito(producto, idUsuario, context)
                                        } else {
                                            viewModelFirestore.eliminarLineaCarrito(producto, idUsuario, context)
                                        }
                                    }
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                            Text(
                                text = "Unidades: ${producto.unidades}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir",
                                tint = Color(0xFF1A1A1A),
                                modifier = Modifier
                                    .size(15.dp)
                                    .background(Color(0xB5DEDEDE))
                                    .clickable {
                                        viewModelFirestore.sumarUnidadCarrito(producto, idUsuario, context)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}