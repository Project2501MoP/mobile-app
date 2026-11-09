package com.example.te5st

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

object AndroidContextProvider {
    private var appRef: java.lang.ref.WeakReference<android.content.Context>? = null

    var context: android.content.Context?
        get() = appRef?.get()
        set(value) {
            appRef = if (value != null) java.lang.ref.WeakReference(value.applicationContext) else null
        }
}

actual fun addToCalendar(title: String, startDate: String, endDate: String, location: String, notes: String) {
    try {
        val context = AndroidContextProvider.context ?: return
        val startMillis = parseDateToMillis(startDate)
        val endMillis = parseDateToMillis(endDate).coerceAtLeast(startMillis + 3600000)

        val intent = android.content.Intent(android.content.Intent.ACTION_INSERT).apply {
            data = android.provider.CalendarContract.Events.CONTENT_URI
            putExtra(android.provider.CalendarContract.Events.TITLE, title)
            putExtra(android.provider.CalendarContract.Events.DESCRIPTION, notes)
            putExtra(android.provider.CalendarContract.Events.EVENT_LOCATION, location)
            putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(android.provider.CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(android.provider.CalendarContract.Events.HAS_ALARM, 1)
            putExtra(android.provider.CalendarContract.Reminders.MINUTES, 24 * 60)
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

actual fun getSystemTodayMillis(): Long = System.currentTimeMillis()