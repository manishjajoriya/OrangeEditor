package com.ruhaan.orangeeditor.presentation.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.kavi.droid.color.picker.ui.pickers.GridColorPicker
import com.ruhaan.orangeeditor.R
import com.ruhaan.orangeeditor.domain.model.format.AlignmentConstants
import com.ruhaan.orangeeditor.domain.model.format.CanvasFormat
import com.ruhaan.orangeeditor.domain.model.layer.EditorState
import com.ruhaan.orangeeditor.domain.model.layer.Layer
import com.ruhaan.orangeeditor.domain.model.layer.LayerBounds
import com.ruhaan.orangeeditor.domain.model.layer.TextLayer
import com.ruhaan.orangeeditor.presentation.theme.CanvasOrange
import com.ruhaan.orangeeditor.util.EditorRenderer
import kotlin.math.abs
import kotlin.math.min
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditorCanvas(
    modifier: Modifier = Modifier,
    canvasFormat: CanvasFormat,
    state: EditorState,
    onCanvasSize: (IntSize) -> Unit,
    onLayerTapped: (String?) -> Unit,
    onUpdateLayer: (Layer) -> Unit,
    onUpdateSelectedTextLayer:
        (
            text: String,
            fontColor: Color,
            fontWeight: FontWeight,
            fontStyle: FontStyle,
        ) -> Unit,
) {
  // Local State
  val renderer = remember { EditorRenderer() }
  var isDragging by remember { mutableStateOf(false) }
  var layerBounds by remember { mutableStateOf<LayerBounds?>(null) }
  var editingLayer by remember { mutableStateOf<TextLayer?>(null) }
  var editingText by remember { mutableStateOf(TextFieldValue("")) }
  var isBold by remember { mutableStateOf(false) }
  var isItalic by remember { mutableStateOf(false) }
  var showColorSheet by remember { mutableStateOf(false) }
  var selectedColor by remember { mutableStateOf(Color.Black) }
  val textFieldFocusRequester = remember { FocusRequester() }
  val focusManager = LocalFocusManager.current

  val isKeyboardOpen = isKeyboardOpen()

  LaunchedEffect(editingLayer) {
    if (editingLayer == null) return@LaunchedEffect

    snapshotFlow {
          listOf(editingText.text, isBold.toString(), isItalic.toString(), selectedColor.toString())
        }
        .collectLatest { _ ->
          onUpdateSelectedTextLayer(
              editingText.text,
              selectedColor,
              if (isBold) FontWeight.Bold else FontWeight.Normal,
              if (isItalic) FontStyle.Italic else FontStyle.Normal,
          )
        }
  }

  // Open keyboard when editingText is non null
  LaunchedEffect(editingLayer) {
    if (editingLayer != null) {
      textFieldFocusRequester.requestFocus()
    }
  }

  // Clear layerBounds when dragging ends
  LaunchedEffect(isDragging) {
    if (!isDragging) {
      layerBounds = null
    }
  }

  // Clear layerBounds when selected layer is removed
  LaunchedEffect(state.selectedLayerId, state.layers) {
    val selectedLayerExists =
        state.selectedLayerId?.let { id -> state.layers.any { it.id == id } } ?: false

    if (!selectedLayerExists) {
      layerBounds = null
    }
  }

  fun reset() {
    editingLayer = null
    showColorSheet = false
    onLayerTapped(null)
  }

  // when keyboard is back then remove set editing layer to null so text filed disappear
  LaunchedEffect(isKeyboardOpen) {
    if (!isKeyboardOpen) {
      reset()
    }
  }

  Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
      val aspectRatio = canvasFormat.width.toFloat() / canvasFormat.height.toFloat()
      val maxWidth = maxWidth
      val maxHeight = maxHeight

      val (canvasWidth, canvasHeight) =
          if (aspectRatio >= 1f) {
            val width = min(maxWidth.value, maxHeight.value * aspectRatio).dp
            val height = width / aspectRatio
            width to height
          } else {
            val height = min(maxHeight.value, maxWidth.value / aspectRatio).dp
            val width = height * aspectRatio
            width to height
          }

      Box(
          modifier =
              Modifier.border(width = 2.dp, color = Color.Gray.copy(alpha = 0.4f))
                  .size(width = canvasWidth, height = canvasHeight)
                  .onSizeChanged { onCanvasSize(it) },
          contentAlignment = Alignment.Center,
      ) {
        Surface(shadowElevation = 2.dp, color = Color.White) {
          Box(modifier = Modifier.size(canvasWidth, canvasHeight)) {
            // Alignment guides overlay (also draws the image/text)
            Canvas(modifier = Modifier.size(canvasWidth, canvasHeight)) {
              drawIntoCanvas {
                renderer.draw(
                    it.nativeCanvas,
                    state.layers,
                    selectedLayerId = state.selectedLayerId,
                )
              }

              val localLayerBounds = layerBounds

              if (
                  state.selectedLayerId != null &&
                      isDragging &&
                      localLayerBounds != null &&
                      state.canvasSize != IntSize.Zero
              ) {
                val canvasWidthPx = state.canvasSize.width.toFloat()
                val canvasHeightPx = state.canvasSize.height.toFloat()

                val centerX = canvasWidthPx / 2f
                val centerY = canvasHeightPx / 2f

                val left = 0f
                val top = 0f

                val layerLeft = localLayerBounds.centerX - localLayerBounds.width / 2f
                val layerRight = localLayerBounds.centerX + localLayerBounds.width / 2f
                val layerTop = localLayerBounds.centerY - localLayerBounds.height / 2f

                val threshold = AlignmentConstants.ALIGNMENT_THRESHOLD_PX
                val color = Color(AlignmentConstants.GUIDE_LINE_COLOR)
                val strokeWidth = AlignmentConstants.GUIDE_LINE_WIDTH_PX

                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f)

                // Vertical center line
                if (abs(localLayerBounds.centerX - centerX) <= threshold) {
                  drawLine(
                      color = color,
                      start = Offset(centerX, 0f),
                      end = Offset(centerX, canvasHeightPx),
                      strokeWidth = strokeWidth,
                      pathEffect = pathEffect,
                  )
                }

                // Horizontal center line
                if (abs(localLayerBounds.centerY - centerY) <= threshold) {
                  drawLine(
                      color = color,
                      start = Offset(0f, centerY),
                      end = Offset(canvasWidthPx, centerY),
                      strokeWidth = strokeWidth,
                      pathEffect = pathEffect,
                  )
                }

                // Left edge line
                if (abs(layerLeft - left) <= threshold) {
                  drawLine(
                      color = color,
                      start = Offset(left, 0f),
                      end = Offset(left, canvasHeightPx),
                      strokeWidth = strokeWidth,
                      pathEffect = pathEffect,
                  )
                }

                // Right edge line
                if (abs(layerRight - canvasWidthPx) <= threshold) {
                  drawLine(
                      color = color,
                      start = Offset(canvasWidthPx, 0f),
                      end = Offset(canvasWidthPx, canvasHeightPx),
                      strokeWidth = strokeWidth,
                      pathEffect = pathEffect,
                  )
                }

                // Top edge line
                if (abs(layerTop - top) <= threshold) {
                  drawLine(
                      color = color,
                      start = Offset(0f, top),
                      end = Offset(canvasWidthPx, top),
                      strokeWidth = strokeWidth,
                      pathEffect = pathEffect,
                  )
                }
              }
            }

            // Gesture
            GestureBox(
                width = canvasWidth,
                height = canvasHeight,
                canvasWidthPx = with(LocalDensity.current) { canvasWidth.toPx() },
                canvasHeightPx = with(LocalDensity.current) { canvasHeight.toPx() },
                state = state,
                onTapped = { reset() },
                onLayerTapped = onLayerTapped,
                onUpdateLayer = onUpdateLayer,
                onDragStateChange = { dragging -> isDragging = dragging },
                onLayerBoundsChange = { layerBounds = it },
                onDoubleTap = { reset() },
                onTextLayerEdit = { textLayer ->
                  editingLayer = textLayer

                  editingText =
                      TextFieldValue(
                          text = textLayer.text,
                          selection = TextRange(textLayer.text.length),
                      )

                  selectedColor = textLayer.color
                  isBold = textLayer.fontWeight == FontWeight.Bold
                  isItalic = textLayer.fontStyle == FontStyle.Italic
                },
            )
          }
        }
      }
    }
    editingLayer?.let {
      Column(
          modifier = Modifier.fillMaxSize().imePadding(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Bottom,
      ) {
        if (showColorSheet) {
          GridColorPicker(
              modifier = modifier,
              lastSelectedColor = selectedColor,
              onColorSelected = { selectedColor = it },
          )
        }
        Row(
            modifier =
                Modifier.border(2.dp, Color.Gray.copy(alpha = .5f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        ) {
          TextField(
              value = editingText,
              onValueChange = { if (it.text.isNotBlank()) editingText = it },
              modifier =
                  Modifier.weight(1f)
                      .height(56.dp) // ✅ fixed height
                      .padding(horizontal = 8.dp)
                      .focusRequester(textFieldFocusRequester),
              singleLine = true,
              shape = RoundedCornerShape(16.dp),
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
              keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) }),
          )

          IconButton(
              modifier = Modifier.size(48.dp), // optional: consistent size
              onClick = { isBold = !isBold },
              shape = RoundedCornerShape(8.dp),
              colors =
                  IconButtonDefaults.iconButtonColors(
                      containerColor = if (isBold) CanvasOrange else Color.White
                  ),
          ) {
            Icon(painter = painterResource(R.drawable.ic_bold), contentDescription = "bold")
          }

          IconButton(
              modifier = Modifier.size(48.dp),
              onClick = { isItalic = !isItalic },
              shape = RoundedCornerShape(8.dp),
              colors =
                  IconButtonDefaults.iconButtonColors(
                      containerColor = if (isItalic) CanvasOrange else Color.White
                  ),
          ) {
            Icon(painter = painterResource(R.drawable.ic_italic), contentDescription = "italic")
          }

          IconButton(
              modifier = Modifier.size(48.dp),
              onClick = { showColorSheet = !showColorSheet },
              shape = RoundedCornerShape(8.dp),
              colors =
                  IconButtonDefaults.iconButtonColors(
                      containerColor = if (showColorSheet) CanvasOrange else Color.White
                  ),
          ) {
            Icon(
                painter = painterResource(R.drawable.ic_color_circle),
                contentDescription = "color",
            )
          }
        }
      }
    }
  }
}

@Composable
fun isKeyboardOpen(): Boolean {
  val imeInsets = WindowInsets.ime
  return imeInsets.getBottom(LocalDensity.current) > 0
}
