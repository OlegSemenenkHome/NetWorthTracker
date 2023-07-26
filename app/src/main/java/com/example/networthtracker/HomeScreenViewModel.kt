package com.example.networthtracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networthtracker.data.Asset
import com.example.networthtracker.data.AssetRepo
import com.example.networthtracker.data.ListAsset
import com.example.networthtracker.data.CryptoRepo
import com.example.networthtracker.data.StockRepo
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

internal class HomeScreenViewModel(
    private val assetRepo: AssetRepo,
    private val cryptoRepo: CryptoRepo = CryptoRepo(),
    private val stockRepo: StockRepo = StockRepo()
) : ViewModel(), KoinComponent {
    private var assetList by mutableStateOf(emptyList<ListAsset>())

    var filteredAssets by mutableStateOf(emptyList<ListAsset>())

    var userAssetList = mutableStateListOf<Asset>()

    var searchQuery: String by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {

            getRepoSupportedAssets()
            assetRepo.allAssets.collect {
                userAssetList.clear()
                userAssetList.addAll(it)
            }
        }
    }

    fun onAssetSelected(name: String) {
        viewModelScope.launch {
            filteredAssets.forEach {
                try {
                    if (it.name == name) {
                        if (it.isStock) {
                            addStockAsset(it)
                        } else {
                            addCryptoAsset(it)
                        }
                    }
                } catch (e: Exception) {
                    throw e
                }
            }
        }
    }

    private fun getRepoSupportedAssets() {
        try {
            viewModelScope.launch {
                assetList = cryptoRepo.getSupportedCryptoAssets() + stockRepo.getAllStocks()
            }
        } catch (e: Exception) {
            println("Unable to get Data $e")
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        if (query.length > 1) {
            viewModelScope.launch {
                filteredAssets = getFilteredAssets(query, assetList)
            }
        }
    }

    private suspend fun addCryptoAsset(listAsset: ListAsset) {
        val cryptoAsset = cryptoRepo.getAsset(listAsset.id)
        if (!userAssetList.contains(cryptoAsset)) {
            assetRepo.insertAsset(cryptoAsset)
        }
    }

    private suspend fun addStockAsset(asset: ListAsset) {
        val stockAsset = stockRepo.stockLookup(asset)
        if (!userAssetList.contains(stockAsset)) {
            assetRepo.insertAsset(stockAsset)
        }
    }

    fun clearQuery() {
        searchQuery = ""
        filteredAssets = emptyList()
    }

    private fun getFilteredAssets(
        query: String,
        cryptoListAsset: List<ListAsset>,
    ): List<ListAsset> {
        if (query.isEmpty()) return emptyList()
        return cryptoListAsset.filter { it.name.lowercase().startsWith(query.lowercase()) }
    }
}