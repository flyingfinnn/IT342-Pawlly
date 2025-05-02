package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import androidx.compose.ui.graphics.Color
import com.sysinteg.pawlly.ui.screens.StatusType
import com.sysinteg.pawlly.ui.screens.AnimatedStatusChip
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AdoptAdoptionStep1Screen(
    userName: String = "John Doe",
    userEmail: String = "john@example.com",
    onStart: () -> Unit = {}
) {
    var acceptedTerms by remember { mutableStateOf(false) }
    var statusType by remember { mutableStateOf(StatusType.None) }
    var showStatusChip by remember { mutableStateOf(false) }

    LaunchedEffect(statusType) {
        if (statusType != StatusType.None) {
            showStatusChip = true
            delay(3000)
            showStatusChip = false
            statusType = StatusType.None
        }
    }

    Scaffold(
        containerColor = White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
            ) {
                // Removed back button and icon
            }
        },
        bottomBar = {
            // No button in bottomBar; button will be in main Column
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Status chip in top right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                AnimatedStatusChip(visible = showStatusChip, status = statusType)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Start Adoption", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Purple)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Name: $userName", fontSize = 18.sp, color = Color.Black)
                Text("Email: $userEmail", fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = acceptedTerms, onCheckedChange = { acceptedTerms = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I accept the ", color = Color.Black)
                    Text("terms and conditions", color = Color.Black, style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline))
                }
                if (acceptedTerms) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text("Continue", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
} 