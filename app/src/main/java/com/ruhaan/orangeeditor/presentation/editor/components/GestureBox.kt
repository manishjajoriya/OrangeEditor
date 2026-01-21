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
    onLayerTapped: (String) -> Unit,
    onDragStateChange: (Boolean) -> Unit,
    onLayerBoundsChange: (LayerBounds?) -> Unit,
) {
  val selectedLayerId = state.selectedLayerId
  val selectedLayer = state.layers.firstOrNull { it.id == selectedLayerId } ?: return
  val density = LocalDensity.current

  var activeLayerId by remember { mutableStateOf<String?>(null) }
  val currentLayer by rememberUpdatedState(selectedLayer)
  val currentState by rememberUpdatedState(state)

  // Calculate layer bounds (in px) from currentLayer
  val layerBounds by
      remember(currentLayer) {
        derivedStateOf {
          val bitmap = currentLayer.bitmap ?: return@derivedStateOf null
          val w = bitmap.width * currentLayer.transform.scale
          val h = bitmap.height * currentLayer.transform.scale
          LayerBounds(
              centerX = currentLayer.transform.x,
              centerY = currentLayer.transform.y,
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

                      if (tappedLayer != null) onLayerTapped(tappedLayer.id)
                      onTapped()
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
                        null -> onDoubleTap()

                        is TextLayer -> {
                          onTextLayerEdit(tappedLayer)
                          onLayerTapped(tappedLayer.id)
                        }

                        else -> {
                          onDoubleTap()
                          onLayerTapped(tappedLayer.id)
                        }
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

                      val snappedRotation = snapToNearest90(currentLayer.transform.rotation)

                      val snappedTransform = currentLayer.transform.copy(rotation = snappedRotation)

                      val updated =
                          when (val layer = currentLayer) {
                            is ImageLayer -> layer.copy(transform = snappedTransform)
                            is TextLayer -> layer.copy(transform = snappedTransform)
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
