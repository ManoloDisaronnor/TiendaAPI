package com.manuelsantos.tiendamanuel.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.LoginScreen
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosScreen

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen { usuario ->
                navController.navigate(Productos(usuario))
            }
        }
        composable<Productos> { backStackEntry ->
            val usuario = backStackEntry.toRoute<Productos>().usuario
            ProductosScreen(usuario)
        }
    }
}