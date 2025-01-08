package com.example.visionsignapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.visionsignapp.ui.screen.container.ScreenContainer
import com.example.visionsignapp.ui.theme.VisionSignAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisionSignAppTheme {
                ScreenContainer()
            }
        }
    }
}


