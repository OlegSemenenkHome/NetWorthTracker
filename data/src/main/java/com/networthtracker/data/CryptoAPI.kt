package com.networthtracker.data

import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetType
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val COINGEKO_API_V3 = "https://api.coingecko.com/api/v3/coins/"

class CryptoAPI(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var client: HttpClient
) {

    suspend fun getSupportedCryptoAssets(): List<ListAsset> {
        return withContext(ioDispatcher) {
            val response = client.get("${COINGEKO_API_V3}list?include_platform=false")
            if (response.status.value == 200) {
                response.body()
            } else {
                throw Exception("Unable to make call, ${response.status.value} ")
            }
        }
    }

    suspend fun getAsset(assetName: String): Asset {
        return withContext(ioDispatcher) {
            val response =
                client.get("${COINGEKO_API_V3 + assetName}?localization=false&tickers=true&market_data=false&community_data=false&developer_data=false&sparkline=false")
            if (response.status.value == 200) {
                mapToAsset(response.body(), assetName)
            } else {
                throw Exception("Unable to make call, ${response.status.value} ")
            }
        }
    }

    private fun mapToAsset(asset: CryptoAsset, assetName: String): Asset {
        return Asset(
            key = asset.symbol + AssetType.CRYPTO,
            name = asset.name,
            imageURL = asset.image.large,
            value = asset.tickers?.get(0)?.last.toString() ?: "0",
            balance = "0",
            symbol = asset.symbol,
            apiName = assetName,
            assetType = AssetType.CRYPTO
        )
    }
}
