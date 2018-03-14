package com.kotlin.patrick

import android.app.Application
import android.content.Context

class KotlinApplication : Application() {

    init {
        // Memory leak risk! (Sample purposes only)
        appContext = this
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}