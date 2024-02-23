package com.yveskalume.jetpong

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data class representing the configuration for the game.
 * @property screenWidth The width of the game screen (BoxWithConstraint)
 * @property screenHeight The height of the game screen
 * @property density The screen density (LocalDensity.current).
 */
data class Config(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val density: Density
)