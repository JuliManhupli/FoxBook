package com.example.foxbook

import android.app.Application
import android.content.Context

class FoxBook: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: FoxBook? = null

        fun getAppContext(): Context {
            return instance!!.applicationContext
        }
    }
}