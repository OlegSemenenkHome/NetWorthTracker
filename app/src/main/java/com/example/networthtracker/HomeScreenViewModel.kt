package com.example.networthtracker

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networthtracker.data.room.Asset
import com.example.networthtracker.data.room.AssetDao
import com.example.networthtracker.data.ListAsset
import com.example.networthtracker.data.CryptoRepo
import com.example.networthtracker.data.StockRepo
import com.example.networthtracker.data.room.AssetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

private const val LOG_TAG = "HOMEVIEWMODEL"
private const val ONE_MINUTE_IN_MILLIS = 60_000L

internal class HomeScreenViewModel(
    private val assetDao: AssetDao,
    private val cryptoRepo: CryptoRepo = CryptoRepo(),
    private val stockRepo: StockRepo = StockRepo(),
) : ViewModel(), KoinComponent {
    private var lastTimeUpdated = System.currentTimeMillis()

    private var assetList = emptyList<ListAsset>()

    var filteredAssets by mutableStateOf(emptyList<ListAsset>())

    val userAssetList = mutableStateListOf<Asset>()

    var totalValue: Double by mutableDoubleStateOf(0.0)
        private set

    var searchQuery: String by mutableStateOf("")
        private set

    init {
        refreshPage()
        viewModelScope.launch {
            getRepoSupportedAssets()
        }
    }

    fun refreshPage() {
        viewModelScope.launch {
            assetDao.getAssets()
                .collect {
                    userAssetList.clear()
                    userAssetList.addAll(it)
                    calculateTotalValue()
                }
        }
    }

    fun updateAssetValues() {
        if (System.currentTimeMillis() - lastTimeUpdated >= ONE_MINUTE_IN_MILLIS) {
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
            }.onFailure { //TODO MAKE ERROR STATE }
            }
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
        if (query.length > 1) {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    filteredAssets = getFilteredAssets(query, assetList)
                }
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
        totalValue = userAssetList.sumOf { asset ->
            asset.value.toDouble() * asset.balance.toDouble()
        }
    }

    private fun getFilteredAssets(
        query: String,
        listAssets: List<ListAsset>,
    ): List<ListAsset> {
        if (query.isEmpty()) return emptyList()
        return listAssets.filter { it.name.lowercase().startsWith(query.lowercase()) }
    }
}