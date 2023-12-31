package com.networthtracker.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networthtracker.data.AssetService
import com.networthtracker.data.ListAsset
import com.networthtracker.data.repo.AssetRepository
import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val LOG_TAG = "HOMEVIEWMODEL"
private const val ONE_MINUTE_IN_MILLIS = 60_000L

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val assetServiceImpl: AssetService,
    private val assetRepository: AssetRepository
) : ViewModel() {
    private var lastTimeUpdated = System.currentTimeMillis()

    private var assetList = emptyList<ListAsset>()

    var loadingScreen by mutableStateOf(false)

    var errorState by mutableStateOf(false)
        private set

    var errorString by mutableStateOf("")
        private set

    var filteredAssets by mutableStateOf(emptyList<ListAsset>())

    val userAssetList = mutableStateListOf<Asset>()

    var totalValue: Double by mutableDoubleStateOf(0.0)
        private set

    var searchQuery: String by mutableStateOf("")
        private set

    init {
        loadingScreen = true
        viewModelScope.launch {

            getRepoSupportedAssets()
            assetRepository.getUserAssets()
                .flowOn(Dispatchers.IO)
                .collect {
                    userAssetList.clear()
                    userAssetList.addAll(it)

                    calculateTotalValue()
                    loadingScreen = false
                }
        }
    }

    fun refreshPage() {
        viewModelScope.launch {
            if (System.currentTimeMillis() - lastTimeUpdated >= ONE_MINUTE_IN_MILLIS) {
                updateAssetValues()
            }
        }
    }

    fun onAssetSelected(name: String) {
        viewModelScope.launch {
            runCatching {
                filteredAssets.forEach {
                    if (it.name == name) {
                        assetRepository.addAsset(it)
                    }
                }
            }.onFailure {
                errorString = "Unable to update asset values"
                errorState = true
                Log.e(LOG_TAG, ("Unable to select asset: "), it)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                filteredAssets = getFilteredAssets(query, assetList)
            }
        }
    }

    fun clearQuery() {
        searchQuery = ""
        filteredAssets = emptyList()
    }

    fun dismissError() {
        errorString = ""
        errorState = false
    }

    private fun calculateTotalValue() {
        runCatching {
            totalValue = userAssetList.sumOf { asset ->
                asset.value.toDouble() * asset.balance.toDouble()
            }
        }
    }

    private fun getFilteredAssets(
        query: String,
        listAssets: List<ListAsset>,
    ): List<ListAsset> {
        if (query.isEmpty()) return emptyList()
        return listAssets.filter { it.name.lowercase().startsWith(query.lowercase()) }
    }

    private suspend fun getRepoSupportedAssets() {
        runCatching {
            assetList =
                assetServiceImpl.getSupportedCryptoAssets()
                    .getOrThrow() + assetServiceImpl.getAllStocks().getOrThrow()
        }.onFailure {
            errorString = "Unable to update asset values"
            errorState = true
            Log.e(LOG_TAG, ("Unable to get supported assets "), it)
        }
    }

    private fun updateAssetValues() {
        loadingScreen = true
        runCatching {
            lastTimeUpdated = System.currentTimeMillis()
            viewModelScope.launch {
                userAssetList.forEach { asset ->
                    asset.value = if (asset.assetType == AssetType.CRYPTO) {
                        assetServiceImpl.getCryptoAsset(asset.apiName).getOrThrow().value
                    } else {
                        assetServiceImpl.stockPriceUpdate(asset).getOrThrow().value
                    }
                }
            }
        }.onFailure {
            errorString = "Unable to update asset values"
            errorState = true
            Log.e(LOG_TAG, ("Unable to get supported assets "), it)
        }
        loadingScreen = false
    }
}
