package com.livefront.test

import android.util.Log
import com.livefront.test.sampledata.sampleCryptoListAsset
import com.livefront.test.sampledata.sampleStockListAsset
import com.networthtracker.data.AssetService
import com.networthtracker.data.repo.AssetRepository
import com.networthtracker.presentation.HomeScreenViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

internal class HomeScreenViewModelTest {

    private val mockService: AssetService = mockk()
    private val asssetRepo: AssetRepository = mockk()

    //Under Test
    private lateinit var vm: HomeScreenViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any<String>(), any()) } returns 0
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `We should fetch assets on initialization and be in the correct state`() = runTest {
        // Given
        coEvery { asssetRepo.getUserAssets() } returns flowOf(emptyList())
        coEvery { mockService.getSupportedCryptoAssets() } returns Result.success(sampleCryptoListAsset)
        coEvery { mockService.getAllStocks() } returns  Result.success(sampleStockListAsset)

        vm = HomeScreenViewModel(assetServiceImpl = mockService, assetRepository = asssetRepo)

        Thread.sleep(1000L)

        assertFalse(vm.loadingScreen)
        assert(vm.userAssetList.isEmpty())
    }
}