package com.zad.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zad.app.R

/**
 * Two-phase calibration:
 *   1) User taps the four corners of the credit/ID card lying next to the dish.
 *   2) User drags a rectangle around the dish on the plate.
 * On done we hand both back to the caller as pixel coordinates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaleScreen(
    photoPath: String,
    onCancel: () -> Unit,
    onDone: (corners: List<Offset>, plate: Rect) -> Unit
) {
    var corners by remember { mutableStateOf(listOf<Offset>()) }
    var plateStart by remember { mutableStateOf<Offset?>(null) }
    var plateEnd by remember { mutableStateOf<Offset?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (corners.size < 4) stringResource(R.string.scale_title)
                        else "ارسم مستطيلاً حول الطبق",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
                    OutlinedButton(
                        onClick = {
                            if (corners.isNotEmpty() && plateStart == null) {
                                corners = corners.dropLast(1)
                            } else {
                                plateStart = null
                                plateEnd = null
                            }
                        }
                    ) { Text(stringResource(R.string.scale_undo)) }
                    Spacer(Modifier.weight(1f))
                    val ready = corners.size == 4 && plateStart != null && plateEnd != null
                    Button(
                        onClick = {
                            val a = plateStart!!
                            val b = plateEnd!!
                            val rect = Rect(
                                left = minOf(a.x, b.x),
                                top = minOf(a.y, b.y),
                                right = maxOf(a.x, b.x),
                                bottom = maxOf(a.y, b.y)
                            )
                            onDone(corners, rect)
                        },
                        enabled = ready
                    ) { Text(stringResource(R.string.scale_done)) }
                }
            }
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(corners.size, plateStart, plateEnd) {
                    if (corners.size < 4) {
                        detectTapGestures { tap ->
                            corners = corners + tap
                        }
                    } else {
                        detectDragGestures(
                            onDragStart = { plateStart = it; plateEnd = it },
                            onDrag = { change, _ ->
                                plateEnd = change.position
                                change.consume()
                            }
                        )
                    }
                }
        ) {
            AsyncImage(
                model = photoPath,
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Canvas(Modifier.fillMaxSize()) {
                corners.forEach { p ->
                    drawCircle(Color(0xFFC97B26), radius = 18f, center = p)
                    drawCircle(Color.White, radius = 18f, center = p, style = Stroke(width = 3f))
                }
                if (corners.size == 4) {
                    val order = orderCornersAsQuad(corners)
                    for (i in order.indices) {
                        val a = order[i]
                        val b = order[(i + 1) % order.size]
                        drawLine(Color(0xFFC97B26), a, b, strokeWidth = 4f)
                    }
                }
                val s = plateStart; val e = plateEnd
                if (s != null && e != null) {
                    drawRect(
                        color = Color(0xFF5B6B3A),
                        topLeft = Offset(minOf(s.x, e.x), minOf(s.y, e.y)),
                        size = Size((s.x - e.x).let { kotlin.math.abs(it) }, (s.y - e.y).let { kotlin.math.abs(it) }),
                        style = Stroke(width = 5f)
                    )
                }
            }

            if (corners.size < 4) {
                Text(
                    text = stringResource(R.string.scale_body),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

/** Order 4 points into a clockwise quad for drawing — based on angle from centroid. */
private fun orderCornersAsQuad(pts: List<Offset>): List<Offset> {
    val cx = pts.map { it.x }.average().toFloat()
    val cy = pts.map { it.y }.average().toFloat()
    return pts.sortedBy { kotlin.math.atan2((it.y - cy).toDouble(), (it.x - cx).toDouble()) }
}
