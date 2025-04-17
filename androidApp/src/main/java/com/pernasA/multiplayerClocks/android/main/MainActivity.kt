package com.pernasA.multiplayerClocks.android.main

import android.os.Bundle

import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import com.pernasA.multiplayerClocks.android.utils.MyApplicationTheme
import com.pernasA.multiplayerClocks.android.utils.MyClocksAppTheme

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyClocksAppTheme (false) {
                Navigation()
            }
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
