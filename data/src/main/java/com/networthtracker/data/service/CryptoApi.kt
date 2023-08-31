package com.networthtracker.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoApi {
    @GET("list?include_platform=false")
    suspend fun getSupportedCryptoAssets(
        @Query("include_platform") includePlat: Boolean = false
    ): Response<List<ListAsset>>

    @GET("{id}/")
    suspend fun getAsset(
        @Path("id") coinId: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = true,
        @Query("market_data") marketData: Boolean = false,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("sparkline") sparkline: Boolean = false
    ): Response<CryptoAsset>
}
