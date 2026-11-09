package com.example.te5st

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class OfficialConvocatedStats(
    @SerialName("user_id") val userId: Long,
    val username: String,
    val firstname: String? = null,
    val lastname: String? = null,
    @SerialName("total_availabilities") val totalAvailabilities: Int = 0,
    @SerialName("total_convocations") val totalConvocations: Int = 0,
    @SerialName("convocation_percentage") val convocationPercentage: Float = 0f,
    @SerialName("sport_name") val sportName: String? = null
)
