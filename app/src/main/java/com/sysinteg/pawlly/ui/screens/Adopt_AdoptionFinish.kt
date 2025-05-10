package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import com.sysinteg.pawlly.ui.theme.Inter

@Composable
fun AdoptFinishScreen(
    onBack: () -> Unit,
    onGoToHome: () -> Unit,
    onGoToProfile: () -> Unit
) {
        Column(
            modifier = Modifier
                .fillMaxSize()
            .background(White)
            .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logoiconpurpleround),
            contentDescription = "Pawlly Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Application Submitted!",
            style = MaterialTheme.typography.headlineMedium,
            color = Purple,
            fontFamily = Inter,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Thank you for your interest in adopting. We will review your application and get back to you soon.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            fontFamily = Inter,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onGoToHome,
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text(
                "Return to Home",
                color = White,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onGoToProfile,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Purple, RoundedCornerShape(8.dp))
        ) {
            Text(
                "Go to Profile",
                color = Purple,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium
            )
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                "Back",
                color = Purple,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium
            )
        }
    }
} 