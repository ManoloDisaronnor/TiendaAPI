package com.manuelsantos.tiendamanuel.ui.screen.carritoScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.ui.FirestoreViewModel
import com.manuelsantos.tiendamanuel.data.repositories.db.ProductoItemDB
import com.manuelsantos.tiendamanuel.scaffold.TopBar

@Composable
fun FacturaScreen(
    auth: AuthManager,
    viewModel: FirestoreViewModel,
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToCarrito: () -> Unit,
    navigateToCompraFinalizada: () -> Unit
) {
    val listaCarrito by viewModel.carrito.observeAsState(emptyList())
    val progressBar by viewModel.isLoading.observeAsState(false)
    val user = auth.getCurrentUser()
    val context = LocalContext.current
    val totalFactura = listaCarrito.sumOf { it.precio * it.unidades }

    LaunchedEffect(Unit) {
        viewModel.getCarrito(auth.getCurrentUser()?.uid, context)
    }

    if (user == null || listaCarrito.isEmpty() || progressBar) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                val nombre = user.displayName?.split(" ")?.firstOrNull() ?: "Invitado"
                TopBar(
                    "Factura",
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
                        navigateToCarrito()
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
                    Text(
                        text = "Factura de ${
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
                            GenerarLineaFactura(producto)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = String.format("Total: %.2f€", totalFactura),
                        textDecoration = TextDecoration.Underline,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Button(
                        onClick = {
                            navigateToCompraFinalizada()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Completar compra"
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Completar compra")
                    }
                }
            }
        }
    }
}

@Composable
fun GenerarLineaFactura(
    producto: ProductoItemDB
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp),
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
                        text = String.format(
                            "Total: %.2f€",
                            producto.producto.price * producto.unidades
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = Color(0xFF002D85)
                    )
                    Text(
                        text = "Unidades: ${producto.unidades}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}