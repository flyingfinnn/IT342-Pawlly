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
import androidx.compose.ui.graphics.Color

@Composable
fun AdoptAdoptionFinishScreen(
    onReturnToHome: () -> Unit = {}
) {
    Scaffold(
        containerColor = White
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
                "Your application has been sent!",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                "Please wait until the admin audits your adoption application. We'll notify you once your application has been successfully reviewed",
                fontSize = 18.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(
                onClick = onReturnToHome,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Text("Return to Home", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
} 