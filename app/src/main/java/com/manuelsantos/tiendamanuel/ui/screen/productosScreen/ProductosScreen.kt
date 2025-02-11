package com.manuelsantos.tiendamanuel.ui.screen.productosScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.data.firebase.FirestoreViewModel
import com.manuelsantos.tiendamanuel.data.repositories.model.ProductoItem
import com.manuelsantos.tiendamanuel.scaffold.TopBarTienda
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ProductosScreen(
    auth: AuthManager,
    viewModel: FirestoreViewModel,
    navigateToDetalle: (String) -> Unit,
    navigateToLogin: () -> Unit
) {
    val lista by viewModel.firestoreProducts.observeAsState(emptyList())
    val progressBar by viewModel.isLoading.observeAsState(false)
    val user = auth.getCurrentUser()

    Scaffold(
        topBar = {
            val nombre = if (user?.email == null) {
                "Invitado"
            } else {
                user.displayName?.split(" ")?.firstOrNull() ?: "Usuario"
            }

            TopBarTienda(nombre, auth) {
                navigateToLogin()
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (progressBar) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (lista.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay elementos", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(lista) { mediaItem ->
                            MediaItemCard(mediaItem, viewModel, user!!.uid, navigateToDetalle)
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun MediaItemCard(
    productoItem: ProductoItem,
    viewModelFirestore: FirestoreViewModel,
    idUsuario: String,
    navigateToDetalle: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .border(
                width = 0.dp,
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .padding(8.dp)
            .clickable {
                navigateToDetalle(productoItem.id.toString())
            },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Imagen(item = productoItem)
        Titulo(item = productoItem, viewModelFirestore = viewModelFirestore, userid = idUsuario)
    }
}

@Composable
private fun Imagen(item: ProductoItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {
        AsyncImage(
            model = item.image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun Titulo(item: ProductoItem, viewModelFirestore: FirestoreViewModel, userid: String) {
    // Introduzco algunas variables y funciones extras para que el icono del carrito se anime al hacer click
    // (No tiene ninguna otra funcionalidad extra)
    var isClicked by remember { mutableStateOf(false) }
    val progressBar by viewModelFirestore.isLoading.observeAsState(false)

    // Animación del icono
    val rotation by animateFloatAsState(
        targetValue = if (isClicked) 360f else 0f,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Calificación: ${item.rating.rate} (${item.rating.count} votos)",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${item.price}€",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 28.sp,
            color = Color(0xFF002D85)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isClicked = !isClicked
                viewModelFirestore.addCarrito(item, userid)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (progressBar) {
                CircularProgressIndicator()
            } else {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Añadir al carrito",
                        modifier = Modifier
                            .graphicsLayer {
                                rotationZ = rotation
                            }
                    )

                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                    Text("Añadir al carrito")
                }
            }

        }
    }
}