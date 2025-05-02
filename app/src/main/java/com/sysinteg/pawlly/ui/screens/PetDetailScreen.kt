package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Height
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
import com.google.accompanist.pager.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun PetDetailScreen(
    petId: Int = 1,
    onEdit: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }
    // Editable state
    var isEditing by remember { mutableStateOf(false) }
    var petName by remember { mutableStateOf("Magie") }
    var petBreed by remember { mutableStateOf("Shiba Inu") }
    var petAge by remember { mutableStateOf("14 months") }
    var petGender by remember { mutableStateOf("Female") }
    var petWeight by remember { mutableStateOf("12 lb") }
    var petColor by remember { mutableStateOf("Red") }
    var petHeight by remember { mutableStateOf("91 cm") }
    var petLocation by remember { mutableStateOf("California (12 km away)") }
    var petStory by remember { mutableStateOf("Magie is a playful and loving Shiba Inu who enjoys long walks and cuddles. She is looking for a forever home where she can share her affection and energy. Magie is house-trained, vaccinated, and gets along well with other pets.") }
    val petImages = listOf(R.drawable.logoiconpurple, R.drawable.logoiconwhite)
    val petTraits = listOf(
        "Good with children",
        "House-trained",
        "Vaccinated",
        "Microchipped"
    )
    val pagerState = rememberPagerState()

    Scaffold(
        containerColor = White,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isEditing) {
                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text("Edit Details", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                } else {
                    Button(
                        onClick = { isEditing = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text("Save", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                ) {
                    HorizontalPager(
                        count = petImages.size,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Image(
                            painter = painterResource(id = petImages[page]),
                            contentDescription = "Pet Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Pet Name & Location
                if (!isEditing) {
                    Text(
                        petName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Purple,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = petName,
                        onValueChange = { petName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Purple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    if (!isEditing) {
                        Text(
                            petLocation,
                            color = Color(0xFF444444),
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        OutlinedTextField(
                            value = petLocation,
                            onValueChange = { petLocation = it },
                            label = { Text("Location") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Attribute Chips or Editable Fields
                if (!isEditing) {
                    val petAttributes = listOf(
                        Triple(Icons.Default.Pets, petBreed, "Breed"),
                        Triple(Icons.Default.Female, petGender, "Gender"),
                        Triple(Icons.Default.Scale, petWeight, "Weight"),
                        Triple(Icons.Default.Palette, petColor, "Color"),
                        Triple(Icons.Default.Height, petHeight, "Height"),
                        Triple(Icons.Default.Pets, petAge, "Age")
                    )
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        petAttributes.forEach { (icon, label, _) ->
                            AssistChip(
                                onClick = {},
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(icon, contentDescription = null, tint = Purple, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(label, color = Color(0xFF444444), fontWeight = FontWeight.Medium)
                                    }
                                },
                                shape = RoundedCornerShape(50),
                                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFF5F5F5)),
                                elevation = AssistChipDefaults.assistChipElevation(4.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = petBreed,
                            onValueChange = { petBreed = it },
                            label = { Text("Breed") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = petGender,
                            onValueChange = { petGender = it },
                            label = { Text("Gender") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = petWeight,
                            onValueChange = { petWeight = it },
                            label = { Text("Weight") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = petColor,
                            onValueChange = { petColor = it },
                            label = { Text("Color") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = petHeight,
                            onValueChange = { petHeight = it },
                            label = { Text("Height") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = petAge,
                            onValueChange = { petAge = it },
                            label = { Text("Age") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Traits List (read-only)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Traits", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Purple)
                    petTraits.forEach { trait ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Purple, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(trait, color = Color(0xFF444444), fontSize = 15.sp)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Story
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Story", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Purple)
                    if (!isEditing) {
                        Text(petStory, color = Color(0xFF444444), fontSize = 15.sp, modifier = Modifier.padding(top = 8.dp))
                    } else {
                        OutlinedTextField(
                            value = petStory,
                            onValueChange = { petStory = it },
                            label = { Text("Story") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(48.dp))
            }
        }
    }
} 