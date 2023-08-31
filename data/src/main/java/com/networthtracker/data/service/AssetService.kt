package com.networthtracker.data

import com.networthtracker.data.room.Asset

/**
 * A repo to handle the fetching and saving of assets
 */
interface AssetService {

    suspend fun getSupportedCryptoAssets(): Result<List<ListAsset>>

    suspend fun getCryptoAsset(assetName: String): Result<Asset>

    suspend fun getAllStocks(): Result<List<ListAsset>>

    suspend fun stockLookup(listAsset: ListAsset): Result<Asset>

    suspend fun getStockPriceHistory(asset: Asset): Result<List<CandleData>>

    suspend fun stockPriceUpdate(asset: Asset): Result<Asset>
}