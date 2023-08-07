package com.networthtracker.presentation.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
internal fun ErrorDialog(
    onDismissRequest: () -> Unit,
    errorText: String,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text(
                    text = "Ok",
                    color = Color.White
                )
            }
        },
        title = {
            Text(text = "Error")
        },
        text = { Text(text = errorText) },
        modifier = modifier,
    )
}