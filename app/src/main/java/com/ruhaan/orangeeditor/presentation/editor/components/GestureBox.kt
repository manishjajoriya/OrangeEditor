package com.ruhaan.orangeeditor.presentation.editor.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.ruhaan.orangeeditor.Constant.LAYER_ROTATION_SNAP_THRESHOLD
import com.ruhaan.orangeeditor.domain.model.format.AlignmentConstants
import com.ruhaan.orangeeditor.domain.model.layer.EditorState
import com.ruhaan.orangeeditor.domain.model.layer.ImageLayer
import com.ruhaan.orangeeditor.domain.model.layer.Layer
import com.ruhaan.orangeeditor.domain.model.layer.LayerBounds
import com.ruhaan.orangeeditor.domain.model.layer.TextLayer
import com.ruhaan.orangeeditor.domain.model.layer.Transform
import com.ruhaan.orangeeditor.domain.model.layer.isIntersectWithMinTarget
import com.ruhaan.orangeeditor.util.snapToGuides
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GestureBox(
    width: Dp,
    height: Dp,
    canvasWidthPx: Float,
    canvasHeightPx: Float,
    state: EditorState,
    onUpdateLayer: (Layer) -> Unit,
    onTapped: () -> Unit,
    onTextLayerEdit: (TextLayer) -> Unit,
    onDoubleTap: () -> Unit,
    onLayerTapped: (String?) -> Unit,
    onDragStateChange: (Boolean) -> Unit,
    onLayerBoundsChange: (LayerBounds?) -> Unit,
) {
  val currentState by rememberUpdatedState(state)
  var activeLayerId by remember { mutableStateOf<String?>(null) }
  val density = LocalDensity.current

  // Calculate layer bounds (in px) from currentLayer
  val layerBounds by
      remember(activeLayerId) {
        derivedStateOf {
          val layerId = activeLayerId ?: return@derivedStateOf null
          val localActionLayer =
              currentState.layers.firstOrNull { it.id == layerId } ?: return@derivedStateOf null

          val bitmap = localActionLayer.bitmap ?: return@derivedStateOf null
          val w = bitmap.width * localActionLayer.transform.scale
          val h = bitmap.height * localActionLayer.transform.scale
          LayerBounds(
              centerX = localActionLayer.transform.x,
              centerY = localActionLayer.transform.y,
              width = w,
              height = h,
          )
        }
      }

  // Notify parent about bounds
  LaunchedEffect(layerBounds) { onLayerBoundsChange(layerBounds) }

  Box(
      modifier =
          Modifier.size(width, height)
              .pointerInput(state.layers) {
                detectTapGestures(
                    onTap = { offset ->
                      val tappedLayer =
                          detectTappedLayer(
                              layers = state.layers,
                              tapX = offset.x,
                              tapY = offset.y,
                              density = density,
                          )

                      if (tappedLayer != null) onLayerTapped(tappedLayer.id) else onTapped()
                    },
                    onDoubleTap = { offset ->
                      val tappedLayer =
                          detectTappedLayer(
                              layers = state.layers,
                              tapX = offset.x,
                              tapY = offset.y,
                              density = density,
                          )

                      when (tappedLayer) {
                        is TextLayer -> {
                          onTextLayerEdit(tappedLayer)
                          onLayerTapped(tappedLayer.id)
                        }

                        else -> onDoubleTap()
                      }
                    },
                )
              }
              .pointerInput(Unit) {
                detectTransformGesturesWithEnd(
                    onGestureStart = { centroid ->
                      onDragStateChange(true)
                      val tappedLayer =
                          detectTappedLayer(
                              layers = currentState.layers,
                              tapX = centroid.x,
                              tapY = centroid.y,
                              density = density,
                          )

                      onLayerTapped(tappedLayer?.id)
                      activeLayerId = tappedLayer?.id
                    },
                    onGesture = { _, pan, zoom, rotation ->
                      val layerId = activeLayerId ?: return@detectTransformGesturesWithEnd

                      val localActionLayer =
                          currentState.layers.firstOrNull { it.id == layerId }
                              ?: return@detectTransformGesturesWithEnd

                      val newX = localActionLayer.transform.x + pan.x
                      val newY = localActionLayer.transform.y + pan.y
                      val newScale = (localActionLayer.transform.scale * zoom).coerceIn(0.1f, 2f)
                      val newRotation = localActionLayer.transform.rotation + rotation

                      val snapX =
                          snapToGuides(
                              value = newX,
                              canvasWidth = canvasWidthPx,
                              canvasHeight = canvasHeightPx,
                              layerWidth = layerBounds?.width ?: 0f,
                              layerHeight = layerBounds?.height ?: 0f,
                              threshold = AlignmentConstants.ALIGNMENT_THRESHOLD_PX,
                          )
                      val snapY =
                          snapToGuides(
                              value = newY,
                              canvasWidth = canvasWidthPx,
                              canvasHeight = canvasHeightPx,
                              layerWidth = layerBounds?.width ?: 0f,
                              layerHeight = layerBounds?.height ?: 0f,
                              threshold = AlignmentConstants.ALIGNMENT_THRESHOLD_PX,
                          )

                      val newTransform =
                          Transform(
                              x = snapX,
                              y = snapY,
                              scale = newScale,
                              rotation = newRotation,
                          )

                      val updated =
                          when (localActionLayer) {
                            is ImageLayer -> localActionLayer.copy(transform = newTransform)
                            is TextLayer -> localActionLayer.copy(transform = newTransform)
                          }

                      onUpdateLayer(updated)
                    },
                    onGestureEnd = {
                      onDragStateChange(false) // NOW THIS GETS CALLED

                      val layerId = activeLayerId ?: return@detectTransformGesturesWithEnd

                      val localActionLayer =
                          currentState.layers.firstOrNull { it.id == layerId }
                              ?: return@detectTransformGesturesWithEnd

                      val snappedRotation = snapToNearest90(localActionLayer.transform.rotation)

                      val snappedTransform =
                          localActionLayer.transform.copy(rotation = snappedRotation)

                      val updated =
                          when (localActionLayer) {
                            is ImageLayer -> localActionLayer.copy(transform = snappedTransform)
                            is TextLayer -> localActionLayer.copy(transform = snappedTransform)
                          }

                      onUpdateLayer(updated)
                    },
                )
              }
  )
}

fun detectTappedLayer(layers: List<Layer>, tapX: Float, tapY: Float, density: Density): Layer? =
    layers
        .asSequence()
        .filter { it.visible }
        .sortedByDescending { it.zIndex } // top-most first
        .firstOrNull { layer ->
          layer.isIntersectWithMinTarget(tapX = tapX, tapY = tapY, density = density)
        }

fun snapToNearest90(angle: Float, threshold: Float = LAYER_ROTATION_SNAP_THRESHOLD): Float {
  val snapped = (angle / 90f).roundToInt() * 90f
  return if (abs(angle - snapped) <= threshold) snapped else angle
}
