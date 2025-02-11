package com.manuelsantos.tiendamanuel.data.repositories.db

import com.manuelsantos.tiendamanuel.data.repositories.model.ProductoItem

class CarritoDB (
    val idCliente: Int,
    val producto: ProductoItem,
    val cantidad: Int,
    val precio: Double
)