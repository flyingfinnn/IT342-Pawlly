package com.sysinteg.pawlly.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun AdoptionConfirmationDialog(
    petName: String,
    onCancel: () -> Unit,
    onContinue: () -> Unit,
    onDismiss: () -> Unit = onCancel
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Start Adoption Process?", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Text("Are you sure you want to start the adoption process for $petName?")
        },
        confirmButton = {
            Button(onClick = onContinue) {
                Text("Continue")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
} 