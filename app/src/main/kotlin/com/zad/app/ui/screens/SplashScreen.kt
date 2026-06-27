package com.zad.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zad.app.ui.theme.Brass
import com.zad.app.ui.theme.Cream
import com.zad.app.ui.theme.Night
import com.zad.app.ui.theme.Ruqaa
import kotlin.math.PI
import kotlin.math.sin

/**
 * Sahrah-style brand splash: the pouch draws itself on the deep-night ground
 * — body, then gather, then drawstring — the knot drops from above, the folds
 * and stitches fade in, then زاد rises with a brass hairline. The whole mark
 * then breathes a moment before fading to Today.
 *
 * Same colours as the مزود mark — Night ground · Cream silhouette · Brass hairline.
 */
@Composable
fun SplashScreen(onDone: () -> Unit) {
    val totalMs = 3400
    val anim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        anim.animateTo(1f, tween(totalMs, easing = LinearEasing))
        onDone()
    }

    val tMs = anim.value * totalMs

    // each sub-stage maps a window of the master clock to 0..1, eased
    fun stage(startMs: Int, durMs: Int): Float =
        ((tMs - startMs) / durMs).coerceIn(0f, 1f).let { EaseOutCubic.transform(it) }

    val bodyP   = stage(  0, 1200)   // pouch body draws
    val gatherP = stage(100, 1000)   // gather at neck
    val cordP   = stage(550, 700)    // drawstring rises
    val knotP   = stage(1150, 350)   // knot drops + spring
    val detailP = stage(1450, 350)   // folds + stitches fade in
    val wordP   = stage(1750, 500)   // word + tagline rise
    val fadeOut = ((tMs - 3000f) / 400f).coerceIn(0f, 1f) // last 400ms fade

    // gentle breath kicks in once the mark is fully drawn
    val breath = 1f + sin(((tMs - 1900) / 1800.0) * 2 * PI).toFloat() * 0.012f * (1f - fadeOut)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Night)
            .alpha(1f - fadeOut),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ───── the pouch, drawn live by Canvas + PathMeasure ─────
            Canvas(
                modifier = Modifier
                    .size(width = 200.dp, height = 250.dp)
                    .scale(breath)
            ) {
                // viewBox 64×80 fitted into the canvas
                val s = minOf(size.width / 64f, size.height / 80f)
                val ox = (size.width  - 64f * s) / 2f
                val oy = (size.height - 80f * s) / 2f
                fun p(x: Float, y: Float) = Offset(x * s + ox, y * s + oy)

                // ---- Body path (the main silhouette) ----
                val body = Path().apply {
                    val a = p(22f, 22f)
                    moveTo(a.x, a.y)
                    cubicTo(p(11f,28f).x,p(11f,28f).y, p(8f,42f).x,p(8f,42f).y, p(14f,56f).x,p(14f,56f).y)
                    cubicTo(p(20f,66f).x,p(20f,66f).y, p(38f,70f).x,p(38f,70f).y, p(48f,62f).x,p(48f,62f).y)
                    cubicTo(p(56f,55f).x,p(56f,55f).y, p(56f,38f).x,p(56f,38f).y, p(50f,28f).x,p(50f,28f).y)
                    cubicTo(p(47f,24f).x,p(47f,24f).y, p(44f,22f).x,p(44f,22f).y, p(42f,22f).x,p(42f,22f).y)
                }
                drawPartialPath(body, bodyP, Cream, strokeWidth = 3.4f * s)

                // ---- Gather (cloth scrunched at the neck) ----
                val gather = Path().apply {
                    val a = p(22f, 22f); moveTo(a.x, a.y)
                    quadraticBezierTo(p(26f,17f).x, p(26f,17f).y, p(29f,17f).x, p(29f,17f).y)
                    quadraticBezierTo(p(32f,17f).x, p(32f,17f).y, p(35f,18f).x, p(35f,18f).y)
                    quadraticBezierTo(p(38f,17f).x, p(38f,17f).y, p(42f,22f).x, p(42f,22f).y)
                }
                drawPartialPath(gather, gatherP, Cream, strokeWidth = 2.6f * s)

                // ---- Drawstring (cord rising up to the knot) ----
                val cord = Path().apply {
                    val a = p(28.6f, 18f); moveTo(a.x, a.y)
                    quadraticBezierTo(p(27f,10f).x, p(27f,10f).y, p(30.5f,6f).x, p(30.5f,6f).y)
                    quadraticBezierTo(p(34f,8f).x,  p(34f,8f).y,  p(35.6f,18f).x, p(35.6f,18f).y)
                }
                drawPartialPath(cord, cordP, Cream, strokeWidth = 2.2f * s)

                // ---- Knot drops from above and settles ----
                if (knotP > 0f) {
                    val dropY = 9f + (1f - knotP) * (-14f)   // starts above (y=-5), lands at y=9
                    drawCircle(
                        color = Cream,
                        radius = 2.4f * s,
                        center = p(32f, dropY),
                        alpha = (knotP * 1.4f).coerceAtMost(1f)
                    )
                }

                // ---- Subtle cloth folds + two stitch dots fade in ----
                if (detailP > 0f) {
                    val leftFold = Path().apply {
                        val a = p(22f, 32f); moveTo(a.x, a.y)
                        quadraticBezierTo(p(22.5f,44f).x, p(22.5f,44f).y, p(25f,58f).x, p(25f,58f).y)
                    }
                    val rightFold = Path().apply {
                        val a = p(42f, 30f); moveTo(a.x, a.y)
                        quadraticBezierTo(p(44f,44f).x, p(44f,44f).y, p(41f,58f).x, p(41f,58f).y)
                    }
                    drawPath(
                        leftFold, Cream,
                        alpha = detailP * 0.45f,
                        style = Stroke(width = 1.0f * s, cap = StrokeCap.Round)
                    )
                    drawPath(
                        rightFold, Cream,
                        alpha = detailP * 0.45f,
                        style = Stroke(width = 1.0f * s, cap = StrokeCap.Round)
                    )
                    drawCircle(Cream, radius = 0.8f * s, center = p(25.5f, 20.5f), alpha = detailP * 0.75f)
                    drawCircle(Cream, radius = 0.8f * s, center = p(38.5f, 20.5f), alpha = detailP * 0.75f)
                }
            }

            // ───── Wordmark زاد rises last ─────
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .alpha(wordP)
            ) {
                androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "زاد",
                        color = Cream,
                        style = TextStyle(
                            fontFamily = Ruqaa,
                            fontWeight = FontWeight.Bold,
                            fontSize = 64.sp,
                            lineHeight = 72.sp
                        )
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .width((48 * wordP).dp.coerceAtLeast(0.dp))
                            .height(1.dp)
                            .background(Brass)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "سعراتك، حبّةً حبّة",
                        color = Cream.copy(alpha = 0.65f),
                        style = TextStyle(
                            fontFamily = Ruqaa,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

/** Draw the first `fraction` of a path — the pen-stroke effect Sahrah uses. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPartialPath(
    path: Path,
    fraction: Float,
    color: androidx.compose.ui.graphics.Color,
    strokeWidth: Float
) {
    if (fraction <= 0f) return
    val measure = PathMeasure().apply { setPath(path, false) }
    val len = measure.length
    if (len <= 0f) return
    val piece = Path()
    measure.getSegment(0f, len * fraction, piece, true)
    drawPath(
        path = piece,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}
