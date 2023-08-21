package com.networthtracker.data

import com.networthtracker.data.room.Asset

/**
 * A repo to handle the fetching and saving of assets
 */
interface AssetRepository {

    suspend fun getSupportedCryptoAssets(): List<ListAsset>

    suspend fun getCryptoAsset(assetName: String): Asset

    suspend fun getAllStocks(): List<ListAsset>

    suspend fun stockLookup(listAsset: ListAsset): Asset

    suspend fun getStockPriceHistory(asset: Asset): List<CandleData>

    suspend fun stockPriceLookup(asset: Asset): Asset
}