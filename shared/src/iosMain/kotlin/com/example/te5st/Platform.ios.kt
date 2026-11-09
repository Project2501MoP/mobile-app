package com.example.te5st

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun addToCalendar(title: String, startDate: String, endDate: String, location: String, notes: String) {
    // iOS calendar stub
}

actual fun getSystemTodayMillis(): Long = ((platform.Foundation.NSDate().timeIntervalSinceReferenceDate() + platform.Foundation.NSTimeIntervalSince1970) * 1000.0).toLong()