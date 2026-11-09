package com.example.te5st

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class sport(
    val id: Long? = null,
    @SerialName("createdAt") val createdAt: Long? = null,
    @SerialName("sport_name") val sport_name: String? = null,
    val descrizione: String? = null
)

fun getSportIdByName(sportName: String?, sportsList: List<sport> = emptyList()): Int? {
    if (sportName.isNullOrBlank()) return null
    val found = sportsList.find { it.sport_name?.equals(sportName.trim(), ignoreCase = true) == true }
    if (found?.id != null) return found.id.toInt()
    return when (sportName.trim().lowercase()) {
        "karate" -> 1
        "judo" -> 2
        "lotta" -> 3
        else -> null
    }
}

fun getSportNameById(sportId: Int?, sportsList: List<sport> = emptyList()): String {
    if (sportId == null) return "Non specificato"
    val found = sportsList.find { it.id?.toInt() == sportId }
    if (found != null) {
        val name = found.sport_name ?: ""
        val desc = found.descrizione
        return if (!desc.isNullOrBlank()) "$name ($desc)" else name
    }
    return when (sportId) {
        1 -> "Karate"
        2 -> "Judo"
        3 -> "Lotta"
        else -> "Non specificato"
    }
}
