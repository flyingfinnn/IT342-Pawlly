package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sysinteg.pawlly.model.LostAndFound
import com.sysinteg.pawlly.ui.components.ReportCard
import com.sysinteg.pawlly.userApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostAndFoundHome(
    onAddReport: () -> Unit,
    onReportClick: (Int) -> Unit
) {
    var reports by remember { mutableStateOf<List<LostAndFound>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val response = userApi.getAllLostAndFoundReports()
            if (response.isSuccessful) {
                reports = response.body() ?: emptyList()
            } else {
                val errorBody = response.errorBody()?.string()
                error = "Failed to fetch reports: ${response.code()} - $errorBody"
            }
            isLoading = false
        } catch (e: Exception) {
            error = "Error: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lost & Found") },
                actions = {
                    IconButton(onClick = onAddReport) {
                        Icon(Icons.Default.Add, contentDescription = "Add Report")
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
                reports.isEmpty() -> {
                    Text(
                        text = "No reports found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reports) { report ->
                            ReportCard(
                                report = report,
                                onClick = { onReportClick(report.reportid ?: -1) }
                            )
                        }
                    }
                }
            }
        }
    }
} 