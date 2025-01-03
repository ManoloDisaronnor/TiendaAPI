package com.manuelsantos.tiendamanuel.ui.screen.loginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manuelsantos.tiendamanuel.R

@Composable
fun LoginScreen(navigateToProductos: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF296AF1)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.width(350.dp)
                .height(450.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFFFFF))
                .border(5.dp, Color(0xFF002D85), RoundedCornerShape(16.dp))
                .padding(vertical = 32.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.title_login),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
                fontFamily = FontFamily.Monospace
            )
            Image(
                painter = painterResource(id = R.drawable.logo_tienda),
                contentDescription = "Logo Telares del sur",
                modifier = Modifier.size(250.dp)
            )

            // Botón de Acceso
            Button(
                onClick = { navigateToProductos() }
            ) {
                Text(stringResource(R.string.acceder))
            }
        }
    }
}