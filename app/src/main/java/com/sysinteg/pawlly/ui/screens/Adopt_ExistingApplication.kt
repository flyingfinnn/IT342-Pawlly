package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White

@Composable
fun AdoptExistingApplicationScreen(
    petName: String? = null,
    onGoToProfile: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You already have an existing application for this pet!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "Track your status with ${petName ?: "this pet"}'s adoption application in Profile",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp),
            lineHeight = 22.sp
        )
        Button(
            onClick = onGoToProfile,
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Go to Profile", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
} 