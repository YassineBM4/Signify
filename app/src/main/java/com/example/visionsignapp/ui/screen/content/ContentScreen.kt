package com.example.visionsignapp.ui.screen.content

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.remote.Content
import com.example.visionsignapp.ui.remote.fetchContents
import com.example.visionsignapp.ui.theme.PrimaryViolet
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker

@Composable
fun ContentScreen(navController: NavController) {
    val categoryId = navController.currentBackStackEntry?.arguments?.getString("categoryId")
    val categoryName = navController.currentBackStackEntry?.arguments?.getString("categoryName")

    var currentContentIndex by remember { mutableStateOf(0) }
    val contents = remember { mutableStateListOf<Content>() }
    var isLoading by remember { mutableStateOf(true) }
    var isAvailable by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val circleSize = screenWidth * 0.6f

    // Fetch category content if categoryId is available
    if (categoryId != null) {
        LaunchedEffect(categoryId) {
            fetchContents(categoryId) { fetchedContents ->
                contents.clear()
                contents.addAll(fetchedContents)
                isLoading = false
            }
        }
    } else {
        println("Error: category ID is missing.")
    }

    val progress = if (contents.isNotEmpty()) {
        (currentContentIndex + 1).toFloat() / contents.size
    } else 0f

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
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = categoryName ?: "",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = (screenWidth * 0.075f).value.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CustomProgressBar(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight * 0.05f)
                                .clip(RoundedCornerShape(percent = 50))
                                .padding(vertical = 8.dp),
                            progressColor = Color(PrimaryVioletLittleDarker.value),
                            borderColor = Color.White
                        )

                        Text(
                            text = "Progress: ${currentContentIndex + 1}/${contents.size}",
                            fontSize = (screenWidth * 0.04f).value.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val currentContent = contents.getOrNull(currentContentIndex)
                        val baseUrl = "http://192.168.137.1:3000/"
                        val imageUrl = currentContent?.contentImage

                        val fullImageUrl = imageUrl?.let {
                            if (it.startsWith("http")) it else "$baseUrl$it" // Prepend base URL if it's a relative URL
                        }
                        val painter = rememberImagePainter(
                            data = fullImageUrl ?: R.drawable.baseline_broken_image_24,
                        )
                        if(fullImageUrl == null){
                                isAvailable = true
                        }
                        val isGif = fullImageUrl?.endsWith(".gif", ignoreCase = true) == true
                        val size = if (isGif) 290.dp else 300.dp
                        Box(
                            modifier = Modifier
                                .size(340.dp)
                                .clip(CircleShape)
                                .background(Color(PrimaryViolet.value)),
                            contentAlignment = Alignment.Center
                        ) {
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
                            if(isAvailable) {
                                Image(
                                    painter = painterResource(id = R.drawable.maintenance),
                                    contentDescription = "IN Maintenance",
                                    modifier = Modifier.size(screenHeight*0.34f)
                                )
                            } else {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(context).data(data = fullImageUrl).apply(block = {
                                            size(Size.ORIGINAL)
                                        }).build(), imageLoader = imageLoader
                                    ),
                                    contentDescription = "Content Image",
                                    modifier = Modifier
                                        .size(size)
                                        .clip(CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentContent?.contentName ?: "In Development",
                            fontSize = (screenWidth * 0.12f).value.sp,
                            color = Color(PrimaryVioletLittleDarker.value),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .offset(y = (-screenHeight * 0.05f).value.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        /*if(isAvailable){
                            Button(
                                onClick = {navController.navigate("lesson_screen")},
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(PrimaryVioletDark.value)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                            ) {
                                Text(
                                    text = "Back",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }*/
                         if (currentContentIndex > 0) {
                            Button(
                                onClick = { currentContentIndex-- },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(PrimaryVioletDark.value)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                            ) {
                                Text(
                                    text = "Back",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                         if (currentContentIndex < contents.size - 1) {
                            Button(
                                onClick = { currentContentIndex++ },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(PrimaryVioletDark.value)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                            ) {
                                Text(
                                    text = "Next",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    navController.navigate("quiz_screen/$categoryId/$categoryName")
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(PrimaryVioletDark.value)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                            ) {
                                Text(
                                    text = "Let's start the quiz!",
                                    color = Color.White,
                                    fontSize = (screenWidth * 0.05f).value.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomProgressBar(
    progress: Float, // Progress value between 0f and 1f
    modifier: Modifier = Modifier,
    trackColor: Color = Color.White,
    progressColor: Color = Color(0xFFFFA500), // Orange color
    borderColor: Color = Color(0xFFFF69B4)    // Pink border
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
