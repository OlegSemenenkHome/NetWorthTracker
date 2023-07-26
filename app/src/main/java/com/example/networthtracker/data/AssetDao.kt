package com.example.networthtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: Asset)

    @Query("SELECT * FROM asset WHERE name = :name")
    suspend fun getAsset(name: String): Asset

    @Query("UPDATE asset SET balance = :newBalance WHERE `key` = :key")
    suspend fun updateAsset(newBalance: String, key: String)

    @Delete
    suspend fun deleteAsset(asset: Asset)

    @Query("SELECT * FROM asset")
    fun getAssets(): Flow<List<Asset>>
}