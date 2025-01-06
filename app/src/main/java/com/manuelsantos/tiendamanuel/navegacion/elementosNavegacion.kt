package com.manuelsantos.tiendamanuel.navegacion

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Productos

@Serializable
data class Detalle(val id: String)