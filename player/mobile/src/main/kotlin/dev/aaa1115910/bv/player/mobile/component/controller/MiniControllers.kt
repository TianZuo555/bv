package dev.aaa1115910.bv.player.mobile.component.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dev.aaa1115910.bv.player.mobile.util.formatMinSec

@Composable
fun MiniControllers(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentTime: Long,
    totalTime: Long,
    currentSeekPosition: Float,
    bufferedSeekPosition: Float,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onEnterFullScreen: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopControllers(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            onBack = onBack
        )
        BottomControllers(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            isPlaying = isPlaying,
            currentTime = currentTime,
            totalTime = totalTime,
            currentSeekPosition = currentSeekPosition,
            bufferedSeekPosition = bufferedSeekPosition,
            onPlay = onPlay,
            onPause = onPause,
            onEnterFullScreen = onEnterFullScreen,
            onSeekToPosition = onSeekToPosition
        )
    }
}

@Composable
private fun TopControllers(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = "这是一个标题",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        }
    }
}

@Composable
private fun BottomControllers(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentTime: Long,
    totalTime: Long,
    currentSeekPosition: Float,
    bufferedSeekPosition: Float,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onEnterFullScreen: () -> Unit,
    onSeekToPosition: (Long) -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        ConstraintLayout {
            val (playButton, seekSlider, positionText, fullscreenButton) = createRefs()

            PlayPauseButton(
                modifier = Modifier
                    .constrainAs(playButton) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                isPlaying = isPlaying,
                onPlay = onPlay,
                onPause = onPause
            )

            SeekSlider(
                modifier = Modifier.constrainAs(seekSlider) {
                    top.linkTo(parent.top)
                    start.linkTo(playButton.end)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(positionText.start)
                    width = Dimension.preferredWrapContent
                },
                currentSeekPosition = currentSeekPosition,
                bufferedSeekPosition = bufferedSeekPosition,
                onSeekChange = { onSeekToPosition((it * totalTime).toLong()) }
            )

            Text(
                modifier = Modifier.constrainAs(positionText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(fullscreenButton.start)
                },
                text = "${currentTime.formatMinSec()}/${totalTime.formatMinSec()}",
                color = Color.White
            )

            IconButton(
                modifier = Modifier.constrainAs(fullscreenButton) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
                onClick = onEnterFullScreen
            ) {
                Icon(
                    imageVector = Icons.Rounded.Fullscreen,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(device = "spec:width=1080px,height=600px")
@Composable
fun MiniControllerPreview() {
    MaterialTheme {
        MiniControllers(
            isPlaying = true,
            currentTime = 12345,
            totalTime = 123456,
            currentSeekPosition = 0.3f,
            bufferedSeekPosition = 0.6f,
            onBack = {},
            onPlay = {},
            onPause = {},
            onEnterFullScreen = {},
            onSeekToPosition = {},
        )
    }
}