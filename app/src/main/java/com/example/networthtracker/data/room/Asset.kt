package com.example.networthtracker.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Asset(
    @PrimaryKey
    val key: String,
    val name: String,
    val imageURL: String,
    var value: String,
    var balance: String,
    val symbol: String,
    val apiName: String = ""
)