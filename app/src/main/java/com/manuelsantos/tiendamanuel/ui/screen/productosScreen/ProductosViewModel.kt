package com.manuelsantos.tiendamanuel.ui.screen.productosScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuelsantos.tiendamanuel.data.model.MediaItem
import com.manuelsantos.tiendamanuel.data.repositories.RemoteConnection
import com.manuelsantos.tiendamanuel.data.repositories.model.toMediaItem
import kotlinx.coroutines.launch

class ProductosViewModel: ViewModel() {
    private val _lista: MutableLiveData<List<MediaItem>> = MutableLiveData()
    val lista: LiveData<List<MediaItem>> = _lista

    private val _progressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressBar: LiveData<Boolean> = _progressBar

    init{
        _progressBar.value = true
        viewModelScope.launch() {
            val productos = RemoteConnection.remoteService.getProducts()
            _lista.value = productos.productos.map {
                it.toMediaItem()
            }
            _progressBar.value = false
        }
    }
}