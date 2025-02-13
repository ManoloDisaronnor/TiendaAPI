package com.manuelsantos.tiendamanuel.data.firebase

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.manuelsantos.tiendamanuel.data.repositories.db.CarritoDB
import com.manuelsantos.tiendamanuel.data.repositories.model.ProductoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.tasks.await

class FirestoreManager(auth: AuthManager, context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = auth.getCurrentUser()?.uid

    companion object {
        const val PRODUCTOS = "Producto"
        const val CARRITO = "Carrito"
    }

    // ----------------------------------- PRODUCTOS -----------------------------
    suspend fun getProductos(): Flow<List<ProductoItem>> {
        return firestore.collection(PRODUCTOS).snapshots().map { querySnapshot ->
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(ProductoItem::class.java)
            }
        }
    }

    suspend fun getProductoById(id: String): ProductoItem {
        val document = firestore.collection(PRODUCTOS).document(id).get().await()
        return document.toObject(ProductoItem::class.java) ?: throw Exception("Producto no encontrado")
    }

    suspend fun addProducto(productoItem: ProductoItem) {
        firestore.collection(PRODUCTOS).document(productoItem.id.toString()).set(productoItem).await()
    }

    suspend fun deleteAllProducts() {
        firestore.collection(PRODUCTOS).get().await().documents.forEach {
            it.reference.delete().await()
        }
    }

    // ----------------------------------- CARRITO -----------------------------
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCarrito(userId: String): Flow<List<CarritoDB>> {
        return firestore.collection(CARRITO)
            .whereEqualTo("idCliente", userId)
            .snapshots()
            .mapLatest { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    try {
                        // Extrae datos del carrito
                        val idProducto = document.getString("idProducto") ?: return@mapNotNull null
                        val unidades = document.getLong("unidades")?.toInt() ?: 0
                        val precioCarrito = document.getDouble("precio") ?: 0.0

                        // Busca el producto en Firestore
                        val productoSnapshot = firestore.collection(PRODUCTOS)
                            .document(idProducto)
                            .get()
                            .await()

                        val producto = productoSnapshot.toObject(ProductoItem::class.java)
                            ?: return@mapNotNull null

                        // Crea el objeto CarritoDB
                        CarritoDB(
                            idCliente = userId,
                            producto = producto,
                            cantidad = unidades,
                            precio = precioCarrito
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .flowOn(Dispatchers.IO)  // Ejecuta en un hilo de IO
    }

    suspend fun addCarrito(item: ProductoItem, userid: String) {
        val carritoRef = firestore.collection(CARRITO)
        val docId = "${userid}_${item.id}" // ID compuesto único

        firestore.runTransaction { transaction ->
            val docSnapshot = transaction.get(carritoRef.document(docId))

            if (docSnapshot.exists()) {
                // Incrementar unidades si ya existe
                transaction.update(
                    carritoRef.document(docId),
                    "unidades",
                    FieldValue.increment(1)
                )
            } else {
                // Crear nuevo registro
                val newItem = hashMapOf(
                    "idCliente" to userid,
                    "idProducto" to item.id.toString(),
                    "unidades" to 1,
                    "precio" to item.price
                )
                transaction.set(carritoRef.document(docId), newItem)
            }
        }.await()
    }

    suspend fun getNumeroElementosCarrito(userid: String?): Int? {
        val documentos = firestore.collection(CARRITO)
            .whereEqualTo("idCliente", userid)
            .get()
            .await()
            .documents

        return documentos.fold(0) { acumulador, documento ->
            val unidades = documento.getLong("unidades")?.toInt() ?: 0
            acumulador + unidades
        }
    }
}