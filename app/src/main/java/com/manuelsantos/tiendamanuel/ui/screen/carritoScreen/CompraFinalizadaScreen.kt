package com.manuelsantos.tiendamanuel.ui.screen.carritoScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.manuelsantos.tiendamanuel.R
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.data.firebase.FirestoreViewModel
import com.manuelsantos.tiendamanuel.scaffold.TopBar

@Composable
fun CompraFinalizadaScreen(
    auth: AuthManager,
    viewModel: FirestoreViewModel,
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToCarrito: () -> Unit
) {
    val user = auth.getCurrentUser()
    val context = LocalContext.current
    var showVideo by remember { mutableStateOf(true) }
    val numeroElementosCarrito by viewModel.numeroElementosCarrito.observeAsState(0)

    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            val videoUri = RawResourceDataSource.buildRawResourceUri(R.raw.success_animation)
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()

            // Cuando el video termine, cambiaremos showVideo a false
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        showVideo = false
                    }
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(Unit) {
        if (user != null) {
            viewModel.vaciarCarrito(user.uid)
        }
        exoPlayer.play()
    }

    if (user == null) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showVideo) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                            }
                        },
                        modifier = Modifier
                            .width(450.dp)
                            .height(450.dp)
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_tienda),
                            contentDescription = "Logo Telares del sur",
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "¡Compra Finalizada!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Gracias por tu compra",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}