package com.networthtracker.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetType
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreenView(
    navController: NavController,
) {
    val viewModel: HomeScreenViewModel = getViewModel()

    var active by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel.userAssetList) {
        viewModel.calculateTotalValue()
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val lifecycleObserver = remember {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPage()
            }
        }
    }

    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.secondary
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
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.onAssetSelected(item.name)
                                            active = false
                                            viewModel.clearQuery()
                                        }
                                        .animateItemPlacement()
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 30.dp)
                        .fillMaxWidth()

                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        text = "Total Portfolio Value",
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Text(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        text = "$" + viewModel.totalValue.toString().trimToNearestThousandth(),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        ) { paddingValues ->
            if (viewModel.loadingScreen) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 50.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                        )
                        Text(
                            text = "Checking for previous assets",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(paddingValues)
                ) {
                    items(viewModel.userAssetList) { asset ->
                        AssetBlock(
                            asset,
                            onCardClicked = { navController.navigate(route = "assetDetail/${asset.name.trim()}") }
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
    asset: Asset,
    onCardClicked: () -> Unit,
) {
    OutlinedCard(
        shape = RoundedCornerShape(10),
        onClick = onCardClicked,
        modifier = Modifier
            .height(120.dp)
            .padding(horizontal = 10.dp),
    ) {
        ListItem(
            leadingContent = {
                if (asset.imageURL.isNotBlank()) {
                    AsyncImage(
                        model = asset.imageURL, contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(
                                start = 10.dp,
                                end = 20.dp
                            )
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = asset.symbol,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp)
                            .padding(top = 10.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(3.dp, Color.White))
                            .padding(top = 30.dp)
                    )
                }
            },
            headlineContent = {
                Text(
                    fontSize = if (asset.name.length > 20) 16.sp
                    else 22.sp,
                    text = asset.name,
                )
            },
            trailingContent = {
                Text(
                    fontSize = 20.sp,
                    text = ("$" + asset.value.trimToNearestThousandth()) ?: "Error"
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
        asset = Asset("BTC", "Bitcoin", "", "1000", "0", "BTC", "", AssetType.CRYPTO),
        onCardClicked = {},
    )
}
