package com.example.networthtracker.data

import com.example.networthtracker.data.room.Asset
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val COINGEKO_API_V3 = "https://api.coingecko.com/api/v3/coins/"

class CryptoRepo {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun getSupportedCryptoAssets(): List<ListAsset> {
        return runCatching {
            val response = client.get("${COINGEKO_API_V3}list?include_platform=false")
            return if (response.status.value == 200) {
                response.body()
            } else {
                throw Exception("Unable to make call, ${response.status.value} ")
            }
        }.getOrThrow<List<ListAsset>>()
    }

    suspend fun getAsset(assetName: String): Asset {
        return runCatching {
            val response =
                client.get("${COINGEKO_API_V3 + assetName}?localization=false&tickers=true&market_data=false&community_data=false&developer_data=false&sparkline=false")
            return if (response.status.value == 200) {
                mapToAsset(response.body(), assetName)
            } else {
                throw Exception("Unable to make call, ${response.status.value} ")
            }
        }.getOrThrow<Asset>()
    }

    private fun mapToAsset(asset: CryptoAsset, assetName: String): Asset {
        return Asset(
            key = asset.symbol + "false",
            name = asset.name,
            imageURL = asset.image.large,
            value = asset.tickers?.getOrNull(0)?.last.toString(),
            balance = "0",
            symbol = asset.symbol,
            apiName = assetName
        )
    }
}
