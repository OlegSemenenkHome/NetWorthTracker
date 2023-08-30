package com.networthtracker.data

import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetDao
import com.networthtracker.data.room.AssetType
import javax.inject.Inject

class AssetServiceImpl @Inject constructor(
    private val stockApi: StockApi,
    private val cryptoApi: CryptoApi,
    private val assetDao: AssetDao
) : AssetService {
    private val RESOLUTION_LIST = listOf("15" to 96, "15" to 672, "60" to 168, "D" to 365)
    private val stockApiKey = BuildConfig.API_KEY

    override suspend fun getSupportedCryptoAssets(): Result<List<ListAsset>> {
        return runCatching {
            val response = cryptoApi.getSupportedCryptoAssets()
            val list = response.body()
            if (response.isSuccessful && list != null) {
                list
            } else {
                throw Exception(
                    "Unable to make call, code: ${response.code()}, ${
                        response.errorBody().toString()
                    }"
                )
            }
        }
    }

    override suspend fun getCryptoAsset(assetName: String): Result<Asset> {
        return runCatching {
            val response = cryptoApi.getAsset(assetName)
            val asset = response.body()
            if (response.isSuccessful && asset != null) {
                asset.mapToAsset(assetName)
            } else {
                throw Exception(
                    "Unable to make call, code: ${response.code()}, " +
                            response.errorBody().toString()
                )
            }
        }
    }

    override suspend fun getAllStocks(): Result<List<ListAsset>> {
        return runCatching {
            val response = stockApi.getSupportedStockAssets(token = stockApiKey)
            val list = response.body()
            if (response.isSuccessful && list != null) {
                list.map { it.toListAsset() }
            } else {
                throw Exception(
                    "Unable to make call, code: ${response.code()}, " +
                            response.errorBody().toString()
                )
            }
        }
    }

    override suspend fun stockLookup(listAsset: ListAsset): Result<Asset> {
        return runCatching {
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
                throw Exception(
                    "Unable to make call, code: ${response.code()}, ${
                        response.errorBody().toString()
                    }"
                )
            }
        }
    }

    override suspend fun getStockPriceHistory(
        asset: Asset
    ): Result<List<CandleData>> {
        return runCatching {
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
                    throw Exception(
                        "Unable to make call, code: ${response.code()}, ${
                            response.errorBody().toString()
                        }"
                    )
                }
            }
            candleDataList
        }
    }

    override suspend fun stockPriceLookup(asset: Asset): Result<Asset> {
        return runCatching {
            val response = stockApi.stockLookup(asset.symbol, stockApiKey)
            val price = response.body()
            if (price != null && response.isSuccessful) {
                asset.value = price.c.toString()
                asset
            } else {
                throw Exception(
                    "Unable to make call, code: ${response.code()}, ${
                        response.errorBody().toString()
                    }"
                )
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
                tickers[2].last.toString()
            } else "0",
            balance = "0",
            symbol = symbol,
            apiName = assetName,
            assetType = AssetType.CRYPTO
        )
    }
}