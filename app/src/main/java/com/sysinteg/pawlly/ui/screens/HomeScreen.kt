package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Inter
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import com.sysinteg.pawlly.ui.screens.NavBarItem
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun HomeScreen(
    onAdoptClick: () -> Unit = {},
    onRehomeClick: () -> Unit = {},
    onLostFoundClick: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavNotifications: () -> Unit = {},
    onNavProfile: () -> Unit = {},
    selectedScreen: String = "Home"
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val name = user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "?"
    val subtitleGrey = Color(0xFF888888)
    val boneWhite = Color(0xFFFAF9F6)

    // Selection state for mode buttons
    var selectedMode by remember { mutableStateOf("") }

    // Handle navigation after mode selection
    LaunchedEffect(selectedMode) {
        if (selectedMode == "Adopt") {
            kotlinx.coroutines.delay(300)
            onAdoptClick()
        }
    }

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
            .background(boneWhite)
    ) {
        // Blurred/semi-transparent status bar overlay
        Box(
            Modifier
                .fillMaxWidth()
                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .background(Color.White.copy(alpha = 0.7f))
        )
        // Welcome Banner (top)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp)
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Logo at the top
            Image(
                painter = painterResource(id = R.drawable.logopurple),
                contentDescription = "Pawlly Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 0.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.offset(y = 18.dp)) {
                Text(
                    text = "Hello $name",
                    color = Purple,
                    fontFamily = Inter,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choose a mode to get started",
                    color = subtitleGrey,
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        // Mode Buttons (center, moved up by 50dp)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            ModeButton(
                icon = Icons.Default.Pets,
                label = "Adopt",
                description = "Find a new friend to bring home",
                selected = selectedMode == "Adopt",
                onClick = { selectedMode = "Adopt" }
            )
            ModeButton(
                icon = Icons.Default.House,
                label = "Rehome",
                description = "Help a pet find a new family",
                selected = selectedMode == "Rehome",
                onClick = { selectedMode = "Rehome" }
            )
            ModeButton(
                icon = Icons.Default.Search,
                label = "Lost & Found",
                description = "Report or search for lost pets",
                selected = selectedMode == "Lost & Found",
                onClick = { selectedMode = "Lost & Found" }
            )
        }
        // Navigation Bar (bottom)
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PawllyNavBar(
                selectedScreen = "Home",
                onNavHome = onNavHome,
                onNavNotifications = onNavNotifications,
                onNavProfile = onNavProfile
            )
        }
    }
}

@Composable
fun ModeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    description: String,
    selected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val purple = com.sysinteg.pawlly.ui.theme.Purple
    val bgColor = if (selected) purple else Color.White
    val textColor = if (selected) Color.White else Color(0xFF222222)
    val descColor = if (selected) Color.White else Color(0xFF888888)
    val iconColor = if (selected) Color.White else Color(0xFF222222)
    val borderColor = if (selected) purple else purple.copy(alpha = 0.25f)
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(28.dp), clip = false)
            .clip(RoundedCornerShape(28.dp))
            .background(bgColor)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(28.dp))
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon and texts
            Column(
                modifier = Modifier.weight(1f).padding(start = 28.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(34.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(label, color = textColor, fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(description, color = descColor, fontFamily = Inter, fontSize = 16.sp)
            }
            // Right: Chevron or loading
            Box(modifier = Modifier.width(40.dp).padding(end = 28.dp), contentAlignment = Alignment.Center) {
                if (selected) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = descColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Go",
                        tint = descColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}