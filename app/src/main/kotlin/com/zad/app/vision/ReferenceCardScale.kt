package com.zad.app.vision

import androidx.compose.ui.geometry.Offset
import kotlin.math.hypot

/**
 * ISO/IEC 7810 ID-1 card dimensions (credit card, driver's license, national ID).
 * Used as the size reference the user places next to their plate.
 */
object ReferenceCard {
    const val WIDTH_MM = 85.60
    const val HEIGHT_MM = 53.98
}

/**
 * Compute pixel-to-mm scale from 4 user-tapped corners of a reference card.
 *
 * Order-independent — we take both pairs of opposite corners (longest pair
 * of opposing points is the diagonal), average their pixel lengths, and divide
 * by the known card diagonal (sqrt(W² + H²) ≈ 101.21 mm).
 *
 * @return mm per pixel, or null if fewer than 4 points were provided.
 */
fun computeScaleMmPerPx(corners: List<Offset>): Double? {
    if (corners.size != 4) return null
    val cardDiagonalMm = hypot(ReferenceCard.WIDTH_MM, ReferenceCard.HEIGHT_MM)
    val pixelDiagonals = listOf(
        dist(corners[0], corners[2]),
        dist(corners[1], corners[3])
    ).filter { it > 0.0 }
    if (pixelDiagonals.isEmpty()) return null
    val avgPx = pixelDiagonals.average()
    return cardDiagonalMm / avgPx
}

private fun dist(a: Offset, b: Offset): Double =
    hypot((a.x - b.x).toDouble(), (a.y - b.y).toDouble())
