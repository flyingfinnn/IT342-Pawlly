package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import kotlin.random.Random
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.sysinteg.pawlly.model.Pet
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.ui.draw.blur
import com.sysinteg.pawlly.ui.components.PetCard
import androidx.compose.ui.platform.LocalContext
import com.sysinteg.pawlly.userApi
import com.sysinteg.pawlly.UserResponse
import com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS
import com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.painter.Painter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdoptScreen(
    onBrowseAll: () -> Unit = {},
    onPetClick: (Int) -> Unit = {},
    onFilterClick: (String) -> Unit = {},
    onLostFoundClick: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavNotifications: () -> Unit = {},
    onNavProfile: () -> Unit = {},
    onAdoptPetDetail: (Int, String) -> Unit = { _, _ -> }
) {
    val filters = listOf("Dog", "Cat", "Small", "Nearby", "All")
    var selectedFilter by remember { mutableStateOf("All") }
    val scrollState = rememberScrollState()
    val cardCorner = 24.dp
    val cardSpacing = 16.dp
    val cardWidth = (LocalConfiguration.current.screenWidthDp.dp - 16.dp * 2 - cardSpacing) / 2
    val cardHeight = cardWidth * 1.15f
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PAWLLY_PREFS, android.content.Context.MODE_PRIVATE) }
    val coroutineScope = rememberCoroutineScope()
    var showCompleteProfileDialog by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<UserResponse?>(null) }
    var currentUsername by remember { mutableStateOf("") }

    // --- Fetch pets from backend ---
    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var petsLoading by remember { mutableStateOf(true) }
    var petsError by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        petsLoading = true
        petsError = ""
        try {
            val response = userApi.getAllPets()
            pets = response.map {
                Pet(
                    id = it.pid,
                    name = it.name ?: "",
                    breed = it.breed ?: "",
                    age = it.age ?: "",
                    location = it.address ?: "",
                    photo1 = it.photo1,
                    photo2 = it.photo2,
                    photo3 = it.photo3,
                    photo4 = it.photo4,
                    weight = it.weight,
                    color = it.color,
                    height = it.height,
                    user_name = it.userName
                )
            }
        } catch (e: Exception) {
            petsError = e.message ?: "Failed to load pets"
        }
        petsLoading = false
    }

    // Fetch user data and check for missing attributes
    LaunchedEffect(Unit) {
        val token = prefs.getString(KEY_JWT_TOKEN, null)
        if (token != null) {
            try {
                val me = userApi.getMe("Bearer $token")
                user = me
                if (me != null && (
                        me.firstName.isNullOrBlank() ||
                        me.lastName.isNullOrBlank() ||
                        me.phoneNumber.isNullOrBlank() ||
                        me.address.isNullOrBlank()
                    )
                ) {
                    showCompleteProfileDialog = true
                }
                currentUsername = me.username ?: ""
            } catch (_: Exception) {
                // ignore
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                    .statusBarsPadding()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logopurple),
                    contentDescription = "Pawlly Logo",
                    modifier = Modifier
                        .height(40.dp)
                        .align(Alignment.CenterStart)
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                PawllyNavBar(
                    selectedScreen = "Home",
                    onNavHome = onNavHome,
                    onNavNotifications = onNavNotifications,
                    onNavProfile = onNavProfile
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .background(Color.White)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            // Banner
            Box(
                modifier = Modifier
                    .width(cardWidth * 2 + cardSpacing)
                    .height(180.dp)
                    .clip(RoundedCornerShape(cardCorner))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.adopt_banner),
                    contentDescription = "Adopt Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(8.dp))
            // Quick Filters
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedFilter = filter
                            onFilterClick(filter)
                        },
                        label = {
                            Text(
                                filter,
                                color = if (isSelected) Color.White else Purple,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (isSelected) Purple else Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            // Featured Pets Section
            Text(
                text = "Featured Pets",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color(0xFF222222),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Pet Cards Grid
            val featuredPets = remember(pets) { pets.shuffled().take(6) }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight * 3 + cardSpacing * 2)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                when {
                    petsLoading -> {
                        item {
                            Text("Loading pets...", color = Color.Gray)
                        }
                    }
                    petsError.isNotEmpty() -> {
                        item {
                            Text("Error: $petsError", color = Color.Red)
                        }
                    }
                    else -> {
                        items(featuredPets) { pet ->
                            PetCard(
                                pet = pet,
                                currentUsername = currentUsername,
                                onOwnerClick = { onPetClick(pet.id) },
                                onPublicClick = { onAdoptPetDetail(pet.id, pet.name) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Action Buttons
            Button(
                onClick = onBrowseAll,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(cardCorner),
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Text("Browse All", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onLostFoundClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(cardCorner),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(cardCorner))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.map),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.35f),
                                        Color.Transparent
                                    ),
                                    startY = Float.POSITIVE_INFINITY,
                                    endY = 0f
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Go to Lost and Found",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Show dialog if profile is incomplete
    if (showCompleteProfileDialog) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismiss by outside click */ },
            title = { Text("You're almost finished!", fontWeight = FontWeight.Bold) },
            text = { Text("Click here to continue your account set up") },
            confirmButton = {
                Button(
                    onClick = {
                        showCompleteProfileDialog = false
                        onNavProfile() // This will now navigate with editMode=true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                    Text("Go to Profile", color = Color.White)
                }
            }
        )
    }
}