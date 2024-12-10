package com.manuelsantos.tiendamanuel.data.model

import com.manuelsantos.tiendamanuel.data.Rating

data class MediaItem (
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val image: String,
    val rating: Rating
)