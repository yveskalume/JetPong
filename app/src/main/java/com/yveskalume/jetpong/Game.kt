package com.yveskalume.jetpong

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.unit.dp


@Composable
fun rememberGame(computerPlayer: Player, humanPlayer: Player, config: Config) =
    remember(config.screenHeight, config.screenWidth) {
        mutableStateOf(Game(computerPlayer, humanPlayer, config))
    }

class Game(val computerPlayer: Player, val humanPlayer: Player, private val config: Config) {

    val ball = Ball(config.density)

    val isPlaying: MutableState<Boolean> = mutableStateOf(false)
    val humanWin: MutableState<Boolean> = mutableStateOf(false)

    init {
        with(config) {
            ball.x.value = (screenWidth / 2) - (ballSize / 2)
            ball.y.value = (screenHeight / 2) - (ballSize / 2)

            computerPlayer.x.value = (screenWidth / 2) - (playerWidth / 2)
            computerPlayer.y.value = 0.dp

            humanPlayer.x.value = (screenWidth / 2) - (playerWidth / 2)
            humanPlayer.y.value = screenHeight - playerHeight
        }
    }


    suspend fun play() {
        isPlaying.value = true
        val computerMachineSpeedAndDirection = ball.velocityX / 2f
        var lastFrameMillis = withFrameMillis { it }

        while (isPlaying.value) {
            withFrameMillis {
                val deltaTime = it - lastFrameMillis

                ball.move(deltaTime)

                with(config.density) {
                    computerPlayer.move(((if (ball.x.value > computerPlayer.x.value + (config.playerWidth / 2)) computerMachineSpeedAndDirection else -computerMachineSpeedAndDirection) * deltaTime).toDp())
                }


                if (ball.y.value <= computerPlayer.y.value + config.playerHeight
                    && (ball.x.value + config.ballSize >= computerPlayer.x.value && ball.x.value <= (computerPlayer.x.value + config.playerWidth))
                    && ball.actualYVelocity < 0
                ) {
                    ball.actualYVelocity *= -1
                    val centerOfThePlayer = computerPlayer.x.value + (config.playerWidth / 2)
                    val centerOfBall = ball.x.value + (config.ballSize / 2)
                    val ballCenterPosRelativeToCenterOfThePlayer =
                        centerOfBall - centerOfThePlayer
                    val ballHeightOnPaddleRatio =
                        ballCenterPosRelativeToCenterOfThePlayer / ((config.playerWidth / 2) + config.ballSize)
                    ball.actualXVelocity = (ballHeightOnPaddleRatio * ball.velocityX).coerceIn(
                        -ball.velocityX,
                        ball.velocityX
                    )
                } else if (ball.y.value >= config.screenHeight - config.playerHeight - config.ballSize
                    && (ball.x.value + config.ballSize >= humanPlayer.x.value && ball.x.value <= humanPlayer.x.value + config.playerWidth)
                    && ball.actualYVelocity > 0
                ) {
                    ball.actualYVelocity *= -1
                    val centerOfThePlayer = humanPlayer.x.value + (config.playerWidth / 2)
                    val centerOfBall = ball.x.value + (config.ballSize / 2)
                    val ballCenterPosRelativeToCenterOfThePlayer =
                        centerOfBall - centerOfThePlayer
                    val ballHeightOnPaddleRatio =
                        ballCenterPosRelativeToCenterOfThePlayer / ((config.playerWidth / 2) + config.ballSize)
                    ball.actualXVelocity = (ballHeightOnPaddleRatio * ball.velocityX).coerceIn(
                        -ball.velocityX,
                        ball.velocityX
                    )
                } else if (ball.y.value >= config.screenHeight - config.ballSize && ball.actualYVelocity > 0) {
                    humanWin.value = false
                    isPlaying.value = false
                    endGame()
                } else if (ball.y.value <= 0.dp && ball.actualYVelocity < 0) {
                    humanWin.value = true
                    isPlaying.value = false
                    endGame()
                }

                if (ball.x.value >= config.screenWidth - config.ballSize && ball.actualXVelocity > 0 || ball.x.value <= 0.dp && ball.actualXVelocity < 0) {
                    ball.actualXVelocity *= -1f
                }

                lastFrameMillis = it
            }
        }
    }

    fun endGame() {
        isPlaying.value = false
    }
}