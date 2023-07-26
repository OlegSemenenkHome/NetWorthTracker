package com.example.networthtracker.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.example.networthtracker.BuildConfig
import com.example.networthtracker.data.room.Asset
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val BASE_URL = "https://finnhub.io/api/v1/"

class StockRepo {
    private val apiKey = BuildConfig.API_KEY

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun getAllStocks(): List<ListAsset> {
        return runCatching {
            val response = client.get(BASE_URL + "stock/symbol?exchange=US&token=" + apiKey)
            return if (response.status.value == 200) {
                response.body<List<StockAsset>>().map {
                    it.toListAsset()
                }
            } else {
                throw Exception("Unable to make call, ${response.status.value} ")
            }
        }.getOrThrow<List<ListAsset>>()
    }

    suspend fun stockLookup(listAsset: ListAsset): Asset {
        val price = client.get(BASE_URL + "quote?symbol=${listAsset.symbol}&token=${apiKey}")
            .body<PriceResult>()
        return Asset(
            key = listAsset.symbol + listAsset.isStock.toString(),
            name = listAsset.name,
            imageURL = "",
            value = price.c.toString(),
            balance = "0",
            symbol = listAsset.symbol
        )
    }

    suspend fun stockPriceLookup(asset: Asset): Asset {
        val price = client.get(BASE_URL + "quote?symbol=${asset.symbol}&token=${apiKey}")
            .body<PriceResult>()
        asset.value = price.c.toString()
        return asset
    }

    private fun StockAsset.toListAsset(): ListAsset {
        return ListAsset(
            id = this.displaySymbol,
            name = this.description,
            symbol = this.displaySymbol,
            isStock = true
        )
    }
}
