package com.manuelsantos.tiendamanuel.data.repositories.model

import com.manuelsantos.tiendamanuel.data.model.MediaItem

data class ProductoItem(
    val category: String,
    val description: String,
    val id: Int,
    val image: String,
    val price: Double,
    val rating: Rating,
    val title: String
)

fun ProductoItem.toMediaItem() = MediaItem(
    category = category,
    description = description,
    id = id,
    image = image,
    price = price,
    rating = rating,
    title = title,
)