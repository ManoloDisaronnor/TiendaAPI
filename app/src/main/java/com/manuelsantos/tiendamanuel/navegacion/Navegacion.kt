package com.manuelsantos.tiendamanuel.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.manuelsantos.tiendamanuel.ui.screen.detalleScreen.DetalleScreen
import com.manuelsantos.tiendamanuel.ui.screen.detalleScreen.ProductoIdViewModel
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.LoginScreen
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosScreen
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosViewModel

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen {
                navController.navigate(Productos)
            }
        }
        composable<Productos> {
            val viewModel = ProductosViewModel()
            ProductosScreen(viewModel) { id ->
                navController.navigate(Detalle(id))
            }
        }
        composable<Detalle> { backStackEntry ->
            val id = backStackEntry.toRoute<Detalle>().id
            val viewModel = ProductoIdViewModel(id)
            DetalleScreen(viewModel) {
                navController.navigate(Productos) {
                    popUpTo(Productos) { inclusive = true }
                }
            }
        }
    }
}