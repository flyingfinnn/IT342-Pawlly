package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.model.Notification
import com.sysinteg.pawlly.model.NotificationType
import com.sysinteg.pawlly.ui.theme.BoneWhite
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.Inter
import androidx.navigation.NavController
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.sysinteg.pawlly.UserApi
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun NotificationScreen(
    navController: NavController,
    onNavHome: () -> Unit = {},
    onNavNotifications: () -> Unit = {},
    onNavProfile: () -> Unit = {},
    selectedScreen: String = "Notifications",
    notificationId: Long? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var notification by remember { mutableStateOf<Notification?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(notificationId) {
        if (notificationId != null) {
            isLoading = true
            error = null
            try {
                val userApi = com.sysinteg.pawlly.userApi
                val notif = userApi.getNotificationById(notificationId)
                // Map NotificationResponse to Notification
                notification = Notification(
                    id = notif.notification_id.toString(),
                    title = notif.notification_title,
                    body = notif.notification_description,
                    type = NotificationType.PET_LISTING_SUCCESS, // Map as needed
                    timestamp = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(notif.notification_date_and_time) ?: java.util.Date(),
                    petId = notif.pet_id?.toInt()
                )
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoneWhite)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (error != null) {
            Text("Error: $error", color = Color.Red, modifier = Modifier.align(Alignment.Center))
        } else if (notification != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontFamily = Inter,
                        fontSize = 28.sp,
                        color = Purple,
                        fontWeight = FontWeight.Bold
                    )
                }
                NotificationItem(
                    notification = notification!!,
                    onClick = {
                        if (notification!!.petId != null) {
                            navController.navigate("adopt/pet/${notification!!.petId}")
                        } else {
                            navController.navigate("notification_detail/${notification!!.id}")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        } else {
            // Fallback to sample notifications if no notificationId provided
            // Sample notifications - in a real app, these would come from a ViewModel
            val notifications = remember {
                listOf(
                    Notification(
                        id = "1",
                        title = "Pet Listing Successful",
                        body = "Your pet listing for Bella has been approved and is now visible to potential adopters.",
                        type = NotificationType.PET_LISTING_SUCCESS,
                        timestamp = Date(),
                        petId = 1
                    ),
                    Notification(
                        id = "2",
                        title = "Adoption Application Denied",
                        body = "Your adoption application for Max has been denied by the audit team. Please review the requirements and try again.",
                        type = NotificationType.ADOPTION_DENIED_AUDIT,
                        timestamp = Date(System.currentTimeMillis() - 3600000),
                        petId = 2
                    ),
                    Notification(
                        id = "3",
                        title = "Adoption Application Denied",
                        body = "Your adoption application for Luna has been denied by the pet owner. They have chosen another applicant.",
                        type = NotificationType.ADOPTION_DENIED_OWNER,
                        timestamp = Date(System.currentTimeMillis() - 7200000),
                        petId = 3
                    ),
                    Notification(
                        id = "4",
                        title = "Adoption Application in Audit",
                        body = "Your adoption application for Charlie is currently being reviewed by our audit team. We'll notify you once a decision is made.",
                        type = NotificationType.ADOPTION_IN_AUDIT,
                        timestamp = Date(System.currentTimeMillis() - 86400000),
                        petId = 4
                    ),
                    Notification(
                        id = "5",
                        title = "Adoption Application Audited",
                        body = "Your adoption application for Max has been reviewed and approved.",
                        type = NotificationType.ADOPTION_AUDITED,
                        timestamp = Date(System.currentTimeMillis() - 108000000),
                        petId = 2
                    ),
                    Notification(
                        id = "6",
                        title = "Pet Ready for Adoption!",
                        body = "Bella is now ready for adoption. Click to view details and start the process.",
                        type = NotificationType.PET_READY_FOR_ADOPTION,
                        timestamp = Date(System.currentTimeMillis() - 144000000),
                        petId = 1
                    ),
                    Notification(
                        id = "7",
                        title = "Adoption Application Sent for Audit",
                        body = "Your adoption application has been submitted and is pending review.",
                        type = NotificationType.ADOPTION_SENT_FOR_AUDIT,
                        timestamp = Date(System.currentTimeMillis() - 172800000),
                        petId = 3
                    )
                )
            }
            notifications.forEach { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = {
                        // If notification has a petId, navigate to Adopt_PetDetail
                        if (notification.petId != null) {
                            navController.navigate("adopt/pet/${notification.petId}")
                        } else {
                            navController.navigate("notification_detail/${notification.id}")
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
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
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val dateFormat = remember { java.text.SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()) }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = notification.title,
                fontFamily = Inter,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Purple
            )
            Text(
                text = notification.body,
                fontFamily = Inter,
                fontSize = 14.sp,
                color = Color(0xFF444444)
            )
            Text(
                text = dateFormat.format(notification.timestamp),
                fontFamily = Inter,
                fontSize = 12.sp,
                color = Color(0xFF888888)
            )
        }
    }
} 