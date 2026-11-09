package com.example.te5st

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class CompetitionRepository {
    private val postgrest = SupabaseManager.client.postgrest

    suspend fun fetchCompetitions(): List<Competition> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("v_competitions").select().decodeList<Competition>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchSports(): List<sport> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("sport").select().decodeList<sport>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun insertCompetition(competition: Competition): Competition? = withContext(Dispatchers.IO) {
        try {
            val json = buildJsonObject {
                competition.title?.let { put("title", it) }
                competition.startDate?.let { put("startDate", it) }
                competition.endDate?.let { put("endDate", it) }
                competition.region?.let { put("region", it) }
                competition.scope?.let { put("scope", it) }
                competition.address?.let { put("address", it) }
                competition.notes?.let { put("notes", it) }
                competition.requiredUdg?.let { put("requiredUdg", it) }
                competition.requiredPdg?.let { put("requiredPdg", it) }
                competition.attachedFileName?.let { put("attachedFileName", it) }
                competition.attachedFilePath?.let { put("attachedFilePath", it) }
                competition.noticeDays?.let { put("noticeDays", it) }
                competition.id_sport?.let { put("id_sport", it) }
            }

            val result = postgrest.from("competitions").insert(json) {
                select()
            }.decodeSingle<Competition>()
            result
        } catch (e: Exception) {
            println("Error in insertCompetition with json select(): ${e.message}")
            e.printStackTrace()
            try {
                postgrest.from("competitions").insert(competition)
                competition
            } catch (e2: Exception) {
                println("Error in insertCompetition fallback: ${e2.message}")
                e2.printStackTrace()
                null
            }
        }
    }

    suspend fun updateCompetition(competition: Competition): Boolean = withContext(Dispatchers.IO) {
        try {
            if (competition.id != null) {
                postgrest.from("competitions").update(competition) {
                    filter {
                        eq("id", competition.id)
                    }
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchAvailabilities(): List<Availability> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("availabilities").select().decodeList<Availability>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun insertAvailabilityWithLog(availability: Availability): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val compId = availability.competitionId
        val uId = availability.userId
        val st = availability.status ?: "Disponibile"
        val availNote = availability.note

        if (compId == null) {
            return@withContext Pair(false, "ERRORE: competitionId è null! Impossibile collegare la disponibilità alla gara.")
        }
        if (uId == null) {
            return@withContext Pair(false, "ERRORE: userId è null! Utente non identificato.")
        }

        val insertDto = AvailabilityInsert(
            competitionId = compId,
            userId = uId,
            status = st,
            note = availNote
        )

        // Attempt 1: Update if availability.id is not null
        if (availability.id != null) {
            try {
                val json = buildJsonObject {
                    put("competitionId", compId)
                    put("userId", uId)
                    put("status", st)
                    if (availNote != null) put("note", availNote)
                }
                postgrest.from("availabilities").update(json) {
                    filter { eq("id", availability.id) }
                }
                return@withContext Pair(true, "OK - Disponibilità (ID ${availability.id}) aggiornata a '$st'")
            } catch (e: Exception) {
                val errUpdate = e.message ?: e.toString()
                println("Update availability failed: $errUpdate")
                e.printStackTrace()
            }
        }

        // Attempt 2: Insert using AvailabilityInsert DTO (matching image schema: "competitionId", "userId")
        try {
            postgrest.from("availabilities").insert(insertDto)
            return@withContext Pair(true, "OK - Nuova disponibilità '$st' registrata su Supabase (competitionId=$compId, userId=$uId)")
        } catch (e: Exception) {
            val err1 = e.message ?: e.toString()
            println("Attempt 1 AvailabilityInsert failed: $err1")
            e.printStackTrace()
        }

        // Attempt 3: Insert using kotlinx.serialization buildJsonObject with camelCase "competitionId" and "userId"
        try {
            val json = buildJsonObject {
                put("competitionId", compId)
                put("userId", uId)
                put("status", st)
                if (availNote != null) put("note", availNote)
            }
            postgrest.from("availabilities").insert(json)
            return@withContext Pair(true, "OK (JsonObject) - Disponibilità '$st' registrata")
        } catch (e: Exception) {
            val err2 = e.message ?: e.toString()
            println("Attempt 2 buildJsonObject failed: $err2")
            e.printStackTrace()
            return@withContext Pair(false, "ERRORE salvataggio disponibilità: $err2")
        }
    }

    suspend fun insertAvailability(availability: Availability): Boolean {
        return insertAvailabilityWithLog(availability).first
    }

    suspend fun fetchNotifications(): List<Notification> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("notifications").select().decodeList<Notification>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun insertNotificationWithLog(notification: Notification): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val targetUserId = notification.userId
        val targetCompId = notification.competitionId

        if (targetUserId == null) {
            return@withContext Pair(false, "ERRORE: userId è null")
        }

        val insertDto = NotificationInsert(
            title = notification.title ?: "Notifica",
            message = notification.message ?: "",
            isRead = notification.isRead ?: false,
            competitionId = targetCompId,
            userId = targetUserId
        )

        // Attempt 1: Insert using NotificationInsert DTO (matching image schema: "userId", "competitionId", "isRead")
        try {
            postgrest.from("notifications").insert(insertDto)
            return@withContext Pair(true, "OK - Inserito tramite DTO (userId=$targetUserId, competitionId=$targetCompId)")
        } catch (e: Exception) {
            val err1 = e.message ?: e.toString()
            println("Attempt 1 NotificationInsert failed: $err1")
            e.printStackTrace()
        }

        // Attempt 2: Insert using kotlinx.serialization buildJsonObject
        try {
            val json = buildJsonObject {
                put("title", notification.title ?: "Notifica")
                put("message", notification.message ?: "")
                put("isRead", notification.isRead ?: false)
                if (targetCompId != null) {
                    put("competitionId", targetCompId)
                }
                put("userId", targetUserId)
            }
            postgrest.from("notifications").insert(json)
            return@withContext Pair(true, "OK - Inserito tramite JsonObject (userId=$targetUserId, competitionId=$targetCompId)")
        } catch (e: Exception) {
            val err2 = e.message ?: e.toString()
            println("Attempt 2 buildJsonObject failed: $err2")
            e.printStackTrace()
            return@withContext Pair(false, "ERRORE: $err2")
        }
    }

    suspend fun insertNotification(notification: Notification): Boolean {
        return insertNotificationWithLog(notification).first
    }

    suspend fun deleteNotificationForUserAndCompetition(userId: Long, competitionId: Long): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        var deleted = false
        val logs = mutableListOf<String>()

        // Strategy 1: Delete with eq("userId", userId) and eq("competitionId", competitionId)
        try {
            postgrest.from("notifications").delete {
                filter {
                    eq("userId", userId)
                    eq("competitionId", competitionId)
                }
            }
            deleted = true
            logs.add("Strategy 1 (userId/competitionId) success")
        } catch (e: Exception) {
            println("Delete notification strategy 1 failed: ${e.message}")
        }

        // Strategy 2: Delete with eq("userid", userId) and eq("competitionid", competitionId)
        try {
            postgrest.from("notifications").delete {
                filter {
                    eq("userid", userId)
                    eq("competitionid", competitionId)
                }
            }
            deleted = true
            logs.add("Strategy 2 (userid/competitionid) success")
        } catch (e: Exception) {
            println("Delete notification strategy 2 failed: ${e.message}")
        }

        // Strategy 3: Delete with eq("user_id", userId) and eq("competition_id", competitionId)
        try {
            postgrest.from("notifications").delete {
                filter {
                    eq("user_id", userId)
                    eq("competition_id", competitionId)
                }
            }
            deleted = true
            logs.add("Strategy 3 (user_id/competition_id) success")
        } catch (e: Exception) {
            println("Delete notification strategy 3 failed: ${e.message}")
        }

        // Strategy 4: Delete notifications for userId where competitionId is null
        try {
            postgrest.from("notifications").delete {
                filter {
                    eq("userId", userId)
                    exact("competitionId", null)
                }
            }
            deleted = true
            logs.add("Strategy 4 (userId, compId null) success")
        } catch (e: Exception) {
            println("Delete notification strategy 4 failed: ${e.message}")
        }

        if (deleted) {
            Pair(true, "OK - Notifica eliminata (${logs.joinToString(", ")})")
        } else {
            Pair(false, "Nessuna notifica eliminata o errore DB")
        }
    }

    suspend fun deleteNotificationById(notificationId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("notifications").delete {
                filter { eq("id", notificationId) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Esegue la query su Supabase PostgREST per recuperare le statistiche
     * degli ufficiali di gara che hanno dato disponibilità e sono stati convocati.
     */
    suspend fun fetchOfficialConvocatedStats(): List<OfficialConvocatedStats> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("v_official_convocated_stats").select().decodeList<OfficialConvocatedStats>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Calcola le statistiche degli ufficiali convocati partendo dalle liste in memoria
     * (inclusi i soli arbitri che hanno dato almeno una disponibilità).
     */
    fun calculateOfficialConvocatedStats(
        users: List<Udg>,
        availabilities: List<Availability>,
        competitions: List<Competition>,
        filterSportId: Int? = null,
        sports: List<sport> = emptyList()
    ): List<OfficialConvocatedStats> {
        val userMap = users.associateBy { it.id }
        val compMap = competitions.associateBy { it.id }

        val filteredAvails = availabilities.filter { av ->
            if (av.userId == null) return@filter false
            if (av.status != "Disponibile" && av.status != "Confermato") return@filter false
            if (filterSportId != null) {
                val comp = compMap[av.competitionId]
                comp?.id_sport == filterSportId
            } else true
        }

        val availByUser = filteredAvails.groupBy { it.userId }

        return availByUser.mapNotNull { (userId, userAvails) ->
            if (userId == null) return@mapNotNull null
            val user = userMap[userId] ?: return@mapNotNull null
            val totalAvail = userAvails.size
            if (totalAvail == 0) return@mapNotNull null

            val convocations = userAvails.count { it.status == "Confermato" }
            val percentage = if (totalAvail > 0) (convocations.toFloat() / totalAvail) * 100f else 0f

            OfficialConvocatedStats(
                userId = user.id ?: userId,
                username = user.username,
                firstname = user.firstname,
                lastname = user.lastname,
                totalAvailabilities = totalAvail,
                totalConvocations = convocations,
                convocationPercentage = percentage,
                sportName = user.scope
            )
        }.sortedByDescending { it.totalAvailabilities }
    }
}
