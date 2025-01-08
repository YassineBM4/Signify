package com.example.visionsignapp.ui.screen.lesson

import android.os.Build
import android.util.Log
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.components.CustomLevelBar
import com.example.visionsignapp.ui.remote.Category
import com.example.visionsignapp.ui.remote.CategoryProgress
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.remote.fetchProfile
import com.example.visionsignapp.ui.remote.updateCategoryLock
import com.example.visionsignapp.ui.screen.container.NavGraph
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletLight
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@Composable
fun LessonScreen(navController: NavController, userPreferences: UserPreferences, context: android.content.Context) {
    var fullName by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(0) }
    var gems by remember { mutableStateOf(0) }

    var showUnlockDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val iconSize = screenWidth * 0.15f
    val fontSize = screenWidth * 0.05f
    val coinSize = screenWidth * 0.12f
    val textPadding = screenWidth * 0.02f

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            fetchProfile(userPreferences) { user ->
                fullName = user.fullName ?: "Full name not available"
                icon = user.icon ?: ""
                level = user.level ?: 0
                gems = user.gems ?: 0
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { BottomNavigationBar(navController) },
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryViolet.copy(alpha = 0.9f),
                        PrimaryViolet.copy(alpha = 0.3f)
                    )
                )
            )
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    fetchProfile(userPreferences) { user ->
                        fullName = user.fullName ?: "Full name not available"
                        icon = user.icon ?: ""
                        level = user.level ?: 0
                        gems = user.gems ?: 0
                    }
                    isRefreshing = false
                }
            }
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val imageResId = context.resources.getIdentifier(
                        icon,
                        "drawable",
                        context.packageName
                    )
                    Image(
                        painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.profile_icon),
                        contentDescription = "Profile Icon",
                        modifier = Modifier
                            .size(iconSize)
                            .clickable { navController.navigate("profile_screen") }

                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "$fullName ~ lvl.$level",
                            fontSize = fontSize.value.sp, // Dynamically calculated font size
                            color = Color(PrimaryVioletDark.value),
                            modifier = Modifier.padding(start = textPadding, top = textPadding)
                        )
                        CustomLevelBar(userPreferences)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            navController.navigate("shop_screen")
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.signify_coin),
                            contentDescription = "Signify Coin",
                            modifier = Modifier
                                .size(coinSize)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                                .padding(top = textPadding, end = textPadding)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        val animatedGems by animateIntAsState(
                            targetValue = gems,
                            animationSpec = tween(durationMillis = 500)
                        )
                        androidx.compose.material3.Text(
                            text = "$animatedGems SC",
                            fontSize = (fontSize.value * 0.9f).sp, // Slightly smaller than name font size
                            color = Color(PrimaryVioletDark.value)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 16.dp),
                    color = Color.LightGray
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your Categories :",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 10.dp, start = 20.dp, top = 16.dp)
                )
                ProgressList(userPreferences, context = context, navController)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
        }
    }

    // Unlock Dialog
    if (showUnlockDialog && selectedCategory != null) {
        UnlockDialog(
            category = selectedCategory!!,
            gems = gems,
            onConfirm = {
                showUnlockDialog = false
            },
            onDismiss = {
                showUnlockDialog = false
            }
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Lessons,
        NavigationItem.VideoCall,
        NavigationItem.Profile
    )

    NavigationBar(
        containerColor = Color(PrimaryVioletLittleDarker.value),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon(), contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemColors(
                    selectedIconColor = Color(PrimaryVioletLittleDarker.value),
                    selectedTextColor = Color.White,
                    selectedIndicatorColor = Color.White,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White,
                    disabledIconColor = Color.Gray,
                    disabledTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

sealed class NavigationItem(val route: String, val title: String, val icon: @Composable () -> Painter) {
    object Home : NavigationItem("home_screen", "Home", { painterResource(id = R.drawable.baseline_home_24) })
    object Lessons : NavigationItem("lesson_screen", "Lessons", { painterResource(id = R.drawable.baseline_cast_for_education_24) })
    object VideoCall : NavigationItem("login_page", "Video Call", { painterResource(id = R.drawable.baseline_add_call_24) })
    object Profile : NavigationItem("profile_screen", "Profile", { painterResource(id = R.drawable.baseline_face_24) })
}

@Composable
fun ProgressList(userPreferences: UserPreferences, context: android.content.Context, navController: NavController) {
    var categoriesProgress by remember { mutableStateOf<List<CategoryProgress>?>(null) }
    var showUnlockDialog by remember { mutableStateOf(false) }
    var selectedCategoryProgress by remember { mutableStateOf<CategoryProgress?>(null) }
    var gems by remember { mutableStateOf(0) }
    var isUnlocking by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val fetchUserData = {
        coroutineScope.launch {
            fetchProfile(userPreferences) { user ->
                categoriesProgress = user.categoriesProgress
                gems = user.gems ?: 0
            }
        }
    }

    // Trigger initial fetch of user data
    LaunchedEffect(Unit) {
        fetchUserData()
    }

    // Show loading indicator when data is null or unlocking is in progress
    if (categoriesProgress.isNullOrEmpty() && !isUnlocking) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categoriesProgress ?: emptyList()) { categoryProgress ->
                ProgressCard(
                    context = context,
                    categoryProgress = categoryProgress,
                    onUnlock = { selectedProgress ->
                        selectedCategoryProgress = selectedProgress
                        showUnlockDialog = true
                    },
                    navController = navController
                )
            }
        }

        // Unlock dialog for category
        if (showUnlockDialog && selectedCategoryProgress != null) {
            UnlockDialog(
                category = selectedCategoryProgress!!.category!!,
                gems = gems,
                onConfirm = {
                    val selectedCategory = selectedCategoryProgress!!.category
                    if (selectedCategory == null) {
                        Log.e("ProgressList", "Category is null!")
                        return@UnlockDialog
                    }

                    if (gems >= selectedCategory.catPrice ?: 0) {
                        gems -= selectedCategory.catPrice ?: 0
                        isUnlocking = true
                        coroutineScope.launch {
                            updateCategoryLock(
                                categoryId = selectedCategory.catId ?: "",
                                isLockedStatus = false,
                                userPreferences = userPreferences,
                                callback = { result ->
                                    result.onSuccess { updatedUser ->
                                        gems = updatedUser.gems ?: 0
                                        fetchUserData()
                                        selectedCategoryProgress = null
                                        showUnlockDialog = false
                                        navController.navigate("lesson_screen") {
                                            popUpTo("progress_screen") { inclusive = true }
                                        }
                                    }
                                    result.onFailure {
                                        Log.e("ProgressList", "Unlock failed: ${it.message}")
                                        gems += selectedCategory.catPrice ?: 0
                                    }
                                    isUnlocking = false
                                }
                            )
                        }
                    } else {
                        Log.e("ProgressList", "Not enough gems")
                    }
                }
                ,
                onDismiss = {
                    showUnlockDialog = false
                }
            )
        }
    }
}

