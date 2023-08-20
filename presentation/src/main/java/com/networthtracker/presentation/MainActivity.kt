package com.networthtracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.networthtracker.data.room.AssetType
import com.networthtracker.presentation.assetdetail.AssetDetailView
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
                    composable(
                        "assetDetail/{assetKey}/",
                        arguments = listOf(
                            navArgument("assetKey") { type = NavType.StringType },
                        )
                    ) { AssetDetailView(navController) }
                }
            }
        }
    }
}