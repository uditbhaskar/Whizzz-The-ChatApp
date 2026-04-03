package com.example.whizzz

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.data.di.dataModule
import com.example.whizzz.di.appModule
import com.example.whizzz.feature.auth.di.authFeatureModule
import com.example.whizzz.feature.chat.di.chatFeatureModule
import com.example.whizzz.feature.home.di.homeFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application entry: registers Koin modules and the FCM notification channel.
 *
 * App Check is installed in [AppCheckInitProvider].
 * @author udit
 */
class WhizzzApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WhizzzApplication)
            modules(
                dataModule,
                authFeatureModule,
                homeFeatureModule,
                chatFeatureModule,
                appModule,
            )
        }
        val channel = NotificationChannel(
            WhizzzStrings.Notification.CHANNEL_ID_MESSAGES,
            WhizzzStrings.Notification.CHANNEL_NAME_MESSAGES,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }
}
