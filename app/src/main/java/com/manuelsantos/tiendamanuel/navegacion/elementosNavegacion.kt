package com.manuelsantos.tiendamanuel.navegacion

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
data class Productos(val usuario: String)