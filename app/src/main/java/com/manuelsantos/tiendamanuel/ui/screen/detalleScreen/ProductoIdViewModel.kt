package com.manuelsantos.tiendamanuel.ui.screen.detalleScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuelsantos.tiendamanuel.data.model.MediaItem
import com.manuelsantos.tiendamanuel.data.repositories.RemoteConnection
import com.manuelsantos.tiendamanuel.data.repositories.model.toMediaItem
import kotlinx.coroutines.launch

class ProductoIdViewModel(productoId: String): ViewModel() {
    private val _producto: MutableLiveData<MediaItem> = MutableLiveData()
    val producto: LiveData<MediaItem> = _producto

    private val _progressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressBar: LiveData<Boolean> = _progressBar

    init{
        _progressBar.value = true
        viewModelScope.launch() {
            val productoPorId = RemoteConnection.remoteService.getProductById(productoId)
            _producto.value = productoPorId.toMediaItem()
            _progressBar.value = false
        }
    }
}