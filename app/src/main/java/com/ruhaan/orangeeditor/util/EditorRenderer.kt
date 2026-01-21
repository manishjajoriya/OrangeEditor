package com.ruhaan.orangeeditor.util

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withSave
import com.ruhaan.orangeeditor.domain.model.layer.ImageFilter
import com.ruhaan.orangeeditor.domain.model.layer.ImageLayer
import com.ruhaan.orangeeditor.domain.model.layer.Layer
import com.ruhaan.orangeeditor.domain.model.layer.NeutralAdjustment
import com.ruhaan.orangeeditor.domain.model.layer.TextLayer
import com.ruhaan.orangeeditor.domain.model.layer.toColorMatrix

class EditorRenderer {

  fun draw(
      canvas: Canvas,
      layers: List<Layer>,
      scaleX: Float = 1f,
      scaleY: Float = 1f,
      selectedLayerId: String? = null,
  ) {
    layers
        .sortedBy { it.zIndex }
        .filter { it.visible }
        .forEach { layer ->
          when (layer) {
            is TextLayer -> drawBitmap(canvas, layer, scaleX, scaleY, selectedLayerId)
            is ImageLayer -> drawBitmap(canvas, layer, scaleX, scaleY, selectedLayerId)
          }
        }
  }

  fun drawBitmap(
      canvas: Canvas,
      layer: Layer,
      scaleX: Float = 1f,
      scaleY: Float = 1f,
      selectedLayerId: String? = null,
  ): Canvas {
    layer.bitmap?.let { newBitmap ->
      canvas.withSave {
        val t = layer.transform
        translate(t.x * scaleX, t.y * scaleY)
        rotate(t.rotation)
        scale(t.scale * scaleX, t.scale * scaleY)

        val (paint, shouldApplyPaint) =
            when (layer) {
              is ImageLayer -> {
                val filter = layer.imageFilter

                val isApplyCustomAdjustments = layer.adjustment != NeutralAdjustment

                val paint =
                    Paint().apply {
                      colorFilter =
                          ColorMatrixColorFilter(
                              if (isApplyCustomAdjustments) layer.adjustment.toColorMatrix()
                              else layer.imageFilter.colorMatrix
                          )
                    }

                val shouldApplyPaint = isApplyCustomAdjustments || filter != ImageFilter.NO_FILTER

                paint to shouldApplyPaint
              }

              is TextLayer -> {
                null to false
              }
            }

        drawBitmap(
            newBitmap,
            -newBitmap.width / 2f,
            -newBitmap.height / 2f,
            if (shouldApplyPaint) paint else null,
        )

        if (selectedLayerId == layer.id) {
          val t = layer.transform
          val halfW = newBitmap.width / 2f
          val halfH = newBitmap.height / 2f

          val padding = 0f // distance from content to border
          val cornerRadius = 6f / t.scale

          // 1. Outer soft glow (shadow-like)
          val glowPaint =
              Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 2f / t.scale
                color = "#0892d0".toColorInt().applyLightness(0.6f)
                isAntiAlias = true
                maskFilter = BlurMaskFilter(8f / t.scale, BlurMaskFilter.Blur.NORMAL)
              }
          drawRoundRect(
              -(halfW + padding + 2f),
              -(halfH + padding + 2f),
              +(halfW + padding + 2f),
              +(halfH + padding + 2f),
              cornerRadius,
              cornerRadius,
              glowPaint,
          )

          // 2. Main blue stroke (like Figma)
          val borderPaint =
              Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f / t.scale
                color = "#0062ff".toColorInt() // Figma blue
                isAntiAlias = true
              }
          drawRoundRect(
              -(halfW + padding),
              -(halfH + padding),
              +(halfW + padding),
              +(halfH + padding),
              cornerRadius,
              cornerRadius,
              borderPaint,
          )
        }
      }
    }
    return canvas
  }

  fun Int.applyLightness(factor: Float): Int {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this, hsv)
    hsv[2] = (hsv[2] * factor).coerceIn(0f, 1f)
    return android.graphics.Color.HSVToColor(hsv)
  }

  private fun resolveTypeface(
      fontWeight: FontWeight,
      fontStyle: FontStyle,
  ): Typeface {

    val style =
        when {
          fontWeight >= FontWeight.Bold && fontStyle == FontStyle.Italic -> Typeface.BOLD_ITALIC
          fontWeight >= FontWeight.Bold -> Typeface.BOLD
          fontStyle == FontStyle.Italic -> Typeface.ITALIC
          else -> Typeface.NORMAL
        }

    return Typeface.create(Typeface.DEFAULT, style)
  }

  fun textLayerToBitmap(
      text: String,
      color: Color,
      fontWeight: FontWeight,
      fontStyle: FontStyle,
  ): Bitmap {
    val paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
          this.color = color.toArgb()
          textSize = 120.toFloat()
          typeface = resolveTypeface(fontWeight, fontStyle)
          textAlign = Paint.Align.LEFT
        }

    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)

    val width = bounds.width().coerceAtLeast(1)
    val height = bounds.height().coerceAtLeast(1)

    val bitmap = createBitmap(width, height)

    val canvas = Canvas(bitmap)

    // IMPORTANT: align text exactly to bitmap
    canvas.drawText(text, -bounds.left.toFloat(), -bounds.top.toFloat(), paint)

    return bitmap
  }
}
