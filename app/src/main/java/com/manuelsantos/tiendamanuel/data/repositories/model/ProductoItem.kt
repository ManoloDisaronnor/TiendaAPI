package com.manuelsantos.tiendamanuel.data.repositories.model

import com.manuelsantos.tiendamanuel.data.model.MediaItem

data class ProductoItem(
    val category: String = "",
    val description: String = "",
    val id: Int = 0,
    val image: String = "",
    val price: Double = 0.0,
    val rating: Rating = Rating(),
    val title: String = ""
) {
    override fun equals(other: Any?): Boolean {
        return this.category == (other as? ProductoItem)?.category &&
                this.description == (other as? ProductoItem)?.description &&
                this.id == (other as? ProductoItem)?.id &&
                this.image == (other as? ProductoItem)?.image &&
                this.price == (other as? ProductoItem)?.price &&
                this.rating == (other as? ProductoItem)?.rating &&
                this.title == (other as? ProductoItem)?.title
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

fun ProductoItem.toMediaItem() = MediaItem(
    category = category,
    description = description,
    id = id,
    image = image,
    price = price,
    rating = rating,
    title = title,
)

