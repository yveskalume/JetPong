package com.yveskalume.jetpong

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Config(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val playerWidth: Dp = 80.dp,
    val playerHeight: Dp = 20.dp,
    val ballSize: Dp = 20.dp,
    val density: Density
)