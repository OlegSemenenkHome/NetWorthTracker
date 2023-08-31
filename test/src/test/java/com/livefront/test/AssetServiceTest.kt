package com.livefront.test

import com.livefront.test.sampledata.SampleCryptoAsset
import com.livefront.test.sampledata.sampleAssetStock
import com.livefront.test.sampledata.sampleCryptoAsset
import com.livefront.test.sampledata.sampleCryptoListAsset
import com.livefront.test.sampledata.sampleStockAssetList
import com.livefront.test.sampledata.sampleStockListAsset
import com.livefront.test.sampledata.sampleStockListAssetList
import com.livefront.test.sampledata.sampleStockPriceResult
import com.networthtracker.data.CryptoApi
import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetType
import com.networthtracker.data.service.StockApi
import com.networthtracker.data.service.AssetServiceImpl
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import retrofit2.Response

internal class AssetServiceTest {
    //Dependencies
    private val mockkCryptoApi: CryptoApi = mockk()
    private val mockkStockApi: StockApi = mockk()

    //Under Test
    private lateinit var service: AssetServiceImpl

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        service = AssetServiceImpl(stockApi = mockkStockApi, cryptoApi = mockkCryptoApi)
    }

    @Test
    fun `getSupportedCryptoAssets fetches supported crypto assets`() = runTest {
        val expectedResult = Result.success(sampleCryptoListAsset)

        coEvery { mockkCryptoApi.getSupportedCryptoAssets() } returns Response.success(
            sampleCryptoListAsset
        )
        val asset = service.getSupportedCryptoAssets()
        assertEquals(expectedResult, asset)
    }

    @Test
    fun `getCryptoAsset fetches supported crypto assets`() = runTest {
        val expectedResult = Result.success(sampleCryptoAsset)

        coEvery { mockkCryptoApi.getAsset(any()) } returns Response.success(SampleCryptoAsset)
        val asset = service.getCryptoAsset("BTC")
        assertEquals(expectedResult, asset)
    }

    @Test
    fun `getAllStocks fetches supported stock assets and maps it to a list asset`() = runTest {
        val expectedResult = Result.success(sampleStockListAssetList)

        coEvery { mockkStockApi.getSupportedStockAssets(any(), any()) } returns Response.success(
            sampleStockAssetList
        )
        val stockList = service.getAllStocks()
        assertEquals(expectedResult, stockList)
    }

    @Test
    fun `stockLookup fetches supported stock assets and maps it to an asset`() = runTest {
        val expectedResult = Result.success(sampleAssetStock)

        coEvery { mockkStockApi.stockLookup(any(), any()) } returns Response.success(
            sampleStockPriceResult
        )
        val stockList = service.stockLookup(sampleStockListAsset[0])
        assertEquals(expectedResult, stockList)
    }

    @Test
    fun `stockPriceLookup will update the price`() = runTest {
        val oldAsset =  Asset("AAPLSTOCK", "Apple Inc", "", "100.0", "0", "AAPL", "", AssetType.STOCK)
        val expectedResult = Result.success(sampleAssetStock)

        coEvery { mockkStockApi.stockLookup(any(), any()) } returns Response.success(
            sampleStockPriceResult
        )
        val stock = service.stockPriceUpdate(oldAsset)

        assertEquals(expectedResult, stock)
        assertEquals(stock.getOrThrow().value, "200.0")
    }
}