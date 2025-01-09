package com.pernasA.multiplayerClocks.android.main

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle

import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import com.pernasA.multiplayerClocks.android.utils.MyApplicationTheme
import com.pernasA.multiplayerClocks.android.utils.MyClocksAppTheme


class MainActivity : BaseActivity() {
    private val prefsName = "AppPrefs"
    private val launchCounter = "launch_counter"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyClocksAppTheme (false) {
                Navigation()
            }
        }
    }

    private fun showRateAppDialog(sharedPreferences: SharedPreferences) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("¡Gracias por usar la aplicación!")
        dialogBuilder.setMessage("Si te gusta la aplicación, por favor califícala en Google Play. Me ayuda mucho a seguir mejorándola :)")

        dialogBuilder.setPositiveButton("Calificar") { _, _ ->
            openGooglePlayStore()
            sharedPreferences.edit().putInt(launchCounter, 50).apply()
        }

        dialogBuilder.setNegativeButton("Más tarde") { dialog, _ ->
            dialog.dismiss()
            sharedPreferences.edit().putInt(launchCounter, 15).apply()
        }

        dialogBuilder.setNeutralButton("Ya la valoré") { dialog, _ ->
            dialog.dismiss()
            sharedPreferences.edit().putInt(launchCounter, 900).apply()
        }
        dialogBuilder.create().show()
    }

    private fun openGooglePlayStore() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }
}



@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
