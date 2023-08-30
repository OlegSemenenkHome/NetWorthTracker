package com.networthtracker.data

import com.networthtracker.data.room.Asset
import kotlinx.coroutines.flow.Flow

interface AssetRepository {

    suspend fun addAsset(listAsset: ListAsset)

    suspend fun updateAssetBalance(newBalance: String, key: String): Asset

    fun getUserAssets(): Flow<List<Asset>>

}