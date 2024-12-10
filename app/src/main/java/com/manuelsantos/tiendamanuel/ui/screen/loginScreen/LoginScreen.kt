package com.manuelsantos.tiendamanuel.ui.screen.loginScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manuelsantos.tiendamanuel.R

@Composable
fun LoginScreen(navigateToProductos: (String) -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_login),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.padding(vertical = 32.dp))

        // Campo de Usuario
        TextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Ícono de usuario")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        // Campo de Contraseña
        TextField(
            value = contrasenya,
            onValueChange = { contrasenya = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Ícono de contraseña")
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Acceso
        Button(
            onClick = { navigateToProductos(usuario) },
            modifier = Modifier.fillMaxWidth(),
            enabled = if (usuario == "" || contrasenya == "") false else true
        ) {
            Text(stringResource(R.string.acceder))
        }
    }
}