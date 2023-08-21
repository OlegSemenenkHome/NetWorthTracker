package com.networthtracker.data

import android.content.Context
import androidx.room.Room
import com.networthtracker.data.room.AssetDao
import com.networthtracker.data.room.AssetDatabase
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

private const val COINGEKO_API_V3 = "https://api.coingecko.com/api/v3/coins/"
private const val FINHUB_BASE_URL = "https://finnhub.io/api/v1/"

@Module
@InstallIn(SingletonComponent::class)
object HiltDataModule {

    @Provides
    @Singleton
    fun provideCryptoApi(): CryptoApi {
        return Retrofit.Builder()
            .baseUrl(COINGEKO_API_V3)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(CryptoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStockApi(): StockApi {
        return Retrofit.Builder()
            .baseUrl(FINHUB_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(StockApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAssetRepository(
        stockApi: StockApi,
        cryptoApi: CryptoApi,
        dispatcher: CoroutineDispatcher,
        assetDao: AssetDao
    ): AssetRepository {
        return AssetRepositoryImpl(
            stockApi = stockApi,
            cryptoApi = cryptoApi,
            dispatcher = dispatcher,
            assetDao = assetDao
        )
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AssetDatabase {
        return Room.databaseBuilder(
            context,
            AssetDatabase::class.java,
            "asset_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideUserDao(database: AssetDatabase): AssetDao {
        return database.getAssetDao()
    }

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
}