@Composable
fun ProgressCard(context: android.content.Context, onUnlock: (CategoryProgress) -> Unit, navController: NavController, categoryProgress: CategoryProgress) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val cardWidth = screenWidth * 0.9f // Card width is 90% of the screen width
    val cardHeight = screenHeight * 0.15f // Card height is 20% of the screen height

    val category = categoryProgress.category
    val isLocked = categoryProgress.isLocked ?: true
    if (category == null) return

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(PrimaryVioletLittleDarker.value))
            .padding(4.dp)
            .clickable(enabled = !isLocked) {
                if (!isLocked) {
                    navController.navigate(
                        NavGraph.Content.createRoute(
                            category.catId ?: "null",
                            category.catName ?: ""
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (!isLocked) {
            // Unlocked Card Content
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val imageResId = context.resources.getIdentifier(
                    category.catIcon,
                    "drawable",
                    context.packageName
                )
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Category Icon",
                        modifier = Modifier
                            .size(cardHeight * 0.65f)
                            .background(Color.Transparent)
                            .offset(y = -(cardHeight * 0.05f))
                    )
                }
                Text(
                    text = category.catName.orEmpty(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = (cardHeight * 0.15f).value.sp,
                    modifier = Modifier
                        .offset(y = (-cardHeight * 0.15f))
                )
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(8.dp)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val imageResId = context.resources.getIdentifier(
                        category.catIcon,
                        "drawable",
                        context.packageName
                    )
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = "Category Icon",
                            modifier = Modifier
                                .size(cardHeight * 0.3f)
                                .background(Color.Transparent)
                        )
                    }

                    Text(
                        text = category.catName.orEmpty(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = (cardHeight * 0.1f).value.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = category.catName.orEmpty(),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = (cardHeight * 0.15f).value.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(
                            onClick = { onUnlock(categoryProgress) },
                            colors = ButtonDefaults.buttonColors(Color(PrimaryVioletDark.value)),
                            modifier = Modifier
                                .height(cardHeight * 0.33f)
                                .width(cardWidth * 0.6f)
                        ) {
                            Text(
                                text = "Unlock ${category.catPrice}SC",
                                color = Color.White,
                                fontSize = (cardHeight * 0.1f).value.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = category.catName.orEmpty(),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = (cardHeight * 0.15f).value.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(
                            onClick = { onUnlock(categoryProgress) },
                            colors = ButtonDefaults.buttonColors(Color(PrimaryVioletDark.value)),
                            modifier = Modifier
                                .height(cardHeight * 0.33f)
                                .width(cardWidth * 0.6f)
                        ) {
                            Text(
                                text = "Unlock ${category.catPrice}SC",
                                color = Color.White,
                                fontSize = (cardHeight * 0.1f).value.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnlockDialog(category: Category, gems: Int, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Unlock ${category.catName}") },
        text = {
            Text(
                "This category costs ${category.catPrice} SC. " +
                        "You currently have $gems SC. Do you want to proceed?"
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Unlock")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}