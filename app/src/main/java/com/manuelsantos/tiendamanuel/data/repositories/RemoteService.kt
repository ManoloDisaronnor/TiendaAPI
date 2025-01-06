package com.manuelsantos.tiendamanuel.data.repositories

import com.manuelsantos.tiendamanuel.data.repositories.model.ProductoItem
import com.manuelsantos.tiendamanuel.data.repositories.model.Productos
import retrofit2.http.GET
import retrofit2.http.Path

interface RemoteService {
    @GET("/products")
    suspend fun getProducts(): Productos
    @GET("/products/{id}")
    suspend fun getProductById(@Path("id") id: String): ProductoItem
}