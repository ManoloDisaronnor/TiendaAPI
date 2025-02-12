package com.manuelsantos.tiendamanuel.ui.screen.profileScreen

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manuelsantos.tiendamanuel.R
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    auth: AuthManager,
    navigateToBack: () -> Unit
) {
    val authState by auth.authState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthManager.AuthRes.Success -> {
                Toast.makeText(context, "Usuario actualizado correctamente", Toast.LENGTH_SHORT)
                    .show()
                auth.resetAuthState()
            }

            is AuthManager.AuthRes.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthManager.AuthRes.Error).errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is AuthManager.AuthRes.Idle -> {}
        }
    }

    val user = auth.getCurrentUser()
    val nombre: String = if (user?.email == null) {
        "Invitado"
    } else {
        user.displayName ?: "Usuario"
    }
    val nombrePerfil = nombre.toCharArray()[0].toString().toUpperCase()

    var nombreNuevo by remember { mutableStateOf(nombre) }
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Atras",
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.Transparent, CircleShape)
                        .clickable { navigateToBack() }
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text(
                    stringResource(id = R.string.ajustes),
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color(0xFFD00073), CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        nombrePerfil,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontSize = 85.sp
                    )
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 20.dp))
            Text(
                stringResource(id = R.string.cambiar_nombre),
                style = MaterialTheme.typography.labelLarge,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            TextField(
                value = nombreNuevo,
                onValueChange = { nombreNuevo = it },
                placeholder = { Text("Modificar nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    if (nombreNuevo != nombre) {
                        scope.launch {
                            auth.updateDisplayName(nombreNuevo)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "El nombre de usuario es el mismo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            ) {
                if (auth.progressBar.observeAsState().value == true) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(30.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(stringResource(id = R.string.guardar_cambios))
                }
            }

        }
    }
}