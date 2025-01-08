package com.example.visionsignapp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.remote.fetchProfile
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker

@Composable
fun CustomLevelBar(userPreferences: UserPreferences) {
    var progress by remember { mutableStateOf(0f) }
    var levelProgress by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        fetchProfile(userPreferences) { user ->
            levelProgress = user.levelProgress ?: 0
        }
    }

    when (levelProgress) {
        0 -> progress = 0.0f
        100 -> progress = 0.14f
        200 -> progress = 0.28f
        300 -> progress = 0.42f
        400 -> progress = 0.56f
        500 -> progress = 0.7f
        600 -> progress = 0.84f
        700 -> progress = 0.0f
    }

    val size by animateFloatAsState(
        targetValue = progress,
        tween(
            durationMillis = 1000,
            delayMillis = 200,
            easing = LinearOutSlowInEasing
        )
    )

    Column(
        modifier = Modifier
            .offset(y = (-10).dp)
            .padding(top = 15.dp, start = 10.dp)
    ) {
        // Progress Bar
        Box(
            modifier = Modifier
                .height(17.dp)
                .width(width = 210.dp)
        ) {
            // for the background of the ProgressBar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(9.dp))
                    .background(color = Color.White)
            )
            // for the progress of the ProgressBar
            Box(
                modifier = Modifier
                    .fillMaxWidth(size)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(PrimaryVioletLittleDarker.value))
                    .animateContentSize()
            )
        }
    }

    LaunchedEffect(key1 = true) {
        levelProgress = levelProgress
   }

}