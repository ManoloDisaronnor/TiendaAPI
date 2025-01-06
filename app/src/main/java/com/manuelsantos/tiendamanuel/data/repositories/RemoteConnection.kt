package com.manuelsantos.tiendamanuel.data.repositories

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RemoteConnection {
    // He creado una configuracion adicional para el retrofit que hara que el timeout para las peticiones sea de 30 segundos.
    // Esto lo he hecho porque la API es muy lenta y muchas veces no responde a tiempo lo que hace que la aplicacion se cierre.
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val remoteService: RemoteService = retrofit.create()
}