package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.ui.theme.Inter
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.BoneWhite
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.Spacer

@Composable
fun NotificationScreen(
    onNavHome: () -> Unit = {},
    onNavNotifications: () -> Unit = {},
    onNavProfile: () -> Unit = {},
    selectedScreen: String = "Notifications"
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(BoneWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        // Blurred/semi-transparent status bar overlay
        Box(
            Modifier
                .fillMaxWidth()
                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .background(BoneWhite.copy(alpha = 0.7f))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(top = 32.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notifications",
                fontFamily = Inter,
                fontSize = 28.sp,
                color = Purple,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            NotificationItem(title = "Adoption Request", body = "You have a new adoption request for Bella.", time = "2 min ago")
            Spacer(modifier = Modifier.height(8.dp))
            NotificationItem(title = "Profile Updated", body = "Your profile information was updated successfully.", time = "10 min ago")
            Spacer(modifier = Modifier.height(8.dp))
            NotificationItem(title = "Rehome Success", body = "Max has found a new home!", time = "1 hour ago")
            Spacer(modifier = Modifier.height(8.dp))
            NotificationItem(title = "Lost Pet Alert", body = "A pet matching your search was found nearby.", time = "Yesterday")
        }
        // Navigation Bar (bottom)
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PawllyNavBar(
                selectedScreen = "Notifications",
                onNavHome = onNavHome,
                onNavNotifications = onNavNotifications,
                onNavProfile = onNavProfile
            )
        }
    }
}

@Composable
fun NotificationItem(title: String, body: String, time: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(title, fontFamily = Inter, fontSize = 18.sp, color = Purple)
        Text(body, fontFamily = Inter, fontSize = 14.sp, color = Color(0xFF444444), modifier = Modifier.padding(top = 4.dp))
        Text(time, fontFamily = Inter, fontSize = 12.sp, color = Color(0xFF888888), modifier = Modifier.padding(top = 8.dp))
    }
} 