package com.networthtracker.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetType
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val BASE_URL = "https://finnhub.io/api/v1/"
private val RESOLUTION_LIST = listOf(
    Pair("15", 96),
    Pair("15", 672),
    Pair("60", 730),
    Pair("D", 365)
)

class StockRepo(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var client: HttpClient
) {

    private val apiKey = BuildConfig.API_KEY

    init {
        client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
    }

    suspend fun getAllStocks(): List<ListAsset> {
        return withContext(ioDispatcher) {
            val response = client.get(BASE_URL + "stock/symbol?exchange=US&token=" + apiKey)
            if (response.status.value == 200) {
                response.body<List<StockAsset>>().map {
                    it.toListAsset()
                }
            } else {
                throw Exception("Unable to make call, ${response.status.value} ")
            }
        }
    }

    suspend fun stockLookup(listAsset: ListAsset): Asset {
        return withContext(ioDispatcher) {
            val price = client.get(BASE_URL + "quote?symbol=${listAsset.symbol}&token=${apiKey}")
                .body<PriceResult>()
            Asset(
                key = listAsset.symbol + listAsset.assetType,
                name = listAsset.name,
                imageURL = "",
                value = price.c.toString(),
                balance = "0",
                symbol = listAsset.symbol,
                assetType = AssetType.STOCK
            )
        }
    }

    suspend fun getStockPriceHistory(
        // listAsset: ListAsset,
    ): List<CandleData> {
        return withContext(ioDispatcher) {
            val candleDataList = mutableListOf<CandleData>()
            RESOLUTION_LIST.forEach { pair ->
                candleDataList.add(
                    client.get(BASE_URL + "stock/candle?symbol=${"AAPL"}&resolution=${pair.first}&from=${0}&to=${System.currentTimeMillis()}&token=${apiKey}")
                        .body<CandleData>().also { candleData ->
                            val size = candleData.c.size
                            candleData.c =
                                ArrayList(candleData.c.subList(size - pair.second, size))
                        }
                )
            }
            candleDataList
        }
    }

    suspend fun stockPriceLookup(asset: Asset): Asset {
        return withContext(ioDispatcher) {
            val price = client.get(BASE_URL + "quote?symbol=${asset.symbol}&token=${apiKey}")
                .body<PriceResult>()
            asset.value = price.c.toString()
            asset
        }
    }

    private fun StockAsset.toListAsset(): ListAsset {
        return ListAsset(
            id = this.displaySymbol,
            name = this.description,
            symbol = this.displaySymbol,
            assetType = AssetType.STOCK
        )
    }
}
