package com.example.te5st

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Udg(
    val id: Long? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val username: String,
    val password: String,
    val firstname: String? = null,
    val lastname: String? = null,
    val region: String? = null,
    val role: String? = null,
    val scope: String? = null,
    @SerialName("isadmin") val isAdmin: Boolean? = false,
    @SerialName("isfirstlogin") val isFirstLogin: Boolean? = true
)
