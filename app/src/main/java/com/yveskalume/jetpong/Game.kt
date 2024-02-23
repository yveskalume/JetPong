package com.yveskalume.jetpong

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.unit.dp

/**
 * Enum representing different states of the game.
 */
enum class GameState {
    Initial, Playing, UserWon, ComputerWon
}

/**
 * Function to remember the game state using Compose.
 * @param config The configuration for the game.
 * @return the game.
 */
@Composable
fun rememberGame(config: Config) = remember(
    config.screenHeight,
    config.screenWidth
) {
    Game(config = config)
}

/**
 * Class representing the game logic.
 * @property computerPlayer The computer player.
 * @property humanPlayer The human player.
 * @property config The configuration for the game.
 */

class Game(
    val computerPlayer: Player = Player(),
    val humanPlayer: Player = Player(),
    private val config: Config
) {

    val ball = Ball(config.density)

    val gameState: MutableState<GameState> = mutableStateOf(GameState.Initial)

    init {
        setUpGame()
    }

    /**
     * Function to set up the initial game state.
     * This function initializes the positions of the ball, computer player, and human player.
     */
    private fun setUpGame() {
        with(ball) {
            x.value = (config.screenWidth / 2) - (size / 2)
            y.value = (config.screenHeight / 2) - (size / 2)
        }
        with(computerPlayer) {
            x.value = (config.screenWidth / 2) - (width / 2)
            y.value = 0.dp
        }

        with(humanPlayer) {
            x.value = (config.screenWidth / 2) - (width / 2)
            y.value = config.screenHeight - height
        }
    }


    suspend fun play() {
        gameState.value = GameState.Playing
        val computerMachineSpeedAndDirection = ball.defaultXVelocity / 2f
        var lastFrameMillis = withFrameMillis { it }

        while (gameState.value == GameState.Playing) {
            withFrameMillis {
                val deltaTime = it - lastFrameMillis // Time elapsed since last frame

                ball.move(deltaTime)

                with(config.density) {
                    with(computerPlayer) {
                        move(((if (ball.x.value > x.value + (width / 2)) computerMachineSpeedAndDirection else -computerMachineSpeedAndDirection) * deltaTime).toDp())
                    }
                }


                if (isTheBallOnTheComputerPlayerLine() && theBallHaveTouchedThePlayer(computerPlayer)) {
                    changeBallDirectionOnTouchThePlayer(computerPlayer)
                } else if (isTheBallOnTheHumanPlayerLine() && theBallHaveTouchedThePlayer(
                        humanPlayer
                    )
                ) {
                    changeBallDirectionOnTouchThePlayer(humanPlayer)
                } else if (ball.y.value >= config.screenHeight - ball.size && ball.actualYVelocity > 0) {
                    gameState.value = GameState.ComputerWon
                    setUpGame()
                } else if (ball.y.value <= 0.dp && ball.actualYVelocity < 0) {
                    gameState.value = GameState.UserWon
                    setUpGame()
                }

                // Check for wall collisions and bounce the ball
                if (ball.x.value >= config.screenWidth - ball.size && ball.actualXVelocity > 0 || ball.x.value <= 0.dp && ball.actualXVelocity < 0) {
                    ball.actualXVelocity *= -1f
                }

                lastFrameMillis = it
            }
        }
    }

    private fun isTheBallOnTheComputerPlayerLine(): Boolean {
        return ball.y.value <= computerPlayer.y.value + computerPlayer.height && ball.actualYVelocity < 0
    }

    private fun isTheBallOnTheHumanPlayerLine(): Boolean {
        return ball.y.value >= config.screenHeight - humanPlayer.height - ball.size && ball.actualYVelocity > 0
    }

    /**
     * Function to change ball direction upon collision with a player.
     * @param player The player object with which the ball collides.
     */
    private fun changeBallDirectionOnTouchThePlayer(player: Player) {
        ball.actualYVelocity *= -1
        val centerOfThePlayer = player.x.value + (player.width / 2)
        val centerOfBall = ball.x.value + (ball.size / 2)
        val ballCenterRelativeToPlayerCenter =
            centerOfBall - centerOfThePlayer
        val ballHeightOnPaddleRatio =
            ballCenterRelativeToPlayerCenter / ((player.width / 2) + ball.size)
        ball.actualXVelocity = (ballHeightOnPaddleRatio * ball.defaultXVelocity).coerceIn(
            -ball.defaultXVelocity,
            ball.defaultXVelocity
        )
    }

    /**
     * Function to check if the ball has touched a player.
     * @param player The player object to check for collision with the ball.
     * @return True if the ball has touched the player, false otherwise.
     */
    private fun theBallHaveTouchedThePlayer(player: Player): Boolean {
        return (ball.x.value + ball.size >= player.x.value && ball.x.value <= player.x.value + player.width)
    }
}