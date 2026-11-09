package com.example.te5st

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Availability(
    val id: Long? = null,
    @SerialName("competitionId") val competitionId: Long? = null,
    @SerialName("userId") val userId: Long? = null,
    val status: String? = null,
    val note: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
)

@Serializable
data class AvailabilityInsert(
    @SerialName("competitionId") val competitionId: Long,
    @SerialName("userId") val userId: Long,
    val status: String,
    val note: String? = null
)
