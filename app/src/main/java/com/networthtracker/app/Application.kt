package com.networthtracker.app

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.networthtracker.data.StockRepo
import com.networthtracker.app.presentation.AssetDetailViewModel
import com.networthtracker.app.presentation.HomeScreenViewModel
import com.networthtracker.data.room.AssetDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class Application : Application() {

    private val viewModelModule = module {
        viewModel { HomeScreenViewModel(assetDao = get(), stockRepo = get()) }
        viewModel { (state: SavedStateHandle) -> AssetDetailViewModel(state, get()) }
    }

    private val databaseModule =
        module {
            single { AssetDatabase.getInstance(this@Application).getAssetDao() }
            single { StockRepo(client = client) }
        }

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(androidContext = this@Application)
            modules(
                listOf(
                    databaseModule,
                    viewModelModule,
                )
            )
        }
    }
}