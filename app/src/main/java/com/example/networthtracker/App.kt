package com.example.networthtracker

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import com.example.networthtracker.data.AssetDatabase
import com.example.networthtracker.data.AssetRepo
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

private const val ASSET_DATABASE_NAME = "asset_database"

class App : Application() {

    private val repoModule = module {
        single { AssetRepo(get()) }
        viewModel { HomeScreenViewModel(get()) }
        viewModel { (state: SavedStateHandle) -> AssetDetailViewModel(state, get()) }
    }

    private val databaseModule =
        module {
            single {
                Room.databaseBuilder(
                    androidContext(),
                    AssetDatabase::class.java,
                    ASSET_DATABASE_NAME
                ).build()
            }
            single { get<AssetDatabase>().getAssetDao() }
        }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(androidContext = this@App)
            modules(
                listOf(
                    databaseModule,
                    repoModule,
                )
            )
        }
    }
}