package com.example.visionsignapp.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker

@Composable
fun NewsButton(
    text: String
    , onClick:()-> Unit
) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(
        containerColor = Color(PrimaryVioletLittleDarker.value),
        contentColor = Color.White,
    ),
        shape = RoundedCornerShape(size = 20.dp))
    {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
            fontSize = 20.sp
        )
    }
}

@Composable
fun NewsTextButton(
    text: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Black,
            fontSize = 15.sp
        )
    }
}