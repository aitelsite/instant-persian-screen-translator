package com.instantpersian.screentranslator

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.instantpersian.screentranslator.translation.TranslationRepository

class InstantPersianApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_CAPTURE,
                "ضبط صفحه برای OCR",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "اعلان هنگام خواندن متن از تصویر صفحه" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        TranslationRepository.prewarm(this)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_CAPTURE = "instant_persian_capture"
    }
}
