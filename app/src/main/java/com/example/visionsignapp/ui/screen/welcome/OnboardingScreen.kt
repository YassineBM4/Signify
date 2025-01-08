package com.example.visionsignapp.ui.screen.welcome

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.pager.rememberPagerState
import com.example.visionsignapp.ui.components.OnBoardingPage
import com.example.visionsignapp.ui.onboarding.pages
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.visionsignapp.ui.components.NewsButton
import com.example.visionsignapp.ui.components.NewsTextButton
import com.example.visionsignapp.ui.components.PageIndicator
import com.example.visionsignapp.ui.remote.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navController: NavController, context: Context) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        val pagerState = rememberPagerState { pages.size }
        val buttonState = remember {
            derivedStateOf {
                when (pagerState.currentPage) {
                    0 -> listOf("", "Next")
                    1 -> listOf("Back", "Next")
                    2 -> listOf("Back", "Get Started")
                    else -> listOf("", "")
                }
            }
        }
        HorizontalPager(state = pagerState) { index ->
            OnBoardingPage(page = pages[index])
        }
        Spacer(modifier = Modifier.weight(0.8f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PageIndicator(
                modifier = Modifier.width(52.dp),
                pageSize = pages.size,
                selectedPage = pagerState.currentPage
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                val scope = rememberCoroutineScope()

                if (buttonState.value[0].isNotEmpty()) {
                    NewsTextButton(
                        text = buttonState.value[0],
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(page = pagerState.currentPage - 1)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                NewsButton(
                    text = buttonState.value[1],
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage == 2) {
                                PreferencesManager.setOnboardingCompleted(context, true)
                                navController.navigate("login_screen") {
                                    popUpTo("onboarding_screen") { inclusive = true }
                                }
                            } else {
                                pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                            }
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.weight(0.3f))
    }
}