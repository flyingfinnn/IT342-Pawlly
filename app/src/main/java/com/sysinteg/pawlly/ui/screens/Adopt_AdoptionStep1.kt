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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun AdoptAdoptionStep1Screen(
    userName: String = "John Doe",
    userEmail: String = "john@example.com",
    onStart: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var acceptedTerms by remember { mutableStateOf(false) }
    Scaffold(
        containerColor = White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 4.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Purple
                        )
                    }
                }
            }
        },
        bottomBar = {
            // No button in bottomBar; button will be in main Column
        }
    ) { innerPadding ->
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
            Text("Name: $userName", fontSize = 18.sp)
            Text("Email: $userEmail", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = acceptedTerms, onCheckedChange = { acceptedTerms = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text("I accept the terms and conditions.")
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