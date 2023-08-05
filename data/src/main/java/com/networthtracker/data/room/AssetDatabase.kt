package com.networthtracker.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Asset::class], version = 1)
@TypeConverters(AssetTypeConverter::class)
abstract class AssetDatabase : RoomDatabase() {
    abstract fun getAssetDao(): AssetDao

    companion object {
        @Volatile
        private var INSTANCE: AssetDatabase? = null

        fun getInstance(context: Context): AssetDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AssetDatabase::class.java,
                "assetDatabase.db"
            )
                .build()
    }
}