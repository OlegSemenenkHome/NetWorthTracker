package com.example.networthtracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Asset::class], version = 1)
abstract class AssetDatabase : RoomDatabase() {
    abstract fun getAssetDao(): AssetDao
}