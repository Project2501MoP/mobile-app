package com.example.te5st

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UdgRepository {
    private val postgrest = SupabaseManager.client.postgrest

    suspend fun fetchUsers(): List<Udg> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("v_udg").select().decodeList<Udg>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun insertUserWithLog(user: Udg): Triple<Boolean, Udg?, String> = withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from("udg").insert(user) {
                select()
            }.decodeSingle<Udg>()
            Triple(true, result, "OK - Ufficiale registrato su Supabase (username='${result.username}', id=${result.id})")
        } catch (e: Exception) {
            val err1 = e.message ?: e.toString()
            println("Error in insertUserWithLog: $err1")
            e.printStackTrace()
            try {
                postgrest.from("udg").insert(user)
                Triple(true, user, "OK - Ufficiale '@${user.username}' inserito nella tabella 'udg'")
            } catch (e2: Exception) {
                val err2 = e2.message ?: e2.toString()
                println("Error in insertUser fallback: $err2")
                e2.printStackTrace()
                Triple(false, null, "ERRORE inserimento ufficiale: $err2")
            }
        }
    }

    suspend fun insertUser(user: Udg): Boolean {
        return insertUserWithLog(user).first
    }

    suspend fun insertUdgSport(udgSport: Udg_sport): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val json = buildJsonObject {
                udgSport.id_udg?.let { put("id_udg", it) }
                udgSport.id_sport?.let { put("id_sport", it) }
            }
            postgrest.from("udg_sport").insert(json)
            Pair(true, "OK - Inserita riga in 'udg_sport' (id_udg=${udgSport.id_udg}, id_sport=${udgSport.id_sport})")
        } catch (e: Exception) {
            val err1 = e.message ?: e.toString()
            println("Error in insertUdgSport json: $err1")
            e.printStackTrace()
            try {
                postgrest.from("udg_sport").insert(udgSport)
                Pair(true, "OK - Udg_sport inserito tramite DTO")
            } catch (e2: Exception) {
                val err2 = e2.message ?: e2.toString()
                println("Error in insertUdgSport fallback: $err2")
                e2.printStackTrace()
                Pair(false, "ERRORE inserimento udg_sport: $err2")
            }
        }
    }

    suspend fun updateUserPassword(username: String, newPass: String): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("udg").update({
                set("password", newPass)
                set("isfirstlogin", false)
            }) {
                filter {
                    eq("username", username)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateUser(user: Udg): Boolean = withContext(Dispatchers.IO) {
        try {
            if (user.id != null) {
                postgrest.from("udg").update(user) {
                    filter {
                        eq("id", user.id)
                    }
                }
                true
            } else if (!user.username.isNullOrEmpty()) {
                postgrest.from("udg").update(user) {
                    filter {
                        eq("username", user.username)
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
}
