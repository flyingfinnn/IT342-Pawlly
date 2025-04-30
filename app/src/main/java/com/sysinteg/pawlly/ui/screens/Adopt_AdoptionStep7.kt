package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White

@Composable
fun AdoptAdoptionStep7Screen(
    onReturnToProfile: () -> Unit = {},
    onAdoptMore: () -> Unit = {}
) {
    Scaffold(
        containerColor = White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    "Step 7 of 7",
                    color = Purple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onReturnToProfile,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                    Text("Return to Profile", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                OutlinedButton(
                    onClick = onAdoptMore,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Adopt More", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
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
            Text(
                "Thank you for completing your adoption application!",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Purple,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                "Our team will review your submission and contact you soon.",
                fontSize = 16.sp,
                color = Purple,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
} 