package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import com.sysinteg.pawlly.ui.theme.Typography
import androidx.compose.foundation.BorderStroke

@Composable
fun AdoptionStepScreen(
    step: Int,
    title: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isNextEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = White,
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onBack,
                    enabled = step > 1,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Purple
                    ),
                    border = BorderStroke(1.dp, Purple)
                ) { Text("Go Back", style = Typography.bodyLarge, color = Purple) }
                Button(
                    onClick = onNext,
                    enabled = isNextEnabled,
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) { Text("Next", color = White, style = Typography.bodyLarge, fontWeight = FontWeight.Bold) }
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text("Step $step of 5", style = Typography.labelSmall, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(16.dp))
            Text(title, style = Typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            content()
            Spacer(Modifier.height(80.dp)) // To ensure content is not hidden by bottom bar
        }
    }
} 