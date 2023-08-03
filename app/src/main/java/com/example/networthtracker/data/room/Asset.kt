package com.example.networthtracker.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity
data class Asset(
    @PrimaryKey
    val key: String,
    val name: String,
    val imageURL: String,
    var value: String,
    var balance: String,
    val symbol: String,
    val apiName: String = "",
    val assetType: AssetType
)

enum class AssetType {
    STOCK,
    CRYPTO,
    BOND
}


internal class AssetTypeConverter {

    @TypeConverter
    fun fromString(value: String?): AssetType? {
        return value?.let { enumValueOf<AssetType>(it) }
    }

    @TypeConverter
    fun statusToString(type: AssetType?): String? {
        return type?.name
    }
}