package com.networthtracker.presentation.assetdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networthtracker.data.AssetService
import com.networthtracker.data.repo.AssetRepository
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
    private val assetServiceImpl: AssetService,
    private val assetRepositoryImpl: AssetRepository
) : ViewModel() {
    var asset: Asset? by mutableStateOf(null)
    var errorState by mutableStateOf(false)
    var errorText by mutableStateOf("")
    var priceHistory by mutableStateOf(listOf<Double>())

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("assetKey")?.let { assetKey ->
                asset = assetDao.getAsset(assetKey)
                getPriceData()
            }
        }
    }

    private suspend fun getPriceData() {
        runCatching {
            asset?.let {
                if (it.assetType == AssetType.STOCK) {
                    priceHistory = assetServiceImpl.getStockPriceHistory(it).getOrThrow()[0].c
                } else {
                    throw (Exception("unable to get price history"))
                }
            }
        }.onFailure {
            errorState = true
            errorText = it.message.toString()
        }
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
                    asset = assetRepositoryImpl.updateAssetBalance(updatedBalance, it.key)
                }
            } else {
                errorState = true
                errorText = "Unable to update value. Please enter a valid number"
            }
        }
    }
}
