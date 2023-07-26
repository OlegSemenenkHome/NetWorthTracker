package com.example.networthtracker.data

import kotlinx.coroutines.flow.Flow

internal class AssetRepo(private val assetDao: AssetDao) {

    val allAssets: Flow<List<Asset>> = assetDao.getAssets()

    suspend fun insertAsset(asset: Asset) {
        assetDao.insertAsset(asset)
    }

    suspend fun deleteAsset(asset: Asset) {
        assetDao.deleteAsset(asset)
    }

    suspend fun updateAsset(newBalance: String, key: String) {
        assetDao.updateAsset(newBalance, key)
    }

    suspend fun getAsset(name: String): Asset {
        return assetDao.getAsset(name)
    }
}