package com.example.visionsignapp.ui.screen.quizz

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.remote.fetchProfile
import com.example.visionsignapp.ui.repository.awardGems
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(correctAnswersCount: Int, incorrectAnswers: Int, expEarned: Int, catProgress: Int, categoryId: String, navController: NavController) {
    var showTitle by remember { mutableStateOf(false) }
    var showLevelUp by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showRewards by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    var earnedSC by remember { mutableStateOf(0) }
    var earnedXP by remember { mutableStateOf(0) }

    // Loading state for API request
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf<Boolean?>(null) }

    val context = LocalContext.current
    val userPreferences = UserPreferences(context = context)
    var fullName by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(0) }
    var gems by remember { mutableStateOf(0) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val imageSize = screenWidth * 0.6f

    LaunchedEffect(Unit) {
        fetchProfile(userPreferences) { user ->
            fullName = user.fullName ?: "Full name not available"
            icon = user.icon ?: ""
            level = user.level ?: 0
            gems = user.gems ?: 0
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        showTitle = true
        delay(500)
        showLevelUp = true
        delay(500)
        showStats = true
        delay(500)
        showRewards = true
        delay(500)
        showButton = true
    }

    val handleAwardGems = {
        isLoading = true
        CoroutineScope(Dispatchers.Main).launch {
            val result = awardGems(userPreferences, correctAnswersCount, expEarned, catProgress, categoryId)
            isLoading = false
            result.onSuccess {
                isSuccess = true
            }.onFailure {
                isSuccess = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryViolet,
                        PrimaryViolet.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Text(
                    text = "QUIZ RESULT",
                    fontSize = (screenWidth * 0.08f).value.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(PrimaryVioletDark.value),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = 20.dp)
                )


            Spacer(modifier = Modifier.height(10.dp))

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if(earnedXP == 700) {
                        if (level == 0) {
                            Image(
                                painter = painterResource(id = R.drawable.one),
                                contentDescription = "level 1",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        } else if ( level == 1 ){
                            Image(
                                painter = painterResource(id = R.drawable.two),
                                contentDescription = "level 2",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        } else if ( level == 2 ){
                            Image(
                                painter = painterResource(id = R.drawable.three),
                                contentDescription = "level 3",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        } else if ( level == 3 ){
                            Image(
                                painter = painterResource(id = R.drawable.four),
                                contentDescription = "level 4",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        } else if ( level == 4 ){
                            Image(
                                painter = painterResource(id = R.drawable.five),
                                contentDescription = "level 5",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        } else if ( level == 5 ){
                            Image(
                                painter = painterResource(id = R.drawable.six),
                                contentDescription = "level 6",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        } else if ( level == 6 ){
                            Image(
                                painter = painterResource(id = R.drawable.seven),
                                contentDescription = "level 7",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        } else if ( level == 7 ){
                            Image(
                                painter = painterResource(id = R.drawable.eight),
                                contentDescription = "level 8",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        } else if ( level == 8 ){
                            Image(
                                painter = painterResource(id = R.drawable.nine),
                                contentDescription = "level 9",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        } else if ( level == 9 ){
                            Image(
                                painter = painterResource(id = R.drawable.ten),
                                contentDescription = "level 9",
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "CONGRATS, Level Up !",
                            fontSize = (screenWidth * 0.06f).value.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(PrimaryVioletLittleDarker.value),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            style = TextStyle(lineHeight = 40.sp)
                        )
                    } else {
                        val imageResId = context.resources.getIdentifier(
                            icon, // The name of the image stored in your MongoDB database
                            "drawable",
                            context.packageName
                        )
                        if(imageResId!=0) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = "Profile Icon",
                                modifier = Modifier
                                    .size(170.dp)
                                    .background(Color.Transparent)
                            )
                        }
                        else {
                            Image(
                                painter = painterResource(id = R.drawable.profile_icon),
                                contentDescription = "Profile Icon",
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.Transparent)
                            )
                        }

                            Text(
                                text = "$fullName ~ lvl.$level",
                                fontSize = (screenWidth * 0.05f).value.sp,
                                color = Color(PrimaryVioletDark.value),
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                            )
                        ResultLevelBar(earnedXP)
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "CLOSE, Try again !",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(PrimaryVioletLittleDarker.value),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            style = TextStyle(lineHeight = 40.sp)
                        )
                    }
                }

            Column (
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                /*AnimatedVisibility(
                    visible = showStats,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Correct Answers : $correctAnswersCount",
                            fontSize = 20.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            style = TextStyle(lineHeight = 40.sp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Incorrect Answers : $incorrectAnswers",
                            fontSize = 20.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            style = TextStyle(lineHeight = 40.sp)
                        )
                    }
                }*/

                Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp)
                    ) {
                        earnedSC = correctAnswersCount*10
                        earnedXP = correctAnswersCount*100
                        RewardItem(icon = R.drawable.signify_coin, label = "+ $earnedSC SC", description = "Signify Coins Earned")
                        RewardItem(icon = R.drawable.signify_xp, label = "+ $earnedXP XP", description = "Exp Points Earned")
                    }
            }

            Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        handleAwardGems()
                        navController.navigate("lesson_screen")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(PrimaryVioletDark.value)),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(screenHeight * 0.08f)
                        .offset(y = (-screenHeight * 0.05f).value.dp)
                ) {
                    Text(text = if (isLoading) "Claiming Rewards..." else "Claim Rewards", color = Color.White, fontSize = (screenWidth * 0.04f).value.sp)
                }
        }
    }
}

@Composable
fun RewardItem(@DrawableRes icon: Int, label: String, description: String) {
    val numericValue = label.filter { it.isDigit() }.toIntOrNull() ?: 0

    val animatedNumber by animateIntAsState(
        targetValue = numericValue,
        animationSpec = tween(durationMillis = 500)
    )

    val animatedLabel = label.replace(numericValue.toString(), animatedNumber.toString())

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Text(text = animatedLabel, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ResultLevelBar(levelProgress: Int) {
    var progress by remember { mutableStateOf(0f) }

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
                .height(22.dp)
                .width(width = 270.dp)
        ) {
            // for the background of the ProgressBar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(9.dp))
                    .background(color = Color(PrimaryViolet.value))
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
}