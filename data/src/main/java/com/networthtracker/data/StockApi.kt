package com.networthtracker.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("stock/symbol")
    suspend fun getSupportedStockAssets(
        @Query("exchange") exchange: String = "US",
        @Query("token") token: String,
    ): Response<List<StockAsset>>

    @GET("quote")
    suspend fun stockLookup(
        @Query("symbol") symbol: String,
        @Query("token") token: String,
    ): Response<PriceResult>

    @GET("stock/candle")
    suspend fun getStockPriceHistory(
        @Query("symbol") symbol: String,
        @Query("token") apiToken: String,
        @Query("resolution") resolution: String,
        @Query("from") from: Long = 0,
        @Query("to") to: Long = System.currentTimeMillis(),
    ): Response<CandleData>
}