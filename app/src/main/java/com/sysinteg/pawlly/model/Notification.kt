package com.sysinteg.pawlly.model

import java.util.Date

enum class NotificationType {
    ADOPTION_REQUEST,
    PROFILE_UPDATE,
    REHOME_SUCCESS,
    LOST_PET_ALERT,
    SYSTEM,
    PET_LISTING_SUCCESS,
    ADOPTION_AUDITED,
    PET_READY_FOR_ADOPTION,
    ADOPTION_SENT_FOR_AUDIT,
    ADOPTION_DENIED_AUDIT,
    ADOPTION_DENIED_OWNER,
    ADOPTION_IN_AUDIT
}

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val timestamp: Date,
    val isRead: Boolean = false,
    val petId: Int? = null, // Optional pet ID if notification is related to a pet
    val actionUrl: String? = null, // Optional deep link or action URL
    val senderId: String? = null, // For notifications involving other users
    val recipientId: String? = null // For targeted notifications
) 