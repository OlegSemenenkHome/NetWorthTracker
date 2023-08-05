package com.networthtracker.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networthtracker.data.CryptoRepo
import com.networthtracker.data.StockRepo
import com.networthtracker.data.ListAsset
import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetDao
import com.networthtracker.data.room.AssetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

private const val LOG_TAG = "HOMEVIEWMODEL"
private const val ONE_MINUTE_IN_MILLIS = 60_000L

class HomeScreenViewModel(
    private val assetDao: AssetDao,
    private val cryptoRepo: CryptoRepo = CryptoRepo(),
    private val stockRepo: StockRepo,
) : ViewModel(), KoinComponent {
    private var lastTimeUpdated = System.currentTimeMillis()

    private var assetList = emptyList<ListAsset>()

    private var loadingScreen by mutableStateOf(false)

    private var errorState by mutableStateOf(false)

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
            assetDao.getAssets()
                .collect {
                    userAssetList.clear()
                    userAssetList.addAll(it)

                    calculateTotalValue()
                    loadingScreen = true
                }
        }
    }

    fun refreshPage() {
        viewModelScope.launch {
            if (System.currentTimeMillis() - lastTimeUpdated >= ONE_MINUTE_IN_MILLIS) {
                updateAssetValues()
                calculateTotalValue()
            }
        }
    }

    private fun updateAssetValues() {
        runCatching {
            lastTimeUpdated = System.currentTimeMillis()
            viewModelScope.launch {
                userAssetList.forEach { asset ->
                    val updatedAsset = if (asset.assetType == AssetType.CRYPTO) {
                        cryptoRepo.getAsset(asset.apiName)
                    } else {
                        stockRepo.stockPriceLookup(asset)
                    }
                    assetDao.updateAssetValue(updatedAsset.value, updatedAsset.key)
                }
            }
        }.onFailure { //TODO make error state do a thing
            errorState = true
        }
    }

    fun onAssetSelected(name: String) {
        viewModelScope.launch {
            runCatching {
                filteredAssets.forEach {
                    if (it.name == name) {
                        if (it.assetType == AssetType.STOCK) {
                            addStockAsset(it)
                        } else {
                            addCryptoAsset(it)
                        }
                    }
                }
            }.onFailure { Log.e(LOG_TAG, ("Unable to select asset " + it.message)) }
        }
    }

    private suspend fun getRepoSupportedAssets() {
        runCatching {
            assetList = cryptoRepo.getSupportedCryptoAssets() + stockRepo.getAllStocks()
        }.onFailure { Log.e(LOG_TAG, ("Unable to get supported assets " + it.message)) }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                filteredAssets = getFilteredAssets(query, assetList)
            }
        }
    }

    private suspend fun addCryptoAsset(listAsset: ListAsset) {
        val cryptoAsset = cryptoRepo.getAsset(listAsset.id)
        if (cryptoAsset !in userAssetList) {
            assetDao.insertAsset(cryptoAsset)
        }
    }

    private suspend fun addStockAsset(asset: ListAsset) {
        val stockAsset = stockRepo.stockLookup(asset)
        if (stockAsset !in userAssetList) {
            assetDao.insertAsset(stockAsset)
        }
    }

    fun clearQuery() {
        searchQuery = ""
        filteredAssets = emptyList()
    }

    fun calculateTotalValue() {
        runCatching {
            totalValue = userAssetList.sumOf { asset ->
                asset.value.toDouble() * asset.balance.toDouble()
            }
        }.onFailure { errorState = true }
    }

    private fun getFilteredAssets(
        query: String,
        listAssets: List<ListAsset>,
    ): List<ListAsset> {
        if (query.isEmpty()) return emptyList()
        return listAssets.filter { it.name.lowercase().startsWith(query.lowercase()) }
    }
}
