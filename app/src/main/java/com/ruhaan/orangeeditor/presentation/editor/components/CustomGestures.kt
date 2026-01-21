package com.ruhaan.orangeeditor.presentation.editor.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlin.math.PI
import kotlin.math.abs

suspend fun PointerInputScope.detectTransformGesturesWithEnd(
    panZoomLock: Boolean = false,
    /*
    zoomSensitivity is between 0 to 1.
    0 being slow and 1 being fast.
     */
    zoomSensitivity: Float = 0.7f,
    onGestureStart: (tappedPosition: Offset) -> Unit = {},
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit,
    onGestureEnd: () -> Unit = {},
) {
  awaitEachGesture {
    var rotation = 0f
    var zoom = 1f
    var pan = Offset.Zero
    var pastTouchSlop = false
    val touchSlop = viewConfiguration.touchSlop
    var lockedToPanZoom = false

    val downEvent = awaitFirstDown(requireUnconsumed = false)
    val tappedPosition = downEvent.position

    onGestureStart(tappedPosition)

    do {
      val event = awaitPointerEvent()
      val canceled = event.changes.fastAny { it.isConsumed }
      if (!canceled) {
        val rawZoomChange = event.calculateZoom()
        val rotationChange = event.calculateRotation()
        val panChange = event.calculatePan()

        // Apply zoom sensitivity damping
        val zoomChange = 1f + (rawZoomChange - 1f) * zoomSensitivity

        if (!pastTouchSlop) {
          val hasMultiplePointers = event.changes.size > 1

          // Skip touch slop for multi-touch gestures (pinch/rotate)
          if (hasMultiplePointers) {
            if (zoomChange != 1f || rotationChange != 0f) {
              pastTouchSlop = true
              lockedToPanZoom = panZoomLock && rotationChange == 0f
            }
          } else {
            // Only check touch slop for single-touch (pan) gestures
            zoom *= zoomChange
            rotation += rotationChange
            pan += panChange

            val centroidSize = event.calculateCentroidSize(useCurrent = false)
            val zoomMotion = abs(1 - zoom) * centroidSize
            val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
            val panMotion = pan.getDistance()

            if (zoomMotion > touchSlop || rotationMotion > touchSlop || panMotion > touchSlop) {
              pastTouchSlop = true
              lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
            }
          }
        }

        if (pastTouchSlop) {
          val centroid = event.calculateCentroid(useCurrent = false)
          val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
          if (effectiveRotation != 0f || zoomChange != 1f || panChange != Offset.Zero) {
            onGesture(centroid, panChange, zoomChange, effectiveRotation)
          }
          event.changes.fastForEach {
            if (it.positionChanged()) {
              it.consume()
            }
          }
        }
      }
    } while (!canceled && event.changes.fastAny { it.pressed })

    onGestureEnd()
  }
}
