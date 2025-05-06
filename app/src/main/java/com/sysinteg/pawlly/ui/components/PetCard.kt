package com.sysinteg.pawlly.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.model.Pet
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.Inter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.foundation.BorderStroke
import coil.compose.AsyncImage

@Composable
fun PetCard(
    pet: Pet,
    modifier: Modifier = Modifier,
    currentUsername: String? = null,
    onOwnerClick: (() -> Unit)? = null,
    onPublicClick: (() -> Unit)? = null
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = (onOwnerClick != null || onPublicClick != null)) {
                // Debug logging and normalization
                val petOwner = pet.user_name?.trim()?.lowercase() ?: ""
                val currentUser = currentUsername?.trim()?.lowercase() ?: ""
                println("[DEBUG] PetCard clicked: pet.user_name='$petOwner', currentUsername='$currentUser'")
                if (currentUser.isNotEmpty() && petOwner == currentUser) {
                    println("[DEBUG] Navigating to PetDetailScreen (owner view)")
                    onOwnerClick?.invoke()
                } else {
                    println("[DEBUG] Navigating to Adopt_PetDetail (public view)")
                    onPublicClick?.invoke()
                }
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(2.dp, Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Full-bleed image
            if (!pet.photo1Thumb.isNullOrEmpty() || !pet.photo1.isNullOrEmpty()) {
                AsyncImage(
                    model = pet.photo1Thumb ?: pet.photo1,
                    contentDescription = pet.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder image (use a local drawable or icon)
                Icon(
                    imageVector = Icons.Outlined.Pets,
                    contentDescription = "No Image",
                    tint = Color.LightGray,
                    modifier = Modifier.fillMaxSize().padding(32.dp)
                )
            }
            // Gradient overlay (bottom 40%)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            // Text overlay (bottom, left-aligned)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = pet.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        text = "\uD83D\uDC3E", // 🐾 emoji
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = pet.breed,
                        fontSize = 14.sp,
                        color = Color(0xFFE0E0E0), // even lighter grey
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
} 