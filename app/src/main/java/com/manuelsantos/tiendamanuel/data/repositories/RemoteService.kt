package com.manuelsantos.tiendamanuel.data.repositories

import com.manuelsantos.tiendamanuel.data.Productos
import retrofit2.http.GET

interface RemoteService {
    @GET("/products")
    suspend fun getProducts(): Productos
}