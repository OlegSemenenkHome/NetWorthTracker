package com.networthtracker.data
/*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@ExperimentalCoroutinesApi
class CryptoAPITest {

    private lateinit var cryptoAPI: CryptoAPI
    private val testDispatcher =StandardTestDispatcher()
    private val listAsset = listOf( ListAsset("bitcoin", "BTC", "Bitcoin"))

    @BeforeEach
    fun setup() {
        clearAllMocks()

    }

    @Test
    fun `test getSupportedCryptoAssets success`() = testApplication {
        val mockResponse: HttpResponse = mockk<HttpResponse>()
        val repo = CryptoRepo(testDispatcher, client)
        coEvery { mockResponse.status.value } returns 200
        coEvery { mockResponse.body<List<ListAsset>>() } returns listAsset
        coEvery { client.get(urlString = "") } returns mockResponse

        val result = repo.getSupportedCryptoAssets()

        assertEquals(listAsset, result)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}

*/