package com.networthtracker.presentation.assetdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networthtracker.data.AssetRepositoryImpl
import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetDao
import com.networthtracker.data.room.AssetType
import com.networthtracker.presentation.trimToNearestThousandth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val assetDao: AssetDao,
    private val assetRepositoryImpl: AssetRepositoryImpl
) : ViewModel() {
    var asset: Asset? by mutableStateOf(null)
    var errorState by mutableStateOf(false)
    var errorText by mutableStateOf("")
    var priceHistory by mutableStateOf(listOf<Double>())

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("assetKey")?.let { assetKey ->
                asset = assetDao.getAsset(assetKey)
                priceHistory = priceHistory + getPriceData()
            }
        }
    }

    private suspend fun getPriceData(): List<Double> {
        asset?.let {
            if (it.assetType == AssetType.STOCK) {
                return assetRepositoryImpl.getStockPriceHistory(it)[0].c
            }
        }
        return listOf()
    }

    fun dismissError() {
        errorText = ""
        errorState = false
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
            if (updatedBalance.matches(Regex("[0-9 ]+"))) {
                asset?.let {
                    assetDao.updateAssetBalance(updatedBalance, it.key)
                    asset = assetDao.getAsset(it.key)
                }
            } else {
                errorState = true
                errorText = "Unable to update value. Please enter a valid number"
            }
        }
    }
}
