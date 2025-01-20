package com.manuelsantos.tiendamanuel.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuelsantos.tiendamanuel.data.repositories.RemoteConnection
import com.manuelsantos.tiendamanuel.data.repositories.model.toMediaItem
import kotlinx.coroutines.launch

class ProductosViewModel: ViewModel() {
    private val _lista: MutableLiveData<List<MediaItem>> = MutableLiveData()
    val lista: LiveData<List<MediaItem>> = _lista

    private val _producto: MutableLiveData<MediaItem> = MutableLiveData()
    val producto: LiveData<MediaItem> = _producto

    private val _progressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressBar: LiveData<Boolean> = _progressBar

    fun cargarProductos() {
        _progressBar.value = true
        viewModelScope.launch() {
            val productos = RemoteConnection.remoteService.getProducts()
            _lista.value = productos.map {
                it.toMediaItem()
            }
            _progressBar.value = false
        }
    }

    fun cargarProductoId(id: String) {
        _progressBar.value = true
        viewModelScope.launch() {
            val productoPorId = RemoteConnection.remoteService.getProductById(id)
            _producto.value = productoPorId.toMediaItem()
            _progressBar.value = false
        }
    }
}