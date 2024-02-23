package com.yveskalume.jetpong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.yveskalume.jetpong.ui.theme.JetPongTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetPongTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isGameFinished by remember {
                        mutableStateOf(false)
                    }

                    var showPlayInstruction by remember {
                        mutableStateOf(false)
                    }

                    var userHaveWon by remember {
                        mutableStateOf(false)
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(32.dp))
                            AnimatedVisibility(
                                visible = isGameFinished,
                            ) {
                                Text(
                                    text = if (userHaveWon) "You won" else "You Loose",
                                    textAlign = TextAlign.Center,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (userHaveWon) Color.Green else Color.Red
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            AnimatedVisibility(
                                visible = showPlayInstruction,
                            ) {
                                Text(
                                    text = "Click on the ball to start playing",
                                    textAlign = TextAlign.Center,
                                    fontSize = 28.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        GameScreen(
                            onGameFinish = { userWon ->
                                userHaveWon = userWon
                                isGameFinished = true
                                showPlayInstruction = true
                            },
                            onStartPlaying = {
                                isGameFinished = false
                                showPlayInstruction = false
                            },
                            onInitializeGame = {
                                showPlayInstruction = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameScreen(
    onGameFinish: (Boolean) -> Unit,
    onStartPlaying: () -> Unit,
    onInitializeGame: () -> Unit
) {

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        val density = LocalDensity.current

        val config by remember(maxWidth, maxHeight, density) {
            mutableStateOf(Config(maxWidth, maxHeight, density = density))
        }

        val game = rememberGame(
            config = config
        )

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(game.gameState.value) {
            when (game.gameState.value) {
                GameState.UserWon -> onGameFinish(true)
                GameState.ComputerWon -> onGameFinish(false)
                GameState.Initial -> onInitializeGame()
                GameState.Playing -> onStartPlaying()
            }
        }


        Box(
            modifier = Modifier
                .playerModifier(game.computerPlayer)
                .background(Color(0xFF970303))

        )

        Box(
            modifier = Modifier
                .playerModifier(game.humanPlayer)
                .background(Color(0xFF039725))
        )

        Box(
            modifier = Modifier
                .zIndex(2f)
                .ballModifier(game)
                .clickable {
                    coroutineScope.launch {
                        game.play()
                    }
                }
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .playerController(game)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetPongTheme {
        GameScreen(
            onGameFinish = {},
            onStartPlaying = {},
            onInitializeGame = {}
        )
    }
}