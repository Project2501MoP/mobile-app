package com.example.te5st

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Competition(
    val id: Long? = null,
    val title: String? = null,
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("endDate") val endDate: String? = null,
    val region: String? = null,
    val scope: String? = null,
    val address: String? = null,
    val notes: String? = null,
    @SerialName("requiredUdg") val requiredUdg: Int? = null,
    @SerialName("requiredPdg") val requiredPdg: Int? = null,
    @SerialName("attachedFileName") val attachedFileName: String? = null,
    @SerialName("attachedFilePath") val attachedFilePath: String? = null,
    @SerialName("noticeDays") val noticeDays: Int? = null,
    @SerialName("createdAt") val createdAt: Long? = null,
    val id_sport: Int? = null
)
