package com.networthtracker.presentation.assetdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.getViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.networthtracker.presentation.trimToNearestThousandth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailView(navController: NavHostController) {
    val viewModel: AssetDetailViewModel = getViewModel()

    var text by remember { mutableStateOf("") }
    var isTextFieldVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Details") },
                    modifier = Modifier,
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigateUp() },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    }
                )
            }) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                if (isTextFieldVisible) {
                    Text(
                        text = "Update Balance",
                        fontSize = 30.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                isTextFieldVisible = false
                                if (text.matches(Regex("[0-9 ]+"))) {
                                    viewModel.updateAsset(text)
                                }
                            }
                        ),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(65.dp)
                            .focusRequester(focusRequester)
                            .onGloballyPositioned { focusRequester.requestFocus() },
                    )
                } else {
                    viewModel.asset?.name?.let { name ->

                        Text(
                            text = name,
                            fontSize = if (name.length > 20) 20.sp
                            else 30.sp,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Text(
                            text = " ${viewModel.asset?.value?.trimToNearestThousandth()}",
                            fontSize = 30.sp
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                viewModel.deleteAsset()
                                navController.navigateUp()
                            },
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Remove Asset",
                                color = Color.White
                            )
                        }

                        Button(
                            enabled = !isTextFieldVisible,
                            onClick = {
                                viewModel.asset?.let {
                                    isTextFieldVisible = !isTextFieldVisible
                                }
                            },
                            modifier = Modifier.padding(20.dp)
                        )
                        {
                            Text(
                                text = "Update Balance",
                                color = Color.White
                            )
                        }
                    }
                    Text(
                        text = "Balance: ${viewModel.asset?.balance}",
                        fontSize = 30.sp
                    )

                    Text(
                        text = "Total Value: ${viewModel.getAssetTotalValue()} ",
                        fontSize = 25.sp
                    )
                }
            }
        }
    }
}
