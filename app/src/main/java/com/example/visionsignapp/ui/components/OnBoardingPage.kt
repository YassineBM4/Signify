package com.example.visionsignapp.ui.components

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visionsignapp.ui.onboarding.Page
import com.example.visionsignapp.ui.onboarding.pages
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.VisionSignAppTheme

@Composable
fun OnBoardingPage(
    modifier: Modifier = Modifier,
    page: Page
) {
    Column ( modifier = modifier ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.6f),
            painter = painterResource(id = page.image),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = page.title,
            modifier = Modifier.padding(horizontal = 30.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            fontSize = 30.sp,
            lineHeight = 30.sp,
            color = Color(PrimaryVioletDark.value)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = page.description,
            modifier = Modifier.padding(horizontal = 30.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 20.sp,
            lineHeight = 30.sp,
            color = Color.DarkGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingPagePreview() {
    VisionSignAppTheme {
        OnBoardingPage(
            page = pages[0]
        )
    }
}