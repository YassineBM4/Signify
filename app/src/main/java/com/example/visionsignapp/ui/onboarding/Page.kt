package com.example.visionsignapp.ui.onboarding

import androidx.annotation.DrawableRes
import com.example.visionsignapp.R

data class Page(
    val title: String,
    val description: String,
    @DrawableRes val image: Int
)

val pages = listOf(
    Page(
        title = "Welcome To Signify",
        description = "Start your journey to mastering hand sign language and connecting with the world in a whole new way.",
        image = R.drawable.signify_onboarding
    ),
    Page(
        title = "Learn Hand Sign Language",
        description = "Engage yourself with lessons tailored for all levels. Test your progress with interactive quizzes to reinforce your skills and track your improvement!",
        image = R.drawable.learn_handsign
    ),
    Page(
        title = "AI-Powered Video Call Translation",
        description = "Connect effortlessly through video calls using hand sign language, with real-time AI translation converting signs into words for seamless communication.",
        image = R.drawable.ai_signify
    )
)