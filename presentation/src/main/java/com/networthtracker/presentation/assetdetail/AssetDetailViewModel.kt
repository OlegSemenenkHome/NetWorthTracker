package com.networthtracker.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetDao
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class AssetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val assetDao: AssetDao
) : ViewModel(), KoinComponent {
    var asset: Asset? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            asset = assetDao.getAsset(savedStateHandle.get<String>("assetName").orEmpty())
        }
    }

    fun getAssetTotalValue(): String {
        return asset?.run {
            val balanceValue = balance.toDouble()
            val assetValue = value.toDouble()
            (balanceValue * assetValue).toString().trimToNearestThousandth()
        } ?: "0"
    }

    fun deleteAsset() {
        viewModelScope.launch {
            asset?.let { assetDao.deleteAsset(it) }
        }
    }

    fun updateAsset(updatedBalance: String) {
        viewModelScope.launch {
            asset?.let {
                assetDao.updateAssetBalance(updatedBalance, it.key)
                asset = assetDao.getAsset(it.name)
            }
        }
    }
}

