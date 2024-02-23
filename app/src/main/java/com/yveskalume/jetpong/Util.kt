package com.yveskalume.jetpong

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity

fun Modifier.ballOffset(ball: Ball): Modifier {
    return this.offset(ball.x.value, ball.y.value)
}

fun Modifier.playerModifier(player: Player): Modifier {
    return this
        .offset(player.x.value, player.y.value)
        .width(player.width)
        .height(player.height)
}

fun Modifier.playerController(game: Game): Modifier = composed {
    val density = LocalDensity.current
    this@composed.draggable(
        rememberDraggableState {
            with(density) {
                game.humanPlayer.move(it.toDp())
            }
        },
        orientation = Orientation.Horizontal
    )
}