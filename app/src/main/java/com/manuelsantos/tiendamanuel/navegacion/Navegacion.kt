package com.manuelsantos.tiendamanuel.navegacion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.manuelsantos.tiendamanuel.R
import com.manuelsantos.tiendamanuel.data.firebase.AuthManager
import com.manuelsantos.tiendamanuel.ui.FirestoreViewModel
import com.manuelsantos.tiendamanuel.ui.screen.carritoScreen.CarritoScreen
import com.manuelsantos.tiendamanuel.ui.screen.carritoScreen.CompraFinalizadaScreen
import com.manuelsantos.tiendamanuel.ui.screen.carritoScreen.FacturaScreen
import com.manuelsantos.tiendamanuel.ui.screen.detalleScreen.DetalleScreen
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.LoginScreen
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosScreen
import com.manuelsantos.tiendamanuel.ui.screen.productosScreen.ProductosViewModel
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.ForgotPasswordScreen
import com.manuelsantos.tiendamanuel.ui.screen.loginScreen.SignUpScreen
import com.manuelsantos.tiendamanuel.ui.screen.profileScreen.ProfileScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter

@Composable
fun Navegacion(
    auth: AuthManager,
    viewModelAPI: ProductosViewModel,
    viewModelFirestore: FirestoreViewModel
) {
    // DECLARAMOS LA VARIABLE QUE CONTIENE LOS PRODUCTOS DE LA APIs
    val listaProductosAPI by viewModelAPI.lista.observeAsState(emptyList())
    val progressBar by viewModelFirestore.isLoading.observeAsState(true)

    LaunchedEffect(Unit) {
        // Primero se cargan los productos de la API en la lista definida arriba
        // Luego cargar productos de Firestore
        coroutineScope {
            val apiDeferred = async { viewModelAPI.cargarProductos() }
            val firestoreDeferred = async { viewModelFirestore.loadFirestoreProducts() }

            apiDeferred.await()
            firestoreDeferred.await()
        }

        // Observar cuando la API tenga datos
        snapshotFlow { listaProductosAPI }
            .filter { it.isNotEmpty() }
            .collect { productos ->
                viewModelFirestore.syncProducts(productos)
            }
    }

    if (progressBar) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .padding(top = 100.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_tienda),
                contentDescription = "Logo Telares del sur",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(R.string.cargando),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(50.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(65.dp),
                strokeWidth = 5.dp
            )
        }
    } else {
        val navController = rememberNavController()
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

            composable<Profile> {
                ProfileScreen(auth) {
                    navController.navigate(Productos) {
                        popUpTo(Productos) { inclusive = true }
                    }
                }
            }

            composable<Carrito> {
                CarritoScreen(
                    auth,
                    viewModelFirestore,
                    {
                        navController.popBackStack()
                    },
                    {
                        navController.navigate(Login) {
                            popUpTo(Carrito) { inclusive = true }
                        }
                    },
                    {
                        navController.navigate(Profile)
                    },
                    {
                        navController.navigate(Factura)
                    }
                )
            }

            composable<Factura> {
                FacturaScreen(
                    auth,
                    viewModelFirestore,
                    {
                        navController.popBackStack()
                    },
                    {
                        navController.navigate(Login) {
                            popUpTo(Factura) { inclusive = true }
                        }
                    },
                    {
                        navController.navigate(Profile)
                    },
                    {
                        navController.popBackStack()
                    },
                    {
                        navController.navigate(CompraFinalizada)
                    }
                )
            }

            composable<CompraFinalizada> {
                CompraFinalizadaScreen(
                    auth,
                    viewModelFirestore,
                    {
                        navController.popBackStack()
                    },
                    {
                        navController.navigate(Login) {
                            popUpTo(CompraFinalizada) { inclusive = true }
                        }
                    },
                    {
                        navController.navigate(Profile)
                    },
                    {
                        navController.navigate(Carrito) {
                            popUpTo(Carrito) { inclusive = true }
                        }
                    },
                    {
                        navController.navigate(Productos) {
                            popUpTo(Productos) { inclusive = true }
                        }
                    }
                )
            }

            composable<Productos> {
                ProductosScreen(auth, viewModelFirestore,
                    { id ->
                        navController.navigate(Detalle(id))
                    },
                    {
                        navController.navigate(Login) {
                            popUpTo(Productos) { inclusive = true }
                        }
                    },
                    {
                      navController.navigate(Carrito)
                    },
                    {
                        navController.navigate(Profile)
                    }
                )
            }

            composable<Detalle> { backStackEntry ->
                val id = backStackEntry.toRoute<Detalle>().id
                DetalleScreen(
                    auth,
                    viewModelFirestore,
                    id,
                    {
                        navController.navigate(Productos) {
                            popUpTo(Productos) { inclusive = true }
                        }
                    },
                    {
                        navController.navigate(Profile) {
                            popUpTo(Detalle(id)) {
                                inclusive = true
                            }
                        }
                    },
                    {
                        navController.navigate(Carrito)
                    },
                    {
                        navController.navigate(Login) {
                            popUpTo(Detalle(id)) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}