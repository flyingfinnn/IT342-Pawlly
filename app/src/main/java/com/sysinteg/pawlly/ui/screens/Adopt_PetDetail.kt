package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import com.google.accompanist.pager.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import coil.compose.AsyncImage
import com.sysinteg.pawlly.userApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.sysinteg.pawlly.PetResponse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun AdoptPetDetailScreen(
    petId: Int = 1,
    petName: String = "",
    onAdoptNow: () -> Unit = {},
    onBack: () -> Unit = {},
    onNavigateToExisting: () -> Unit = {},
    onNavigateToStart: () -> Unit = {}
) {
    val context = LocalContext.current
    var pet by remember { mutableStateOf<PetResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var photoUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var ownerProfilePicture by remember { mutableStateOf<String?>(null) }
    var ownerProfileBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var checkingApplication by remember { mutableStateOf(false) }
    val prefs = remember { context.getSharedPreferences("pawlly_prefs", 0) }
    val userId = prefs.getLong("user_id", 0L)
    val coroutineScope = rememberCoroutineScope()

    // Fetch pet details
    LaunchedEffect(petId) {
        isLoading = true
        error = ""
        try {
            val result = withContext(Dispatchers.IO) { userApi.getPetById(petId) }
            pet = result
            photoUris = listOfNotNull(result.photo1, result.photo2, result.photo3, result.photo4).filter { it.isNotEmpty() }
        } catch (e: Exception) {
            error = e.message ?: "Failed to load pet details."
        }
        isLoading = false
    }

    // Fetch owner profile picture (base64 or URL)
    LaunchedEffect(pet?.userName) {
        val userName = pet?.userName ?: return@LaunchedEffect
        if (userName.isNotBlank()) {
            try {
                val owner = withContext(Dispatchers.IO) { userApi.getUserByUsername(userName) }
                ownerProfilePicture = owner.profilePicture
                ownerProfileBitmap = try {
                    if (!owner.profilePicture.isNullOrBlank() && owner.profilePicture.startsWith("data:image")) {
                        val base64Data = owner.profilePicture.substringAfter(",", "")
                        val imageBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                        android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    } else null
                } catch (_: Exception) { null }
            } catch (_: Exception) {
                ownerProfilePicture = null
                ownerProfileBitmap = null
            }
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (error.isNotEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = Color.Red)
        }
        return
    }
    if (pet == null) return

    val petImages = photoUris
    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    val pagerState = rememberPagerState()

    Scaffold(
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text("Pet Details", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Purple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (checkingApplication) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            checkingApplication = true
                            coroutineScope.launch {
                                try {
                                    val applications = userApi.getAdoptionApplications(userId, petId)
                                    if (applications.isNotEmpty()) {
                                        onNavigateToExisting()
                                    } else {
                                        onNavigateToStart()
                                    }
                                } catch (e: Exception) {
                                    onNavigateToStart()
                                } finally {
                                    checkingApplication = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text("Adopt Now", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                // Image Carousel
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(horizontal = 0.dp)
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                ) {
                    if (petImages.isNotEmpty()) {
                        HorizontalPager(count = petImages.size, state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                            val imageUrl = petImages[page]
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Pet Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        selectedImage = imageUrl
                                        showImageDialog = true
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        // Placeholder
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Pets, contentDescription = "No Image", tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        }
                    }
                    // Fullscreen image popout dialog
                    if (showImageDialog && selectedImage != null) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.95f))
                                .clickable { showImageDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = selectedImage,
                                contentDescription = "Full Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black),
                                contentScale = ContentScale.Fit
                            )
                            IconButton(
                                onClick = { showImageDialog = false },
                                modifier = Modifier.align(Alignment.TopEnd).padding(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Pet Name
                Text(
                    pet?.name ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Purple,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
                // Attribute Bullets Row
                val attributeBullets = listOf(
                    Triple(Icons.Default.Pets, pet?.breed ?: "", "Breed"),
                    Triple(Icons.Default.Female, pet?.gender ?: "", "Gender"),
                    Triple(Icons.Default.Scale, pet?.weight ?: "", "Weight"),
                    Triple(Icons.Default.Palette, pet?.color ?: "", "Color"),
                    Triple(Icons.Default.Height, pet?.height ?: "", "Height")
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    attributeBullets.forEach { (icon, value, label) ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, Purple),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(icon, contentDescription = label, tint = Purple, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(value, color = Purple, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Pet Details List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val petAttributes = listOf(
                        Triple(Icons.Default.Pets, pet?.age ?: "", "Age"),
                        Triple(Icons.Default.Pets, pet?.type ?: "", "Type"),
                        Triple(Icons.Default.Pets, pet?.status ?: "", "Status"),
                        Triple(Icons.Default.LocationOn, pet?.address ?: "", "Address"),
                        Triple(Icons.Default.Person, pet?.contactNumber ?: "", "Contact Number"),
                        Triple(Icons.Default.Person, pet?.submissionDate ?: "", "Submission Date")
                    )
                    petAttributes.forEach { (icon, value, label) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = label, tint = Purple, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("$label: $value", color = Color(0xFF444444), fontSize = 15.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Description
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Description", fontWeight = FontWeight.Bold, color = Purple, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(pet?.description ?: "", color = Color(0xFF444444), fontSize = 15.sp)
                }
                Spacer(Modifier.height(32.dp))
                // Pet Owner Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Pet Owner", fontWeight = FontWeight.Bold, color = Purple, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (ownerProfileBitmap != null) {
                            // Fallback: use AsyncImage with a temp file or skip, asImageBitmap is not needed here
                            // If you want to show the bitmap, you can use ImageBitmapConfig, but for now, fallback to icon
                            Icon(Icons.Default.Person, contentDescription = "Owner", tint = Purple, modifier = Modifier.size(48.dp))
                        } else if (!ownerProfilePicture.isNullOrBlank()) {
                            AsyncImage(
                                model = ownerProfilePicture,
                                contentDescription = "Owner Profile Picture",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = "Owner", tint = Purple, modifier = Modifier.size(48.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(pet?.userName ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF222222))
                    }
                }
                Spacer(Modifier.height(48.dp))
            }
        }
    }
} 
