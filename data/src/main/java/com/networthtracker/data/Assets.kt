package com.networthtracker.data

import com.networthtracker.data.room.AssetType
import kotlinx.serialization.Serializable

@Serializable
data class ListAsset(
    val id: String,
    val symbol: String,
    val name: String,
    val assetType: AssetType = AssetType.CRYPTO,
)

@Serializable
data class CryptoAsset(
    val symbol: String,
    val name: String,
    val image: ImageLinks,
    val tickers: List<Ticker>?,
)

@Serializable
data class ImageLinks(
    val thumb: String,
    val small: String,
    val large: String,
)

@Serializable
data class Ticker(
    val last: Double,
)


@Serializable
data class StockAsset(
    var description: String,
    var displaySymbol: String,
)

@Serializable
data class PriceResult(
    var c: Double,
)

@Serializable
data class CandleData (
    var c: ArrayList<Double>,
)