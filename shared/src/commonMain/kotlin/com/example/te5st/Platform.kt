package com.example.te5st

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun addToCalendar(title: String, startDate: String, endDate: String, location: String, notes: String)

expect fun getSystemTodayMillis(): Long

fun parseDateToMillis(dateStr: String): Long {
    val parts = dateStr.split("-")
    if (parts.size == 3) {
        val y = parts[0].toIntOrNull() ?: 2025
        val m = parts[1].toIntOrNull() ?: 1
        val d = parts[2].toIntOrNull() ?: 1
        val a = (14 - m) / 12
        val y1 = y + 4800 - a
        val m1 = m + 12 * a - 3
        val julianDay = d + (153 * m1 + 2) / 5 + 365 * y1 + y1 / 4 - y1 / 100 + y1 / 400 - 32045
        val epochDays = julianDay - 2440588
        return epochDays.toLong() * 86400 * 1000
    }
    return 1748736000000L
}

fun formatMillisToDateString(millis: Long): String {
    val totalSeconds = millis / 1000
    val totalDays = (totalSeconds / 86400).toInt()
    var days = totalDays + 719468
    val era = (if (days >= 0) days else days - 146096) / 146097
    val doe = days - era * 146097
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = mp + if (mp < 10) 3 else -9
    val year = y + if (m <= 2) 1 else 0
    val monthStr = m.toString().padStart(2, '0')
    val dayStr = d.toString().padStart(2, '0')
    return "$year-$monthStr-$dayStr"
}