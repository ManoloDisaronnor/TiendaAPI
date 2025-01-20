package com.manuelsantos.tiendamanuel.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.manuelsantos.tiendamanuel.ui.screen.detalleScreen.DetalleScreen
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.LoginScreen
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosScreen
import com.manuelsantos.tiendamanuel.data.model.ProductosViewModel

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val viewModel = ProductosViewModel()
    viewModel.cargarProductos()
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen {
                navController.navigate(Productos)
            }
        }
        composable<Productos> {
            ProductosScreen(viewModel) { id ->
                navController.navigate(Detalle(id))
            }
        }
        composable<Detalle> { backStackEntry ->
            val id = backStackEntry.toRoute<Detalle>().id
            viewModel.cargarProductoId(id)
            DetalleScreen(viewModel) {
                navController.navigate(Productos) {
                    popUpTo(Productos) { inclusive = true }
                }
            }
        }
    }
}