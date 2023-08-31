package com.livefront.test

import com.networthtracker.data.repo.AssetRepository
import com.networthtracker.data.repo.AssetRepositoryImpl
import com.networthtracker.data.AssetService
import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetDao
import com.networthtracker.data.room.AssetType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import org.junit.Test

internal class AssetRepoTest {
    private val mockService: AssetService = mockk()
    private val mockDao: AssetDao = mockk()

    // Under test
    private lateinit var repository: AssetRepository

    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        repository = AssetRepositoryImpl(assetDao = mockDao, assetServiceImpl = mockService)
    }

    @Test
    fun `Should be able to add a user Asset`(){
        coEvery { mockDao.insertAsset(any())} returns Unit
    }

}