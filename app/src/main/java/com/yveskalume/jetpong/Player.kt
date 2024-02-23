package com.yveskalume.jetpong

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Player {

    val x = mutableStateOf(0.dp)
    val y = mutableStateOf(0.dp)

    fun move(distance: Dp) {
        x.value += distance
    }
}