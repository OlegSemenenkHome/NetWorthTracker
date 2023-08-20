package com.networthtracker.data

import com.networthtracker.data.room.AssetType
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
data class ListAsset(
    val id: String,
    val symbol: String,
    val name: String,
    val assetType: AssetType = AssetType.CRYPTO,
)

@JsonClass(generateAdapter = true)
data class CryptoAsset(
    val symbol: String,
    val name: String,
    val image: ImageLinks,
    val tickers: List<Ticker>?,
)

@JsonClass(generateAdapter = true)
data class ImageLinks(
    val thumb: String,
    val small: String,
    val large: String,
)

@JsonClass(generateAdapter = true)
data class Ticker(
    val last: Double,
)

@JsonClass(generateAdapter = true)
data class StockAsset(
    var description: String,
    var displaySymbol: String,
)

@JsonClass(generateAdapter = true)
data class PriceResult(
    var c: Double,
)

@JsonClass(generateAdapter = true)
data class CandleData (
    var c: List<Double>,
)