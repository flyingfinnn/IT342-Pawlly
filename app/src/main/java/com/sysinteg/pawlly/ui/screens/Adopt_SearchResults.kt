package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import com.sysinteg.pawlly.ui.theme.Inter
import com.sysinteg.pawlly.model.Pet
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.SideEffect
import com.sysinteg.pawlly.ui.components.PetCard
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

private val samplePets = listOf(
    Pet(1, "Milo", "Golden Retriever", "2 yrs", "Lagos", R.drawable.logoiconpurple),
    Pet(2, "Luna", "Siamese Cat", "1 yr", "Abuja", R.drawable.logoiconpurple),
    Pet(3, "Buddy", "Rabbit", "6 mo", "Ibadan", R.drawable.logoiconpurple)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptSearchResultsScreen(
    pets: List<Pet> = samplePets,
    onPetClick: (Int) -> Unit = {},
    onBack: () -> Unit = {},
    onFilter: (String) -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavNotifications: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var filteredPets by remember { mutableStateOf(samplePets) }

    // Filter state
    var animalType by remember { mutableStateOf("Dog") }
    var locationKm by remember { mutableStateOf(10f) }
    var size by remember { mutableStateOf("Small") }
    var gender by remember { mutableStateOf("Male") }
    var age by remember { mutableStateOf(2f) }

    // Helper functions for filtering
    fun getTypeFromBreed(breed: String): String = when {
        breed.contains("cat", ignoreCase = true) -> "Cat"
        else -> "Dog"
    }
    fun getSizeFromBreed(breed: String): String = when {
        breed.contains("retriever", ignoreCase = true) || breed.contains("labrador", ignoreCase = true) -> "Large"
        breed.contains("siamese", ignoreCase = true) || breed.contains("persian", ignoreCase = true) -> "Medium"
        breed.contains("rabbit", ignoreCase = true) -> "Small"
        else -> "Small"
    }
    fun getAgeInYears(ageStr: String): Float = when {
        ageStr.contains("yr") -> ageStr.split(" ")[0].toFloatOrNull() ?: 0f
        ageStr.contains("mo") -> (ageStr.split(" ")[0].toFloatOrNull() ?: 0f) / 12f
        else -> 0f
    }
    fun getGenderFromName(name: String): String = when (name) {
        "Milo", "Buddy", "Max" -> "Male"
        "Luna", "Bella", "Coco" -> "Female"
        else -> "Male"
    }

    Scaffold(
        containerColor = White,
        // Removed topBar, will add search row in scrollable content
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        placeholder = { Text("Search...") },
                        shape = CircleShape,
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { showFilterSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (filteredPets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pets found. Try adjusting your filters!",
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(filteredPets) { pet ->
                            PetCard(
                                pet = pet,
                                onClick = { onPetClick(pet.id) }
                            )
                        }
                    }
                }
            }
            // Modal Bottom Sheet for Filters
            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFilterSheet = false },
                    containerColor = Purple,
                    contentColor = Color(0xFFEDE7F6)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Filter Pets", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        // Animal Type
                        Text("Type", color = Color(0xFFEDE7F6), fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            listOf("Dog", "Cat").forEach { type ->
                                FilterChip(
                                    selected = animalType == type,
                                    onClick = { animalType = type },
                                    label = { Text(type, color = Color.White) },
                                    shape = RoundedCornerShape(50),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (animalType == type) Color.White.copy(alpha = 0.2f) else Color.Transparent
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        // Location
                        Text("Location (km)", color = Color(0xFFEDE7F6), fontWeight = FontWeight.Bold)
                        Slider(
                            value = locationKm,
                            onValueChange = { locationKm = it },
                            valueRange = 1f..100f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                        Text("${locationKm.toInt()} km", color = Color.White)
                        Spacer(Modifier.height(16.dp))
                        // Size
                        Text("Size", color = Color(0xFFEDE7F6), fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            listOf("Small", "Medium", "Large").forEach { sz ->
                                FilterChip(
                                    selected = size == sz,
                                    onClick = { size = sz },
                                    label = { Text(sz, color = Color.White) },
                                    shape = RoundedCornerShape(50),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (size == sz) Color.White.copy(alpha = 0.2f) else Color.Transparent
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        // Gender
                        Text("Gender", color = Color(0xFFEDE7F6), fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            listOf("Male", "Female").forEach { g ->
                                FilterChip(
                                    selected = gender == g,
                                    onClick = { gender = g },
                                    label = { Text(g, color = Color.White) },
                                    shape = RoundedCornerShape(50),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (gender == g) Color.White.copy(alpha = 0.2f) else Color.Transparent
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        // Age
                        Text("Age", color = Color(0xFFEDE7F6), fontWeight = FontWeight.Bold)
                        Slider(
                            value = age,
                            onValueChange = { age = it },
                            valueRange = 0f..20f,
                            steps = 19,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                        val years = age.toInt()
                        val months = ((age - years) * 12).toInt()
                        val ageLabel = when {
                            years == 0 && months > 0 -> "$months month${if (months > 1) "s" else ""}"
                            years > 0 && months == 0 -> "$years year${if (years > 1) "s" else ""}"
                            years > 0 && months > 0 -> "$years year${if (years > 1) "s" else ""} $months month${if (months > 1) "s" else ""}"
                            else -> "0 months"
                        }
                        Text(ageLabel, color = Color.White)
                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    filteredPets = samplePets.filter { pet ->
                                        getTypeFromBreed(pet.breed) == animalType &&
                                        getSizeFromBreed(pet.breed) == size &&
                                        getGenderFromName(pet.name) == gender &&
                                        getAgeInYears(pet.age) <= age
                                    }
                                    showFilterSheet = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Apply Filter", color = Purple, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(16.dp))
                            OutlinedButton(
                                onClick = {
                                    // Discard changes
                                    filteredPets = samplePets
                                    showFilterSheet = false
                                },
                                border = ButtonDefaults.outlinedButtonBorder,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Discard Changes", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            // Navigation Bar (bottom)
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                PawllyNavBar(
                    selectedScreen = "Adopt",
                    onNavHome = onNavHome,
                    onNavNotifications = onNavNotifications,
                    onNavProfile = onNavProfile
                )
            }
        }
    }
} 