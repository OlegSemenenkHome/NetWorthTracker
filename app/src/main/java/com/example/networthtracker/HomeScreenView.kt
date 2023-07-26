package com.example.networthtracker

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import okhttp3.internal.trimSubstring
import org.koin.androidx.compose.getViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenView(
    navController: NavController,
) {

    val viewModel: HomeScreenViewModel = getViewModel()

    var active by rememberSaveable { mutableStateOf(false) }

    val totalValue: Double by remember {
        derivedStateOf {
            var newTotalValue = 0.0
            viewModel.userAssetList.forEach { asset ->
                val value = asset.balance.toDouble()
                    .let { asset.value.toDouble().times(it) }
                newTotalValue += value
            }
            newTotalValue
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Asset Tracker")
                    },
                    actions = {
                        IconButton(onClick = { active = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Search")
                        }
                    }
                )
            },
        ) { paddingValues ->
            if (active) {
                SearchBar(
                    query = viewModel.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onSearch = {
                        viewModel.onAssetSelected(it)
                        active = false
                    },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = { Text("Look up asset") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(viewModel.filteredAssets) { item ->
                            ListItem(
                                headlineContent = { Text(text = item.name) },
                                modifier = Modifier.clickable {
                                    viewModel.onAssetSelected(item.name)
                                    active = false
                                    viewModel.clearQuery()
                                })
                        }
                    }
                }
            } else {
                if (viewModel.userAssetList.isEmpty()) {
                    Text(text = "No Assets Added")
                } else {
                    Column {
                        LazyColumn(
                            modifier = Modifier
                                .padding(paddingValues)
                                .weight(3f),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(viewModel.userAssetList) { asset ->
                                AssetBlock(
                                    symbol = asset.symbol,
                                    icon = asset.imageURL,
                                    name = asset.name,
                                    assetValue = asset.value,
                                    onCardClicked = { navController.navigate(route = "assetDetail/${asset.name}") }
                                )
                            }
                        }

                        Text(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            text = "Total Portfolio Value:",
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .fillMaxWidth()
                                .weight(.25f)

                        )
                        Text(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            text = trimToNearestThousandth(totalValue.toString()),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.25f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
@ExperimentalMaterial3Api
internal fun AssetBlock(
    icon: String,
    name: String,
    assetValue: String,
    onCardClicked: () -> Unit,
    symbol: String
) {
    OutlinedCard(
        shape = RoundedCornerShape(10),
        onClick = { onCardClicked() },
        modifier = Modifier
            .height(150.dp),
    ) {
        ListItem(
            leadingContent = {
                if (icon.isNotBlank()) {
                    AsyncImage(
                        model = icon, contentDescription = null,
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp)
                            .padding(
                                start = 10.dp,
                                end = 20.dp
                            )
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = symbol,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp)
                            .padding(top = 15.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(3.dp, Color.White))
                            .padding(top = 30.dp)
                    )
                }
            },
            headlineContent = {
                val nameLength = if (name.length > 20)
                    20 else {
                    name.length
                }
                Text(
                    fontSize = 24.sp,
                    text = name.trimSubstring(0, nameLength),
                    modifier = Modifier.weight(1f)
                )
            },
            trailingContent = {
                Text(
                    fontSize = 15.sp,
                    text = trimToNearestThousandth(assetValue)
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun AssetBlockPreview() {
    AssetBlock(
        icon = "",
        name = "BTC",
        assetValue = "20000",
        onCardClicked = {},
        symbol = "BTC"
    )
}

fun trimToNearestThousandth(value: String): String {
    return BigDecimal(value.toDouble()).setScale(3, RoundingMode.HALF_UP).toString()
}
