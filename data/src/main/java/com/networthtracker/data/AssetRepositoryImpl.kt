package com.networthtracker.data

import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetDao
import com.networthtracker.data.room.AssetType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val stockApi: StockApi,
    private val cryptoApi: CryptoApi,
    private val dispatcher: CoroutineDispatcher,
    private val assetDao: AssetDao
) : AssetRepository {
    private val RESOLUTION_LIST = listOf("15" to 96, "15" to 672, "60" to 168, "D" to 365)
    private val stockApiKey = BuildConfig.API_KEY

    override suspend fun getSupportedCryptoAssets(): List<ListAsset> {
        return withContext(dispatcher) {
            val response = cryptoApi.getSupportedCryptoAssets()
            val list = response.body()
            if (response.isSuccessful && list != null) {
                list
            } else {
                throw Exception("Unable to make call, ${response.code()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getCryptoAsset(assetName: String): Asset {
        return withContext(dispatcher) {
            val response = cryptoApi.getAsset(assetName)
            val asset = response.body()
            if (response.isSuccessful && asset != null) {
                asset.mapToAsset(assetName)
            } else {
                throw Exception("Unable to make call, ${response.code()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getAllStocks(): List<ListAsset> {
        return withContext(dispatcher) {
            val response = stockApi.getSupportedStockAssets(token = stockApiKey)
            val list = response.body()
            if (response.isSuccessful && list != null) {
                list.map { it.toListAsset() }
            } else {
                throw Exception("Unable to make call, ${response.code()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun stockLookup(listAsset: ListAsset): Asset {
        return withContext(dispatcher) {
            val response = stockApi.stockLookup(listAsset.symbol, stockApiKey)
            val price = response.body()
            if (price != null && response.isSuccessful) {
                Asset(
                    key = listAsset.symbol + listAsset.assetType,
                    name = listAsset.name,
                    imageURL = "",
                    value = price.c.toString(),
                    balance = "0",
                    symbol = listAsset.symbol,
                    assetType = AssetType.STOCK
                )
            } else {
                throw Exception("Unable to make call, ${response.code()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getStockPriceHistory(
        asset: Asset
    ): List<CandleData> {
        return withContext(dispatcher) {
            val candleDataList = mutableListOf<CandleData>()
            RESOLUTION_LIST.forEach { pair ->
                val response = stockApi.getStockPriceHistory(
                    symbol = asset.symbol,
                    resolution = pair.first,
                    apiToken = stockApiKey
                )
                val data = response.body()
                if (response.isSuccessful && data != null) {
                    candleDataList.add(data)
                } else {
                    throw Exception("Unable to make call, ${response.code()}, ${response.errorBody()}")
                }
            }
            candleDataList
        }
    }

    override suspend fun stockPriceLookup(asset: Asset): Asset {
        return withContext(dispatcher) {
            val response = stockApi.stockLookup(asset.symbol, stockApiKey)
            val price = response.body()
            if (price != null && response.isSuccessful) {
                asset.value = price.c.toString()
                asset
            } else {
                throw Exception("Unable to make call, ${response.code()}, ${response.errorBody()}")
            }
        }
    }

    private fun StockAsset.toListAsset(): ListAsset {
        return ListAsset(
            id = this.displaySymbol,
            name = this.description,
            symbol = this.displaySymbol,
            assetType = AssetType.STOCK
        )
    }

    private fun CryptoAsset.mapToAsset(assetName: String): Asset {
        return Asset(
            key = symbol + AssetType.CRYPTO,
            name = name,
            imageURL = image.large,
            value = if (tickers?.isNotEmpty() == true) {
                tickers[0].last.toString()
            } else "0",
            balance = "0",
            symbol = symbol,
            apiName = assetName,
            assetType = AssetType.CRYPTO
        )
    }
}