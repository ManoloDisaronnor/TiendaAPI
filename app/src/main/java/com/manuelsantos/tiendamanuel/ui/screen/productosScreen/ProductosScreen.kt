package com.manuelsantos.tiendamanuel.ui.screen.productosScreen

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.manuelsantos.tiendamanuel.R
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.ui.FirestoreViewModel
import com.manuelsantos.tiendamanuel.data.repositories.model.ProductoItem
import com.manuelsantos.tiendamanuel.scaffold.TopBarTienda
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun ProductosScreen(
    auth: AuthManager,
    viewModel: FirestoreViewModel,
    navigateToDetalle: (String) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToCarrito: () -> Unit,
    navigateToProfile: () -> Unit
) {
    val lista by viewModel.firestoreProducts.observeAsState(emptyList())
    val listaCategorias by viewModel.firestoreCategories.observeAsState(emptyList())
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    val syncState by viewModel.syncState.observeAsState()
    val context = LocalContext.current
    val progressBar by viewModel.isLoading.observeAsState(true)
    val user = auth.getCurrentUser()

    var headerOffsetY by remember { mutableFloatStateOf(0f) }
    val headerHeight = 136.dp

    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val headerOffset by animateFloatAsState(targetValue = headerOffsetY, label = "headerOffset")

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = (headerOffsetY + delta).coerceIn(-headerHeightPx, 0f)
                headerOffsetY = newOffset
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(Unit) {

        viewModel.loadCategories()
    }

    LaunchedEffect(syncState) {
        when (syncState) {
            is FirestoreViewModel.SyncState.Success -> {
                Toast.makeText(
                    context,
                    (syncState as FirestoreViewModel.SyncState.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.recargarEstadoSync()
            }

            is FirestoreViewModel.SyncState.Error -> {
                Toast.makeText(
                    context,
                    "No se ha podido añadir al carrito, error: " + (syncState as FirestoreViewModel.SyncState.Error).exception.message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.recargarEstadoSync()
            }

            is FirestoreViewModel.SyncState.Loading -> {

            }

            null -> {

            }
        }

    }

    LaunchedEffect(categoriaSeleccionada) {
        if (categoriaSeleccionada != "") {
            if (searchText != "") {
                viewModel.loadProductsByCategory(categoriaSeleccionada)
                viewModel.filtrarLista(searchText)
            } else {
                viewModel.loadProductsByCategory(categoriaSeleccionada)
            }
        } else {
            viewModel.loadFirestoreProducts()
            if (searchText != "") {
                viewModel.filtrarLista(searchText)
            }
        }
    }

    LaunchedEffect(searchText) {
        if (searchText != "") {
            if (categoriaSeleccionada != "") {
                viewModel.loadProductsByCategory(categoriaSeleccionada)
                viewModel.filtrarLista(searchText)
            } else {
                viewModel.filtrarLista(searchText)
            }
        } else {
            viewModel.loadFirestoreProducts()
            if (categoriaSeleccionada != "") {
                viewModel.loadFirestoreProductsByCategory(categoriaSeleccionada)
            }
        }
    }

    if (user != null) {
        Scaffold(
            topBar = {
                val nombre = if (user.email == null) {
                    "Invitado"
                } else {
                    user.displayName?.split(" ")?.firstOrNull() ?: "Usuario"
                }

                TopBarTienda(
                    nombre,
                    auth,
                    viewModelFirestore = viewModel,
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(nestedScrollConnection)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = headerHeight)
                        ) {
                            if (lista.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.logo_tienda),
                                            contentDescription = "Logo Telares del sur",
                                            modifier = Modifier.size(200.dp)
                                        )
                                        Spacer(modifier = Modifier.padding(vertical = 16.dp))
                                        Text(
                                            text = "Vaya, parece que existe ese producto. ¿Prueba a buscar otro?",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 18.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                items(lista) { mediaItem ->
                                    MediaItemCard(mediaItem, viewModel, user.uid, navigateToDetalle)
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(0, headerOffset.roundToInt()) }
                                .background(MaterialTheme.colorScheme.background)
                                .fillMaxWidth()
                        ) {
                            Column {
                                TextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp, horizontal = 16.dp)
                                        .shadow(2.dp, RoundedCornerShape(30.dp))
                                        .background(Color.White, RoundedCornerShape(30.dp)),
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    singleLine = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Buscar productos",
                                            tint = Color(0xFF002D85),
                                            modifier = Modifier.clickable {

                                            }
                                        )
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    placeholder = { Text("Buscar productos") }
                                )

                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .padding(bottom = 16.dp),
                                ) {
                                    items(listaCategorias) { categoria ->
                                        val colorFondo =
                                            if (categoriaSeleccionada == categoria) Color(0x45656565) else Color.White
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .background(
                                                    colorFondo,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(0xFF002D85),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    if (categoriaSeleccionada != "" && categoriaSeleccionada != categoria) {
                                                        viewModel.viewModelScope.launch {
                                                            viewModel.loadFirestoreProductsByCategory(categoria)
                                                        }
                                                    }
                                                    categoriaSeleccionada =
                                                        if (categoriaSeleccionada == categoria) "" else categoria
                                                }
                                                .padding(4.dp)
                                        ) {
                                            Text(
                                                text = categoria,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF002D85),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
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
            .padding(bottom = 14.dp)
            .padding(horizontal = 14.dp)
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