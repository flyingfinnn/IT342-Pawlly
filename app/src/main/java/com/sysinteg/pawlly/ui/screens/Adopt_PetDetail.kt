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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun AdoptPetDetailScreen(
    petId: Int = 1,
    onAdoptNow: () -> Unit = {},
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
    // Placeholder pet data
    val petImages = listOf(R.drawable.logoiconpurple, R.drawable.logoiconwhite)
    val petName = "Magie"
    val petBreed = "Shiba Inu"
    val petAge = "14 months"
    val petGender = "Female"
    val petWeight = "12 lb"
    val petColor = "Red"
    val petHeight = "91 cm"
    val petLocation = "California (12 km away)"
    val petAttributes = listOf(
        Triple(Icons.Default.Pets, petBreed, "Breed"),
        Triple(Icons.Default.Female, petGender, "Gender"),
        Triple(Icons.Default.Scale, petWeight, "Weight"),
        Triple(Icons.Default.Palette, petColor, "Color"),
        Triple(Icons.Default.Height, petHeight, "Height"),
        Triple(Icons.Default.Pets, petAge, "Age")
    )
    val petTraits = listOf(
        "Good with children",
        "House-trained",
        "Vaccinated",
        "Microchipped"
    )
    val petStory = "Magie is a playful and loving Shiba Inu who enjoys long walks and cuddles. She is looking for a forever home where she can share her affection and energy. Magie is house-trained, vaccinated, and gets along well with other pets."

    var expanded by remember { mutableStateOf(false) }
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
                Button(
                    onClick = onAdoptNow,
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
                Text(
                    petName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Purple,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
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
                    Text(
                        petLocation,
                        color = Color(0xFF444444),
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(24.dp))
                // Attribute Chips Row
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
                Spacer(Modifier.height(24.dp))
                // Traits List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    petTraits.forEach { trait ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Purple, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(trait, color = Color(0xFF444444), fontSize = 16.sp)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Pet Story (Expandable)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("About $petName", fontWeight = FontWeight.Bold, color = Purple, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    if (expanded) {
                        Text(petStory, color = Color(0xFF444444), fontSize = 15.sp)
                        Text(
                            "Read less",
                            color = Purple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.clickable { expanded = false }
                        )
                    } else {
                        Text(
                            petStory,
                            color = Color(0xFF444444),
                            fontSize = 15.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Read more",
                            color = Purple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.clickable { expanded = true }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Spacer(Modifier.height(48.dp)) // For bottom bar spacing
            }
        }
    }
} 