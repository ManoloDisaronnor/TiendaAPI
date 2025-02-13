package com.manuelsantos.tiendamanuel.data.firebase

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.manuelsantos.tiendamanuel.data.model.MediaItem
import com.manuelsantos.tiendamanuel.data.repositories.db.CarritoDB
import com.manuelsantos.tiendamanuel.data.repositories.model.ProductoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FirestoreViewModel(
    private val firestoreManager: FirestoreManager
) : ViewModel() {

    private val _firestoreProducts = MutableLiveData<List<ProductoItem>>()
    val firestoreProducts: LiveData<List<ProductoItem>> = _firestoreProducts

    private val _firestoreProduct = MutableLiveData<ProductoItem>()
    val firestoreProduct: LiveData<ProductoItem> = _firestoreProduct

    private val _numeroElementosCarrito = MutableLiveData<Int>()
    val numeroElementosCarrito: LiveData<Int> = _numeroElementosCarrito

    private val _carrito = MutableLiveData<List<ProductoItem>>()
    val carrito: LiveData<List<ProductoItem>> = _carrito

    private val _syncState = MutableLiveData<SyncState>()
    val syncState: LiveData<SyncState> = _syncState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    sealed class SyncState {
        object Loading : SyncState()
        data class Success(val message: String) : SyncState()
        data class Error(val exception: Throwable) : SyncState()
    }

    // Cargar productos de Firestore
    suspend fun loadFirestoreProducts() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            try {
                firestoreManager.getProductos()
                    .collect { productos ->
                        _firestoreProducts.value = productos
                    }
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e)
            }
        }
    }

    // Comparar y sincronizar productos
    suspend fun syncProducts(apiProducts: List<MediaItem>) {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            _isLoading.value = true
            try {
                val firestoreProducts = _firestoreProducts.value ?: emptyList()
                val apiProductosConvertidos = apiProducts.map { it.toProductoItem() }
                if (debenSincronizarse(apiProductosConvertidos, firestoreProducts)) {
                    firestoreManager.deleteAllProducts()
                    apiProductosConvertidos.forEach { producto ->
                        firestoreManager.addProducto(producto)
                    }
                } else {
                    _syncState.value = SyncState.Success("No se necesitan cambios")
                }
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e)
            }
            _isLoading.value = false
        }
    }

    suspend fun cargarProductoPorId(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val producto = firestoreManager.getProductoById(id)
            _firestoreProduct.value = producto
        }
        _isLoading.value = false
    }

    // Lógica de comparación para saber si hay que sincronizar los datos de Firestore
    private fun debenSincronizarse(
        apiList: List<ProductoItem>,
        firestoreList: List<ProductoItem>
    ): Boolean {
        return apiList.size != firestoreList.size || !apiList.containsAll(firestoreList)
    }

    fun addCarrito(item: ProductoItem, userid: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                firestoreManager.addCarrito(item, userid)
                _numeroElementosCarrito.value = _numeroElementosCarrito.value?.plus(1) ?: 1
                _syncState.value = SyncState.Success("Producto añadido al carrito")
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e)
            }
        }
        _isLoading.value = false
    }

    fun getNumeroElementosCarrito(userid: String?) {
        viewModelScope.launch {
            _numeroElementosCarrito.value = firestoreManager.getNumeroElementosCarrito(userid)
        }
    }

    fun getCarrito(userid: String?, context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            val carrito: Flow<List<CarritoDB>>
            if (userid != null) {
                carrito = firestoreManager.getCarrito(userid)
                carrito.collect { carritoDB ->
                    _carrito.value = carritoDB.map { it.producto }
                }
            } else {
                Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
        _isLoading.value = false
    }

    class FirestoreViewModelFactory(
        private val firestoreManager: FirestoreManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FirestoreViewModel(firestoreManager) as T
        }
    }
}

