package com.networthtracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.networthtracker.presentation.AssetDetailView
import com.networthtracker.presentation.HomeScreenView
import com.networthtracker.presentation.ui.theme.NetWorthTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetWorthTrackerTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreenView(navController) }
                    composable("assetDetail/{assetName}") { AssetDetailView(navController) }
                }
            }
        }
    }
}