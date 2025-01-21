package com.manuelsantos.tiendamanuel.ui.screen.detalleScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.manuelsantos.tiendamanuel.R
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.data.model.MediaItem
import com.manuelsantos.tiendamanuel.scaffold.TopBar
import com.manuelsantos.tiendamanuel.data.model.ProductosViewModel

@Composable
fun DetalleScreen(auth: AuthManager, viewModel: ProductosViewModel, navigateToBack: () -> Unit) {
    val producto by viewModel.producto.observeAsState()
    val progressBar by viewModel.progressBar.observeAsState(false)
    val user = auth.getCurrentUser()

    if (progressBar) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = { TopBar(producto!!.title, user?.displayName!!) { navigateToBack() } }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF))
                    .padding(innerPadding)
                    .padding(16.dp)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    Imagen(producto!!)
                    CargarDetalles(producto!!)
                }
            }
        }

    }
}

@Composable
private fun Imagen(producto: MediaItem) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .height(500.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = producto.image,
            contentDescription = "Imagen del producto ${producto.title}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(
                    color = Color(0xCCFFFFFF),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF002D85),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = producto.category,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF002D85),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
private fun CargarDetalles(producto: MediaItem) {
    var isClicked by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isClicked) 360f else 0f,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = producto.title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.7f)
            )

            Spacer(modifier = Modifier.padding(horizontal = 8.dp))

            Text(
                text = "${producto.price}€",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 28.sp,
                color = Color(0xFF002D85),
                textAlign = TextAlign.Right,
                modifier = Modifier.weight(0.3f)
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = stringResource(id = R.string.descripcion),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = producto.description,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 22.sp,
            textAlign = TextAlign.Justify,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = stringResource(id = R.string.resenyas),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable {}
                .border(1.dp, Color(0xFF414141))
                .padding(vertical = 16.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Calificación: ${producto.rating.rate}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 20.sp,
                color = Color(0xFF2A2A2A)
            )

            Spacer(modifier = Modifier.padding(horizontal = 3.dp))

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Estrellas calificación",
                tint = Color(0xFFFFFD00)
            )

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            Text(
                text = "(${producto.rating.count} votos)",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 20.sp,
                color = Color(0xFF2A2A2A)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Flecha desplegable",
                modifier = Modifier.weight(0.1f)
            )
        }

        Spacer(modifier = Modifier.padding(16.dp))

        Button(
            onClick = {
                isClicked = !isClicked
            },
            modifier = Modifier.fillMaxWidth()
        ) {
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