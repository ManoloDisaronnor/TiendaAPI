package com.manuelsantos.tiendamanuel.navegacion

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object SignUp

@Serializable
object ForgotPassword

@Serializable
object Profile

@Serializable
object Carrito

@Serializable
object Productos

@Serializable
data class Detalle(val id: String)