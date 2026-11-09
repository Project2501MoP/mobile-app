package com.example.te5st

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseManager {
    val client = createSupabaseClient(
        supabaseUrl = "https://hhybepbctetqvyswqved.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhoeWJlcGJjdGV0cXZ5c3dxdmVkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTE4MzUxMjMsImV4cCI6MjEwNzQxMTEyM30.pv6RuC9DT33VDxvBk2y961w7tCVOooCRmj4aGG0DlyY"
    ) {
        install(Postgrest)
    }
}
