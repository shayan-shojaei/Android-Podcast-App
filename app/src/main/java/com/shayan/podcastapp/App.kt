package com.shayan.podcastapp

import android.app.Application

class App : Application() {
    companion object{
        lateinit var preferences: Preferences
    }

    override fun onCreate() {
        super.onCreate()
        preferences = Preferences(context = applicationContext)
    }
}