package com.manuelsantos.tiendamanuel.data.repositories.db

import com.manuelsantos.tiendamanuel.data.repositories.model.ProductoItem

class CarritoDB (
    val idCliente: String = "",
    val producto: ProductoItem = ProductoItem(),
    val cantidad: Int = 0,
    val precio: Double = 0.0
)