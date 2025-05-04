package com.sysinteg.pawlly.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import android.graphics.BitmapFactory
import androidx.compose.foundation.clickable

@Composable
fun AdoptAdoptionStep4Screen(
    onContinue: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var imageUris by remember { mutableStateOf(listOf<Uri>()) }
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.size <= 4) imageUris = uris
    }

    fun removeImage(index: Int) {
        imageUris = imageUris.toMutableList().apply { removeAt(index) }
        selectedImageIndex = null
    }

    Scaffold(
        containerColor = White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Step 4 of 7",
                        color = Purple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onContinue,
                    enabled = imageUris.size in 2..4,
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue", color = White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Upload Images of Your Home", fontSize = 22.sp, color = Purple)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Please upload 2–4 images.",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    (0..1).forEach { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                                .clickable {
                                    if (index < imageUris.size) {
                                        selectedImageIndex = index
                                    } else {
                                        imagePicker.launch("image/*")
                                    }
                                }
                        ) {
                            if (index < imageUris.size) {
                                val bitmap = remember(imageUris[index]) {
                                    context.contentResolver.openInputStream(imageUris[index])?.use { inputStream ->
                                        BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                                    }
                                }
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = "Home Image",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (selectedImageIndex == index) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.5f))
                                                .clickable { removeImage(index) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("×", color = Color.White, fontSize = 32.sp)
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+", fontSize = 24.sp)
                                }
                            }
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    (2..3).forEach { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                                .clickable {
                                    if (index < imageUris.size) {
                                        selectedImageIndex = index
                                    } else {
                                        imagePicker.launch("image/*")
                                    }
                                }
                        ) {
                            if (index < imageUris.size) {
                                val bitmap = remember(imageUris[index]) {
                                    context.contentResolver.openInputStream(imageUris[index])?.use { inputStream ->
                                        BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                                    }
                                }
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = "Home Image",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (selectedImageIndex == index) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.5f))
                                                .clickable { removeImage(index) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("×", color = Color.White, fontSize = 32.sp)
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+", fontSize = 24.sp)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
} 