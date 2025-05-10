package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sysinteg.pawlly.model.LostAndFound
import com.sysinteg.pawlly.userApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportView(
    reportId: Int,
    onBack: () -> Unit,
    currentUserId: Int
) {
    var report by remember { mutableStateOf<LostAndFound?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(reportId) {
        try {
            // Since we don't have a direct endpoint for single report,
            // we'll fetch all reports and find the matching one
            val response = userApi.getAllLostAndFoundReports()
            if (response.isSuccessful) {
                report = response.body()?.find { it.reportid == reportId }
            } else {
                error = "Failed to fetch report: ${response.code()}"
            }
            isLoading = false
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    fun handleDelete() {
        scope.launch {
            try {
                val response = userApi.deleteLostAndFoundReport(reportId)
                if (response.isSuccessful) {
                    onBack()
                } else {
                    error = "Failed to delete report: ${response.code()}"
                }
            } catch (e: Exception) {
                error = e.message
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                report == null -> {
                    Text(
                        text = "Report not found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Report Type and ID
                        Text(
                            text = "${report!!.reporttype?.uppercase() ?: ""} #${report!!.reportid ?: -1}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (report!!.reporttype?.equals("lost", ignoreCase = true) == true) Color.Red else Color.Green
                        )

                        // Date Reported
                        Text(
                            text = "Date Reported: ${report!!.datereported ?: "Unknown"}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Pet Name
                        Text(
                            text = "Pet: ${report!!.petname ?: "Unknown"}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Last Seen Location
                        Text(
                            text = "Last Seen: ${report!!.lastseen ?: "Unknown"}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Description
                        Text(
                            text = "Description:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = report!!.description ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Display image if available
                        if (report!!.imageurl != null) {
                            AsyncImage(
                                model = report!!.imageurl,
                                contentDescription = "Report Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Show Edit and Delete buttons only if the current user is the creator
                        if (report!!.creatorid == currentUserId) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showEditDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Edit")
                                }
                                Button(
                                    onClick = { handleDelete() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && report != null) {
        EditReportDialog(
            report = report!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedReport ->
                scope.launch {
                    try {
                        val response = userApi.updateLostAndFoundReport(
                            id = updatedReport.reportid ?: -1,
                            reportType = updatedReport.reporttype ?: "LOST",
                            petName = updatedReport.petname ?: "Unknown",
                            dateReported = updatedReport.datereported ?: "Unknown",
                            lastSeen = updatedReport.lastseen ?: "Unknown",
                            description = updatedReport.description ?: ""
                        )
                        if (response.isSuccessful) {
                            report = updatedReport.copy(imageurl = report!!.imageurl)
                            showEditDialog = false
                        } else {
                            error = "Failed to update report: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        error = e.message
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReportDialog(
    report: LostAndFound,
    onDismiss: () -> Unit,
    onSave: (LostAndFound) -> Unit
) {
    var reportType by remember { mutableStateOf(report.reporttype ?: "LOST") }
    var petName by remember { mutableStateOf(report.petname ?: "Unknown") }
    var lastSeen by remember { mutableStateOf(report.lastseen ?: "Unknown") }
    var description by remember { mutableStateOf(report.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Report") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Report Type Selection
                Text("Report Type", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = reportType == "LOST",
                        onClick = { reportType = "LOST" },
                        label = { Text("Lost") }
                    )
                    FilterChip(
                        selected = reportType == "FOUND",
                        onClick = { reportType = "FOUND" },
                        label = { Text("Found") }
                    )
                }

                // Pet Name
                OutlinedTextField(
                    value = petName,
                    onValueChange = { petName = it },
                    label = { Text("Pet Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Last Seen Location
                OutlinedTextField(
                    value = lastSeen,
                    onValueChange = { lastSeen = it },
                    label = { Text("Last Seen Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        report.copy(
                            reporttype = reportType,
                            petname = petName.trim(),
                            lastseen = lastSeen.trim(),
                            description = description.trim()
                        )
                    )
                }
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 