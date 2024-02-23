package com.yveskalume.jetpong

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class Ball(private val screenDensity: Density) {
    val size: Dp = 20.dp
    val x = mutableStateOf(0.dp)
    val y = mutableStateOf(0.dp)

    val defaultXVelocity = 2f
    private val defaultYVeloticty = 1f

    var actualXVelocity by mutableFloatStateOf(
        Random.nextInt(
            -defaultXVelocity.toInt(),
            defaultXVelocity.toInt()
        ).toFloat()
    )

    var actualYVelocity by mutableFloatStateOf(-defaultYVeloticty)


    fun move(delta: Long) {
        x.value += with(screenDensity) { (actualXVelocity * delta).toDp() }
        y.value += with(screenDensity) { (actualYVelocity * delta).toDp() }
    }
}