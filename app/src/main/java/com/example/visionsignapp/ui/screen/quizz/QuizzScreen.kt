package com.example.visionsignapp.ui.screen.quizz

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.remote.Quizz
import com.example.visionsignapp.ui.remote.fetchQuizzes
import com.example.visionsignapp.ui.screen.container.NavGraph
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizzScreen(navController: NavController, context: android.content.Context) {
    val categoryId = navController.currentBackStackEntry?.arguments?.getString("categoryId")
    val categoryName = navController.currentBackStackEntry?.arguments?.getString("categoryName")
    val quizzes = remember { mutableStateListOf<Quizz>() }
    var currentQuizIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val progress = if (quizzes.isNotEmpty()) {
        (currentQuizIndex + 1).toFloat() / quizzes.size
    } else 0f
    var showPopup by remember { mutableStateOf<String?>(null) }
    val userAnswers = remember { mutableStateListOf<String?>() }
    var countAnswers by remember { mutableIntStateOf(0) }
    var incorrectAnswers by remember { mutableIntStateOf(0) }
    val correctAnswers = remember { mutableStateListOf<String?>() }
    var showResultPopup by remember { mutableStateOf(false) }
    var correctAnswersCount by remember { mutableStateOf(0)}
    var expEarned by remember { mutableStateOf(0)}
    var catProgress by remember { mutableStateOf(0)}

    var timer by remember { mutableStateOf(10) }
    val timerProgress = timer / 10f
    var triggerNextQuiz by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val imageSize = screenWidth * 0.6f

    LaunchedEffect(currentQuizIndex) {
        timer = 10
    }

    LaunchedEffect(timer) {
        if (timer > 0) {
            delay(1000)
            timer--
            if(countAnswers == 7){
                showResultPopup = true
                timer == 0
            }
        } else if (timer == 0) {
            if (currentQuizIndex < quizzes.size - 1) {
                incorrectAnswers++
                currentQuizIndex++
                timer = 10
            }
            else {
                showResultPopup = true
            }
        }
    }

    LaunchedEffect(triggerNextQuiz) {
        if (triggerNextQuiz) {
            delay(1000)
            if (currentQuizIndex < quizzes.size - 1) {
                currentQuizIndex++
            }
            triggerNextQuiz = false
        }
    }

    if (categoryId != null) {
        LaunchedEffect(categoryId) {
            fetchQuizzes(categoryId) { fetchedQuizzes ->
                if (fetchedQuizzes.isNotEmpty()) {
                    quizzes.clear()
                    quizzes.addAll(fetchedQuizzes.shuffled())
                    userAnswers.clear()
                    correctAnswers.clear()
                } else {
                    isError = true
                }
                isLoading = false
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
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(PrimaryViolet.value)
                    )
                } else if (isError) {
                    // Show error screen if there was a problem fetching quizzes
                    Text(
                        text = "Error fetching quizzes. Please try again later.",
                        fontSize = 16.sp,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (quizzes.isEmpty()) {
                    Text(
                        text = "No quizzes available.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = categoryName ?: "",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = (screenWidth * 0.08f).value.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CustomProgressBar(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(45.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .padding(vertical = 8.dp),
                            progressColor = Color(PrimaryVioletLittleDarker.value),
                            borderColor = Color.White
                        )
                        Text(
                            text = "Question: ${currentQuizIndex + 1}/${quizzes.size}",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TimerProgressBar(
                            progress = timerProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .padding(vertical = 8.dp),
                            progressColor = Color(0xFFFFA500),
                            borderColor = Color.White
                        )

                        val currentQuiz = quizzes.getOrNull(currentQuizIndex)
                        currentQuiz?.quizzQuestion?.let {
                            Text(
                                text = it,
                                fontSize = (screenWidth * 0.05f).value.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val baseUrl = "http://192.168.137.1:3000/"
                        currentQuiz?.let {
                            if (it.quizzType == "single") {
                                val imageUrl = it.quizzImage1
                                val fullImageUrl = imageUrl?.let {
                                    if (it.startsWith("http")) it else "$baseUrl$it"
                                }
                                val isGif = fullImageUrl?.endsWith(".gif", ignoreCase = true) == true
                                val size = if (isGif) 290.dp else 300.dp

                                val context = LocalContext.current
                                val imageLoader = ImageLoader.Builder(context)
                                    .components {
                                        if (SDK_INT >= 28) {
                                            add(ImageDecoderDecoder.Factory())
                                        } else {
                                            add(GifDecoder.Factory())
                                        }
                                    }
                                    .build()
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(context).data(data = fullImageUrl).apply(block = {
                                            size(Size.ORIGINAL)
                                        }).build(), imageLoader = imageLoader
                                    ),
                                    contentDescription = "Quiz Image",
                                    modifier = Modifier
                                        .size(imageSize)
                                        .clip(RoundedCornerShape(16.dp))
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                var userAnswer by remember { mutableStateOf("") }
                                var warningMessage by remember { mutableStateOf("") }

                                OutlinedTextField(
                                    value = userAnswer,
                                    onValueChange = {
                                        if (it.matches(Regex("[a-zA-Z]*"))) {
                                            userAnswer = it
                                            warningMessage = ""
                                        }
                                    },
                                    label = {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (userAnswer.isNotEmpty()) Color.Transparent else Color.Transparent
                                                )
                                        ) {
                                            Text(
                                                "Your Answer",
                                                color = if (userAnswer.isEmpty()) Color(
                                                    PrimaryVioletLittleDarker.value
                                                ) else Color(PrimaryVioletLittleDarker.value)
                                            )
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    colors = outlinedTextFieldColors(
                                        focusedBorderColor = Color(PrimaryVioletLittleDarker.value),
                                        unfocusedBorderColor = Color(PrimaryVioletLittleDarker.value),
                                        cursorColor = Color(PrimaryVioletLittleDarker.value),
                                        unfocusedContainerColor = Color.White,
                                        focusedContainerColor = Color.White
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = Color.Black
                                    )
                                )

                                if (warningMessage.isNotEmpty()) {
                                    Text(
                                        warningMessage,
                                        color = Color.Red,
                                        style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(PrimaryVioletDark.value)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    onClick = {
                                        if (userAnswer.isEmpty()) {
                                            warningMessage = "Please write a letter"
                                        }
                                        else {
                                            val isAnswerCorrect = userAnswer.equals(it.reponseCorrecte, ignoreCase = true)
                                            showPopup = if (isAnswerCorrect) "Correct Answer!" else "Incorrect Answer!"
                                            userAnswers.add(userAnswer)
                                            countAnswers++
                                            if (isAnswerCorrect) {
                                                correctAnswersCount++
                                                expEarned++
                                                catProgress++
                                            } else {
                                                incorrectAnswers++
                                            }
                                            triggerNextQuiz = true
                                        }
                                }) {
                                    Text(
                                        text = "Confirm",
                                        color = Color.White
                                    )
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        listOf(it.quizzImage1, it.quizzImage2).forEachIndexed { index, imageUrl ->
                                            val fullImageUrl = imageUrl?.let {
                                                if (it.startsWith("http")) it else "$baseUrl$it"
                                            }
                                            val context = LocalContext.current
                                            val imageLoader = ImageLoader.Builder(context)
                                                .components {
                                                    if (SDK_INT >= 28) {
                                                        add(ImageDecoderDecoder.Factory())
                                                    } else {
                                                        add(GifDecoder.Factory())
                                                    }
                                                }
                                                .build()
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    ImageRequest.Builder(context).data(data = fullImageUrl).apply(block = {
                                                        size(Size.ORIGINAL)
                                                    }).build(), imageLoader = imageLoader
                                                ),
                                                contentDescription = "Quiz Image",
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        val selectedAnswer = (index + 1).toString()
                                                        val isCorrect = selectedAnswer == it.reponseCorrecte
                                                        showPopup = if (isCorrect) "Correct Answer!" else "Incorrect Answer!"
                                                        if (isCorrect) {
                                                            correctAnswersCount++
                                                            expEarned++
                                                            catProgress++
                                                        }
                                                        else {
                                                            incorrectAnswers++
                                                        }
                                                        userAnswers.add(selectedAnswer)
                                                        countAnswers++
                                                        triggerNextQuiz = true

                                                    }
                                            )
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Second set of images (index 3 and 4)
                                        listOf(it.quizzImage3, it.quizzImage4).forEachIndexed { index, imageUrl ->
                                            val fullImageUrl = imageUrl?.let {
                                                if (it.startsWith("http")) it else "$baseUrl$it"
                                            }
                                            val context = LocalContext.current
                                            val imageLoader = ImageLoader.Builder(context)
                                                .components {
                                                    if (SDK_INT >= 28) {
                                                        add(ImageDecoderDecoder.Factory())
                                                    } else {
                                                        add(GifDecoder.Factory())
                                                    }
                                                }
                                                .build()
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    ImageRequest.Builder(context).data(data = fullImageUrl).apply(block = {
                                                        size(Size.ORIGINAL)
                                                    }).build(), imageLoader = imageLoader
                                                ),
                                                contentDescription = "Quiz Image",
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        val selectedAnswer = (index + 3).toString()
                                                        val isCorrect = selectedAnswer == it.reponseCorrecte
                                                        showPopup = if (isCorrect) "Correct Answer!" else "Incorrect Answer!"
                                                        if (isCorrect) {
                                                            correctAnswersCount++
                                                            expEarned++
                                                            catProgress++
                                                        }
                                                        else {
                                                            incorrectAnswers++
                                                        }
                                                        userAnswers.add(selectedAnswer)
                                                        countAnswers++
                                                        triggerNextQuiz = true
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    showPopup?.let { message ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CustomPopup(
                                message = message,
                                onDismiss = { showPopup = null }
                            )
                        }
                    }
                    if (showResultPopup) {
                        navController.navigate("result_screen/$correctAnswersCount/$incorrectAnswers/$expEarned/$catProgress/$categoryId") {
                            popUpTo(NavGraph.Quiz.route) { inclusive = true }
                        }
                        showResultPopup = false
                    }
                }
            }
        }
    }
}

@Composable
fun CustomProgressBar(progress: Float, modifier: Modifier = Modifier, trackColor: Color = Color.White, progressColor: Color = Color(0xFFFFA500), borderColor: Color = Color(0xFFFF69B4)
) {
    Box(
        modifier = modifier
            .height(45.dp)
            .background(borderColor, RoundedCornerShape(50)) // Outer rounded border
            .padding(3.dp) // Padding for border thickness
            .clip(RoundedCornerShape(50)) // Clip the inner content
            .background(trackColor) // Background track color
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress) // Fill width based on progress
                .background(progressColor, RoundedCornerShape(50)) // Progress bar color
        )
    }
}

@Composable
fun TimerProgressBar(progress: Float, modifier: Modifier = Modifier, progressColor: Color, borderColor: Color) {
    Box(
        modifier = modifier
            .background(borderColor, RoundedCornerShape(9.dp))
            .padding(2.dp)
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(9.dp)),
            color = progressColor,
            trackColor = Color.Transparent
        )
    }
}

@Composable
fun CustomPopup(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(message) {
        delay(2000)
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(1f),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = message,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}