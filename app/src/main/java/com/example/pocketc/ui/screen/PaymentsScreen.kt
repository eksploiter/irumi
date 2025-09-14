package com.example.pocketc.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun PaymentsScreen(brand: Color) {
    Text("결제 내역", fontSize = 28.sp, color = brand)
}
