package com.zad.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Minimal line chart. Accepts a series of (timestampMs, value) points and
 * renders a smooth path on the brand palette: brass line, cream fill below.
 */
@Composable
fun LineChart(
    points: List<Pair<Long, Double>>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.secondary,
    fillColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    showDots: Boolean = true
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (points.size < 2) return@Canvas
        val xs = points.map { it.first.toDouble() }
        val ys = points.map { it.second }
        val xMin = xs.min(); val xMax = xs.max()
        val yMin = ys.min(); val yMax = ys.max()
        val xRange = (xMax - xMin).takeIf { it > 0 } ?: 1.0
        val yPad = (yMax - yMin) * 0.10
        val yLo = yMin - yPad
        val yHi = yMax + yPad
        val yRange = (yHi - yLo).takeIf { it > 0 } ?: 1.0

        val padLeft = 12f
        val padRight = 12f
        val padTop = 20f
        val padBot = 20f
        val w = size.width - padLeft - padRight
        val h = size.height - padTop - padBot

        fun px(x: Double) = padLeft + ((x - xMin) / xRange * w).toFloat()
        fun py(y: Double) = padTop + h - ((y - yLo) / yRange * h).toFloat()

        // axis hairline
        drawLine(
            color = Color(0xFF6B6056).copy(alpha = 0.18f),
            start = Offset(padLeft, padTop + h),
            end = Offset(size.width - padRight, padTop + h),
            strokeWidth = 1f
        )

        // line path
        val line = Path().apply {
            moveTo(px(xs[0]), py(ys[0]))
            for (i in 1 until points.size) lineTo(px(xs[i]), py(ys[i]))
        }
        // fill under line
        val fill = Path().apply {
            addPath(line)
            lineTo(px(xs.last()), padTop + h)
            lineTo(px(xs.first()), padTop + h)
            close()
        }
        drawPath(fill, fillColor.copy(alpha = 0.55f))
        drawPath(line, lineColor, style = Stroke(width = 3f))

        if (showDots) {
            points.forEachIndexed { i, _ ->
                drawCircle(lineColor, radius = 3.5f, center = Offset(px(xs[i]), py(ys[i])))
                drawCircle(Color.White, radius = 1.6f, center = Offset(px(xs[i]), py(ys[i])))
            }
        }
    }
}
