package com.zad.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zad.app.R
import com.zad.app.ui.theme.Brass
import com.zad.app.ui.theme.Cream
import com.zad.app.ui.theme.Night
import com.zad.app.ui.theme.Ruqaa
import kotlinx.coroutines.delay

/**
 * Brand splash. Quiet, three beats:
 *   1) the pouch fades in (600ms)
 *   2) it "breathes" — gentle scale loop while the wordmark زاد rises (600ms)
 *   3) hold (700ms), then fade out (450ms) → onDone()
 *
 * Same palette as the logo: deep-night ground, cream silhouette, brass underline.
 */
@Composable
fun SplashScreen(onDone: () -> Unit) {
    val pouchAlpha = remember { Animatable(0f) }
    val pouchScale = remember { Animatable(0.86f) }
    var showWord by remember { mutableStateOf(false) }
    var fadingOut by remember { mutableStateOf(false) }

    // Subtle continuous breath after the entrance animation
    val breath by rememberInfiniteTransition(label = "breath").animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath-scale"
    )

    LaunchedEffect(Unit) {
        // beat 1 — fade + ease in
        pouchAlpha.animateTo(1f, tween(600, easing = EaseOutCubic))
        pouchScale.animateTo(1f, tween(700, easing = EaseOutCubic))
        // beat 2 — bring up the wordmark
        showWord = true
        delay(800)
        // beat 3 — hold, then fade out
        delay(700)
        fadingOut = true
        delay(500)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Night),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = !fadingOut,
            enter = fadeIn(tween(0)),
            exit = fadeOut(tween(450))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.wrapContentHeight()
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_zad_mark),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Cream),
                    modifier = Modifier
                        .size(width = 160.dp, height = 200.dp)
                        .alpha(pouchAlpha.value)
                        .scale(pouchScale.value * breath)
                )

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = showWord,
                    enter = fadeIn(tween(500, easing = LinearEasing)) +
                            slideInVertically(tween(500, easing = EaseOutCubic)) { it / 3 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "زاد",
                            color = Cream,
                            style = TextStyle(
                                fontFamily = Ruqaa,
                                fontWeight = FontWeight.Bold,
                                fontSize = 72.sp,
                                lineHeight = 80.sp
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(1.dp)
                                .background(Brass)
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "سعراتك، حبّةً حبّة",
                            color = Cream.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = Ruqaa,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
