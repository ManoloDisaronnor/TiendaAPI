package com.manuelsantos.tiendamanuel.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.ui.screen.detalleScreen.DetalleScreen
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.LoginScreen
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosScreen
import com.manuelsantos.tiendamanuel.data.model.ProductosViewModel
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.ForgotPasswordScreen
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.SignUpScreen

@Composable
fun Navegacion(auth: AuthManager) {
    val navController = rememberNavController()
    val viewModel = ProductosViewModel()
    viewModel.cargarProductos()
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(auth,
                {
                    navController.navigate(Productos) {
                        popUpTo(Login) {
                            inclusive = true
                        }
                    }
                },
                {
                    navController.navigate(SignUp)
                },
                {
                    navController.navigate(ForgotPassword)
                }
            )
        }

        composable<SignUp> {
            SignUpScreen(auth) {
                navController.navigate(Login) {
                    popUpTo(Login) { inclusive = true }
                }
            }
        }

        composable<ForgotPassword> {
            ForgotPasswordScreen(auth) {
                navController.navigate(Login) {
                    popUpTo(Login) { inclusive = true }
                }
            }
        }

        composable<Productos> {
            ProductosScreen(auth, viewModel,
                { id ->
                    navController.navigate(Detalle(id))
                },
                {
                    navController.navigate(Login) {
                        popUpTo(Productos) { inclusive = true }
                    }
                }
            )
        }

        composable<Detalle> { backStackEntry ->
            val id = backStackEntry.toRoute<Detalle>().id
            viewModel.cargarProductoId(id)
            DetalleScreen(
                auth, viewModel,
                {
                    navController.navigate(Productos) {
                        popUpTo(Productos) { inclusive = true }
                    }
                },
                {
                    navController.navigate(Login) {
                        popUpTo(Detalle) { inclusive = true }
                    }
                }
            )
        }
    }
}