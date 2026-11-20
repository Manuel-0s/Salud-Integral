package com.example.healthtrack

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class SaludIntegralApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
