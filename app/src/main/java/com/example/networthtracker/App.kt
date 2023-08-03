package com.example.networthtracker

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.networthtracker.data.room.AssetDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class App : Application() {

    private val viewModelModule = module {
        viewModel { HomeScreenViewModel(get()) }
        viewModel { (state: SavedStateHandle) -> AssetDetailViewModel(state, get()) }
    }

    private val databaseModule =
        module {
            single { AssetDatabase.getInstance(this@App).getAssetDao() }
        }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(androidContext = this@App)
            modules(
                listOf(
                    databaseModule,
                    viewModelModule,
                )
            )
        }
    }
}