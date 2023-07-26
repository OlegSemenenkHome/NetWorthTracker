package com.example.networthtracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AssetDetailView(navController: NavHostController) {

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
                                viewModel.updateAsset(text)
                            }
                        ),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(65.dp)
                            .focusRequester(focusRequester)
                            .onGloballyPositioned { focusRequester.requestFocus() },
                    )
                }

                Spacer(modifier = Modifier.padding(20.dp))

                Spacer(modifier = Modifier.padding(20.dp))
                Text(text = "Asset Balance: ${viewModel.asset?.balance}")
                Text(text = "Asset Value: ${viewModel.asset?.value}")
                Text(text = "Total User Asset Value: ${viewModel.getAssetTotalValue()} ")
                IconButton(onClick = {
                    viewModel.deleteAsset()
                    navController.navigateUp()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
                IconButton(onClick = {
                    viewModel.asset?.let { isTextFieldVisible = !isTextFieldVisible }
                })
                { Icon(Icons.Default.Build, contentDescription = "Delete") }
            }
        }
    }
}