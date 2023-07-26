package com.example.networthtracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networthtracker.data.Asset
import com.example.networthtracker.data.AssetRepo
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

internal class AssetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val assetRepo: AssetRepo
) : ViewModel(), KoinComponent {
    var asset: Asset? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            asset = assetRepo.getAsset(savedStateHandle.get<String>("assetName").orEmpty())
        }
    }

    fun getAssetTotalValue(): String {
        return asset?.balance?.toDouble()?.let { asset?.value?.trim()?.toDouble()?.times(it) }
            .toString()
    }

    fun deleteAsset() {
        viewModelScope.launch {
            asset?.let { assetRepo.deleteAsset(it) }
        }
    }

    fun updateAsset(updatedBalance: String) {
        viewModelScope.launch {
            asset?.let {
                assetRepo.updateAsset(updatedBalance, it.key)
                asset = assetRepo.getAsset(it.name)
            }
        }
    }
}

