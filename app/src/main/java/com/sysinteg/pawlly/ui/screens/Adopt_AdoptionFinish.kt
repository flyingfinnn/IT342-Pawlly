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
fun AdoptAdoptionFinishScreen(
    onReturnToProfile: () -> Unit = {},
    onAdoptMore: () -> Unit = {}
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
                "Congratulations!",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Purple,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                "Your adoption process is complete. Thank you for giving a pet a new home!",
                fontSize = 18.sp,
                color = Purple,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(
                onClick = onReturnToProfile,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Text("Return to Profile", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onAdoptMore,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Adopt More", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
} 