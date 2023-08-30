package com.networthtracker.data

import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetDao
import com.networthtracker.data.room.AssetType
import kotlinx.coroutines.flow.Flow

class AssetRepositoryImpl(
    private val assetServiceImpl: AssetService,
    private val assetDao: AssetDao
) : AssetRepository {

    override suspend fun addAsset(listAsset: ListAsset) {
        runCatching {
            val asset = if (listAsset.assetType == AssetType.STOCK) {
                assetServiceImpl.stockLookup(listAsset)
            } else {
                assetServiceImpl.getCryptoAsset(listAsset.id)
            }
            asset
                .onSuccess { assetDao.insertAsset(it) }
                .onFailure { }
        }
    }

    override suspend fun updateAssetBalance(newBalance: String, key: String): Asset {
        assetDao.updateAssetBalance(newBalance, key)
        return assetDao.getAsset(key)
    }

    override fun getUserAssets(): Flow<List<Asset>> {
        return assetDao.getAssets()
    }
}