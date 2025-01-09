package com.pernasA.multiplayerClocks.android.main

import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity

open class BaseActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { adjustFontScale(it) })
    }

    private fun adjustFontScale(context: Context): Context {
        val configuration = context.resources.configuration
        if (configuration.fontScale != 1.0f) {
            val newConfig = Configuration(configuration)
            newConfig.fontScale = 1.0f
            return context.createConfigurationContext(newConfig)
        }
        return context
    }
}