package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.background
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

@Composable
fun AdoptStartAdoptionScreen(
    onNext: () -> Unit,
    hasExistingApplication: Boolean = false,
    petName: String? = null,
    onGoToProfile: () -> Unit = {}
) {
    if (hasExistingApplication) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "You already have an existing application!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Purple,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                text = "Track your status with ${petName ?: "this pet"}'s adoption application in Profile",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Purple.copy(alpha = 0.7f),
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
    } else {
        var checked by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "You are starting the adoption process.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = CheckboxDefaults.colors(checkedColor = Purple)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "By checking this, you agree to the terms and conditions",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            if (checked) {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Next", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
} 