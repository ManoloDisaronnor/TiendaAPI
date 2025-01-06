package com.manuelsantos.tiendamanuel.data.model

import com.manuelsantos.tiendamanuel.data.repositories.model.Rating

data class MediaItem (
    val category: String,
    val description: String,
    val id: Int,
    val image: String,
    val price: Double,
    val rating: Rating,
    val title: String
)