package com.manuelsantos.tiendamanuel.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.LoginScreen
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosScreen
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosViewModel

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
            val viewModel = ProductosViewModel()
            ProductosScreen(usuario, viewModel)
        }
    }
}