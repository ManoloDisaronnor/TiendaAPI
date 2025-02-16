package com.manuelsantos.tiendamanuel.data.repositories.db

import com.manuelsantos.tiendamanuel.data.repositories.model.ProductoItem

// Clase para gestionar las lineas de pedido de un carrito
data class ProductoItemDB (
    val producto: ProductoItem = ProductoItem(),
    val unidades: Int = 0,
    val precio: Double = 0.0,
)