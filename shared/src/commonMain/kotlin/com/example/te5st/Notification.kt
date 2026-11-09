package com.example.te5st

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Notification(
    val id: Long? = null,
    val title: String? = null,
    val message: String? = null,
    @SerialName("isRead") val isRead: Boolean? = false,
    @SerialName("competitionId") val competitionId: Long? = null,
    @SerialName("userId") val userId: Long? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class NotificationInsert(
    val title: String,
    val message: String,
    @SerialName("isRead") val isRead: Boolean = false,
    @SerialName("competitionId") val competitionId: Long? = null,
    @SerialName("userId") val userId: Long
)
