package com.sysinteg.pawlly.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class StatusType {
    Pending, Submitted, Success, Failed, Expired, None
}

@Composable
fun StatusChip(status: StatusType, modifier: Modifier = Modifier) {
    val icon: ImageVector?
    val text: String
    val color: Color
    val textColor: Color
    when (status) {
        StatusType.Pending -> {
            icon = Icons.Default.HourglassEmpty
            text = "Pending"
            color = Color(0xFFFFA726)
            textColor = Color(0xFFFFA726)
        }
        StatusType.Submitted -> {
            icon = Icons.Default.Schedule
            text = "Submitted"
            color = Color(0xFF1976D2)
            textColor = Color(0xFF1976D2)
        }
        StatusType.Success -> {
            icon = Icons.Default.CheckCircle
            text = "Success"
            color = Color(0xFF43A047)
            textColor = Color(0xFF43A047)
        }
        StatusType.Failed -> {
            icon = Icons.Default.Cancel
            text = "Failed"
            color = Color(0xFFE53935)
            textColor = Color(0xFFE53935)
        }
        StatusType.Expired -> {
            icon = Icons.Default.Error
            text = "Expired"
            color = Color(0xFFBDBDBD)
            textColor = Color(0xFF757575)
        }
        StatusType.None -> {
            icon = null
            text = ""
            color = Color.Transparent
            textColor = Color.Transparent
        }
    }
    if (status != StatusType.None) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(Color.White, RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = text,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun AnimatedStatusChip(visible: Boolean, status: StatusType) {
    AnimatedVisibility(
        visible = visible && status != StatusType.None,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = 400)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = 400)
        )
    ) {
        StatusChip(status = status)
    }
} 