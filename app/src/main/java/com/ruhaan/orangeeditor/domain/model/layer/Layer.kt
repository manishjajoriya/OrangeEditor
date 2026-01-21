package com.ruhaan.orangeeditor.domain.model.layer

import android.graphics.Bitmap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

sealed class Layer(
    open val id: String,
    open val transform: Transform,
    open val zIndex: Int,
    open val visible: Boolean = true,
    open val bitmap: Bitmap?,
)

// Old code
// Going to remove in future
fun Layer.isIntersect(tapX: Float, tapY: Float): Boolean {
  val bmp = bitmap ?: return false
  val t = transform

  // 1️⃣ Translate into layer space
  var x = tapX - t.x
  var y = tapY - t.y

  // 2️⃣ Inverse rotation
  val rad = toRadians(-t.rotation.toDouble())
  val cos = cos(rad)
  val sin = sin(rad)

  val rx = (x * cos - y * sin).toFloat()
  val ry = (x * sin + y * cos).toFloat()

  // 3️⃣ Inverse scale
  x = rx / t.scale
  y = ry / t.scale

  // 4️⃣ Center-anchored bounds
  val halfW = bmp.width / 2f
  val halfH = bmp.height / 2f

  return x in -halfW..halfW && y in -halfH..halfH
}

fun Layer.isIntersectWithMinTarget(
    tapX: Float,
    tapY: Float,
    density: Density,
    minTouchTargetImage: Dp = 56.dp,
    minTouchTargetText: Dp = 68.dp,
    touchPaddingText: Dp = 24.dp, // single side padding
): Boolean {
  val bmp = bitmap ?: return false
  val t = transform

  val minTouchTargetImagePx = with(density) { minTouchTargetImage.toPx() }
  val minTouchTargetTextPx = with(density) { minTouchTargetText.toPx() }
  val touchPaddingTextPx = with(density) { touchPaddingText.toPx() }

  val actualWidth = bmp.width * t.scale
  val actualHeight = bmp.height * t.scale

  val effectiveWidth =
      when (this) {
        is TextLayer ->
            maxOf(
                actualWidth + (touchPaddingTextPx * 2),
                minTouchTargetTextPx,
            )
        is ImageLayer -> maxOf(actualWidth, minTouchTargetImagePx)
      }

  val effectiveHeight =
      when (this) {
        is TextLayer ->
            maxOf(
                actualHeight + (touchPaddingTextPx * 2),
                minTouchTargetTextPx,
            )
        is ImageLayer -> maxOf(actualHeight, minTouchTargetImagePx)
      }

  val x = tapX - t.x
  val y = tapY - t.y

  val rad = toRadians(-t.rotation.toDouble())
  val cos = cos(rad)
  val sin = sin(rad)

  val rx = (x * cos - y * sin).toFloat()
  val ry = (x * sin + y * cos).toFloat()

  val halfW = effectiveWidth / 2f
  val halfH = effectiveHeight / 2f

  return rx in -halfW..halfW && ry in -halfH..halfH
}
