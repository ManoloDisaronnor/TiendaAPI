package com.manuelsantos.tiendamanuel.scaffold

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manuelsantos.tiendamanuel.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarTienda() {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.topBar_tienda), style = MaterialTheme.typography.titleLarge, fontSize = 24.sp)
        },
        navigationIcon = {
            IconButton(
                onClick = {  },
            ) {
                Icon(
                    imageVector = Icons.Default.Shop,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                        .border(0.dp, Color.White, CircleShape),
                    tint = Color(0xFF296AF1),

                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        )
    )
}