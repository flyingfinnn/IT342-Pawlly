package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.ui.theme.BoneWhite
import com.sysinteg.pawlly.ui.theme.Inter
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.userApi
import kotlinx.coroutines.launch
import com.sysinteg.pawlly.DetailedAdoptionApplicationResponse
import com.sysinteg.pawlly.UserResponse
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import android.content.Context
import com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS
import com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationView(
    applicationId: Int,
    onBack: () -> Unit,
    isOwnerView: Boolean = false,
    onStatusChanged: (() -> Unit)? = null
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var application by remember { mutableStateOf<DetailedAdoptionApplicationResponse?>(null) }
    var userDetails by remember { mutableStateOf<UserResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showDenyDialog by remember { mutableStateOf(false) }
    var actionLoading by remember { mutableStateOf(false) }
    var actionError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var petName by remember { mutableStateOf("") }
    var applicantUserId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(applicationId) {
        try {
            application = userApi.getAdoptionApplicationById(applicationId)
            applicantUserId = application?.userId
            isLoading = false
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    LaunchedEffect(applicantUserId) {
        if (applicantUserId != null) {
            try {
                userDetails = userApi.getUserById(applicantUserId!!)
            } catch (e: Exception) {
                error = "Failed to load applicant details: ${e.message}"
            }
        }
    }

    LaunchedEffect(application?.petId) {
        val petId = application?.petId
        if (petId != null) {
            try {
                val pet = userApi.getPetById(petId)
                petName = pet.name ?: ""
            } catch (_: Exception) {
                petName = ""
            }
        }
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }

    Scaffold(
        containerColor = BoneWhite,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Application Details",
                        fontFamily = Inter,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BoneWhite,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            val app = application
            if (isOwnerView && app != null && error == null && app.status.equals("pending", ignoreCase = true)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showAcceptDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f),
                        enabled = !actionLoading
                    ) {
                        Text("Accept", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { showDenyDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        modifier = Modifier.weight(1f),
                        enabled = !actionLoading
                    ) {
                        Text("Reject", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Purple
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: $error",
                            color = Color.Red,
                            fontFamily = Inter,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { /* Retry logic */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple)
                        ) {
                            Text("Retry", fontFamily = Inter)
                        }
                    }
                }
                application != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Status Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Status",
                                    fontFamily = Inter,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = application?.status?.uppercase() ?: "",
                                    fontFamily = Inter,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (application?.status?.lowercase()) {
                                        "pending" -> Color(0xFFFFC107)
                                        "accepted" -> Color(0xFF4CAF50)
                                        "rejected" -> Color(0xFFE53935)
                                        else -> Color.Gray
                                    }
                                )
                            }
                        }

                        // Pet Details Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Applying for ${application?.petName ?: ""}",
                                    fontFamily = Inter,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Applicant Details Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Applicant Details",
                                    fontFamily = Inter,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailRow("Name", "${userDetails?.firstName ?: ""} ${userDetails?.lastName ?: ""}")
                                DetailRow("Email", userDetails?.email ?: "")
                                DetailRow("Phone", userDetails?.phoneNumber ?: "")
                                DetailRow("Address", userDetails?.address ?: "")
                            }
                        }

                        // Application Details Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        text = "Application Details #${application?.id ?: ""}",
                                        fontFamily = Inter,
                                        fontSize = 16.sp,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailRow("Pet Name", petName)
                                // Robustly extract the date from createdAt
                                val submissionDate = application?.createdAt?.let { dateStr ->
                                    dateStr.split("T").firstOrNull() ?: dateStr
                                } ?: "N/A"
                                DetailRow("Created An", submissionDate)
                                DetailRow("Reason for Adoption", application?.reasonForAdoption ?: "N/A")
                                DetailRow("Experience with Pets", application?.experienceWithPets ?: "N/A")
                                
                                // Living Situation Details
                                val livingSituation = buildString {
                                    append("Type: ${application?.householdType ?: "N/A"}\n")
                                    append("Ownership: ${application?.householdOwnership ?: "N/A"}\n")
                                    append("Adults: ${application?.numAdults ?: "N/A"}\n")
                                    append("Children: ${application?.numChildren ?: "N/A"}")
                                }
                                DetailRow("Living Situation", livingSituation)
                                
                                DetailRow("Daily Routine", application?.dailyRoutine ?: "N/A")
                                DetailRow("Other Pets", if (application?.otherPets == true) "Yes" else "No")
                            }
                        }
                    }
                }
            }
        }
        // Snackbar for success
        if (successMessage != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Snackbar(
                    modifier = Modifier.padding(bottom = 80.dp),
                    action = {
                        TextButton(onClick = { successMessage = null }) {
                            Text("Dismiss", color = Purple)
                        }
                    }
                ) {
                    Text(successMessage ?: "", fontFamily = Inter)
                }
            }
        }
    }

    // Accept Confirmation Dialog
    if (showAcceptDialog) {
        AlertDialog(
            onDismissRequest = { showAcceptDialog = false },
            title = { Text("Accept Application", fontFamily = Inter, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to accept this application?", fontFamily = Inter) },
            confirmButton = {
                Button(
                    onClick = {
                        showAcceptDialog = false
                        actionLoading = true
                        actionError = null
                        scope.launch {
                            try {
                                val response = userApi.updateAdoptionStatus(applicationId, mapOf("status" to "accepted"))
                                if (response.isSuccessful) {
                                    actionLoading = false
                                    successMessage = "Application accepted successfully!"
                                    onStatusChanged?.invoke()
                                } else {
                                    actionLoading = false
                                    actionError = "Failed to accept application."
                                }
                            } catch (e: Exception) {
                                actionLoading = false
                                actionError = e.message ?: "Failed to accept application."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    enabled = !actionLoading
                ) { Text("Confirm", color = Color.White, fontFamily = Inter) }
            },
            dismissButton = {
                TextButton(onClick = { showAcceptDialog = false }) { Text("Cancel", fontFamily = Inter) }
            }
        )
    }
    // Reject Confirmation Dialog
    if (showDenyDialog) {
        AlertDialog(
            onDismissRequest = { showDenyDialog = false },
            title = { Text("Reject Application", fontFamily = Inter, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to reject this application?", fontFamily = Inter) },
            confirmButton = {
                Button(
                    onClick = {
                        showDenyDialog = false
                        actionLoading = true
                        actionError = null
                        scope.launch {
                            try {
                                val response = userApi.updateAdoptionStatus(applicationId, mapOf("status" to "rejected"))
                                if (response.isSuccessful) {
                                    actionLoading = false
                                    successMessage = "Application rejected successfully!"
                                    onStatusChanged?.invoke()
                                } else {
                                    actionLoading = false
                                    actionError = "Failed to reject application."
                                }
                            } catch (e: Exception) {
                                actionLoading = false
                                actionError = e.message ?: "Failed to reject application."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    enabled = !actionLoading
                ) { Text("Confirm", color = Color.White, fontFamily = Inter) }
            },
            dismissButton = {
                TextButton(onClick = { showDenyDialog = false }) { Text("Cancel", fontFamily = Inter) }
            }
        )
    }
    // Optionally show loading/error state for action
    if (actionLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Purple)
        }
    }
    if (actionError != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $actionError", color = Color.Red, fontFamily = Inter)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontFamily = Inter,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontFamily = Inter,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
