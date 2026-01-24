package com.ruhaan.orangeeditor.presentation.editor

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.orangeeditor.domain.model.format.CanvasFormat
import com.ruhaan.orangeeditor.domain.model.layer.Adjustment
import com.ruhaan.orangeeditor.domain.model.layer.EditorState
import com.ruhaan.orangeeditor.domain.model.layer.ExportResult
import com.ruhaan.orangeeditor.domain.model.layer.ImageFilter
import com.ruhaan.orangeeditor.domain.model.layer.ImageLayer
import com.ruhaan.orangeeditor.domain.model.layer.Layer
import com.ruhaan.orangeeditor.domain.model.layer.NeutralAdjustment
import com.ruhaan.orangeeditor.domain.model.layer.TextLayer
import com.ruhaan.orangeeditor.domain.model.layer.Transform
import com.ruhaan.orangeeditor.domain.repository.OrangeRepository
import com.ruhaan.orangeeditor.util.BottomBarMode
import com.ruhaan.orangeeditor.util.EditorRenderer
import com.ruhaan.orangeeditor.util.Storage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditorViewModel
@Inject
constructor(
    private val orangeRepository: OrangeRepository,
    private val storage: Storage,
    private val editorRenderer: EditorRenderer,
) : ViewModel() {

  private val _editorState = MutableStateFlow(EditorState())
  val editorState = _editorState.asStateFlow()

  val allDraft =
      orangeRepository
          .getAllEditorState()
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5000),
              initialValue = emptyList(),
          )

  private val undoStack = mutableListOf<List<Layer>>()
  private val redoStack = mutableListOf<List<Layer>>()
  private val maxHistorySize = 20

  private var nextTextId = 1
  private var nextImageId = 1

  private val _exportResult = MutableStateFlow<ExportResult>(ExportResult.Idle)
  val exportResult = _exportResult.asStateFlow()

  private val _showDeleteIconForLayer = mutableStateOf<String?>(null)
  val showDeleteIconForLayer: State<String?> = _showDeleteIconForLayer

  fun resetState() {
    _editorState.update { it.copy(layers = emptyList(), selectedLayerId = null) }
  }

  fun addLayer(layer: Layer) {
    saveSnapshot()
    _editorState.update { it.copy(layers = it.layers + layer, selectedLayerId = layer.id) }
  }

  fun selectedLayer(id: String?) {
    _editorState.update { state -> state.copy(selectedLayerId = id) }
  }

  fun addImageLayer(
      bitmap: Bitmap,
      imageFilter: ImageFilter = ImageFilter.NO_FILTER,
      canvasWidthInPx: Float,
      canvasHeightInPx: Float,
  ) {
    val scale =
        minOf(
            canvasWidthInPx / bitmap.width,
            canvasHeightInPx / bitmap.height,
            1f,
        ) * 0.9f

    // to place image at center of canvas when user import images.
    val x = canvasWidthInPx / 2f
    val y = canvasHeightInPx / 2f

    val layer =
        ImageLayer(
            id = UUID.randomUUID().toString(),
            displayName = "Image ${nextImageId++}",
            bitmap = bitmap,
            imageFilter = imageFilter,
            adjustment = NeutralAdjustment,
            transform = Transform(x = x, y = y, scale = scale, rotation = 0f),
            zIndex = (_editorState.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1,
            originalWidth = bitmap.width,
            originalHeight = bitmap.height,
        )

    addLayer(layer = layer)
  }

  fun addTextLayer(
      text: String,
      color: Color,
      fontWeight: FontWeight,
      fontStyle: FontStyle,
      canvasWidthInPx: Float,
      canvasHeightInPx: Float,
  ) {
    val x = canvasWidthInPx / 2f
    val y = canvasHeightInPx / 2f

    val bitmapCreated =
        editorRenderer.textLayerToBitmap(
            text = text,
            color = color,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
        )

    val layer =
        TextLayer(
            id = UUID.randomUUID().toString(),
            displayName = "Text ${nextTextId++}",
            text = text,
            bitmap = bitmapCreated,
            color = color,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            transform =
                Transform(
                    x = x,
                    y = y,
                    scale = 1f,
                    rotation = 0f,
                ),
            zIndex = (_editorState.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1,
            visible = true,
        )

    addLayer(layer)
  }

  fun getSelectedLayer(): Layer? {
    return _editorState.value.layers.firstOrNull { it.id == _editorState.value.selectedLayerId }
  }

  fun getSelectedImagerLayer(): ImageLayer? {
    val currentSelectedLayer = getSelectedLayer()
    if (currentSelectedLayer is ImageLayer) return currentSelectedLayer
    return null
  }

  fun getSelectedTextLayer(): TextLayer? {
    val currentSelectedLayer = getSelectedLayer()
    if (currentSelectedLayer is TextLayer) return currentSelectedLayer
    return null
  }

  fun updateLayer(updatedLayer: Layer) {
    _editorState.update { state ->
      state.copy(
          layers =
              state.layers.map { layer -> if (layer.id == updatedLayer.id) updatedLayer else layer }
      )
    }
  }

  fun updateBitmapOfSelectedImageLayer(updatedBitmap: Bitmap) {
    val selectedLayer = getSelectedLayer()
    val selectedImageLayer = selectedLayer as? ImageLayer
    selectedImageLayer?.let {
      saveSnapshot()
      updateLayer(updatedLayer = it.copy(bitmap = updatedBitmap))
    }
  }

  fun updateImageFilterOfSelectedImagerLayer(imageFilter: ImageFilter) {
    val selectedLayer = getSelectedLayer()
    val selectedImageLayer = selectedLayer as? ImageLayer
    selectedImageLayer?.let {
      saveSnapshot()
      updateLayer(updatedLayer = it.copy(imageFilter = imageFilter, adjustment = NeutralAdjustment))
    }
  }

  fun updateAdjustmentsOfSelectedImageLayer(adjustment: Adjustment) {
    val selectedLayer = getSelectedLayer()
    val selectedImageLayer = selectedLayer as? ImageLayer
    selectedImageLayer?.let {
      updateLayer(
          updatedLayer = it.copy(adjustment = adjustment, imageFilter = ImageFilter.NO_FILTER)
      )
    }
  }

  fun updateSelectedTextLayer(
      text: String,
      fontColor: Color,
      fontWeight: FontWeight,
      fontStyle: FontStyle,
  ) {
    val selectedTextLayer = getSelectedTextLayer() ?: return

    saveSnapshot()

    val newBitmap = editorRenderer.textLayerToBitmap(text, fontColor, fontWeight, fontStyle)

    val updatedLayer =
        selectedTextLayer.copy(
            text = text,
            bitmap = newBitmap,
            color = fontColor,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
        )

    updateLayer(updatedLayer)
  }

  fun removeLayer(id: String) {
    saveSnapshot()
    _editorState.update { state ->
      val remainingLayers = state.layers.filterNot { it.id == id }

      // Auto-select the new top layer (last in list)
      val newSelectedId = remainingLayers.lastOrNull()?.id

      state.copy(layers = remainingLayers, selectedLayerId = newSelectedId)
    }
  }

  fun updateFileName(newName: String) {
    val sanitized = newName.replace(Regex("[/\\\\:*?\"<>|]"), "").trim().take(20)

    val finalName = sanitized.ifEmpty { "Draft" }

    _editorState.update { it.copy(fileName = finalName) }
  }

  private fun saveSnapshot() {
    val currentLayers = _editorState.value.layers.toList()

    undoStack.add(currentLayers)

    if (undoStack.size > maxHistorySize) {
      undoStack.removeAt(0)
    }

    redoStack.clear()
  }

  fun canUndo(): Boolean {
    return undoStack.isNotEmpty()
  }

  fun canRedo(): Boolean {
    return redoStack.isNotEmpty()
  }

  fun undo() {
    if (!canUndo()) return

    val currentLayers = _editorState.value.layers.toList()
    redoStack.add(currentLayers)

    val previousLayers = undoStack.removeAt(undoStack.lastIndex)

    // Auto-select the top layer (last in list)
    val newSelectedId = previousLayers.lastOrNull()?.id

    _editorState.update { it.copy(layers = previousLayers, selectedLayerId = newSelectedId) }
  }

  fun redo() {
    if (!canRedo()) return

    val currentLayers = _editorState.value.layers.toList()
    undoStack.add(currentLayers)

    val nextLayers = redoStack.removeAt(redoStack.lastIndex)

    // Auto-select the top layer (last in list)
    val newSelectedId = nextLayers.lastOrNull()?.id

    _editorState.update { it.copy(layers = nextLayers, selectedLayerId = newSelectedId) }
  }

  private fun reassignZIndex(layers: List<Layer>): List<Layer> {
    return layers.mapIndexed { index, layer ->
      when (layer) {
        is TextLayer -> layer.copy(zIndex = index)
        is ImageLayer -> layer.copy(zIndex = index)
      }
    }
  }

  fun moveLayerUp(layerId: String) {
    val layers = _editorState.value.layers
    val index = layers.indexOfFirst { it.id == layerId }
    if (index <= 0 || index >= layers.size) return

    saveSnapshot()
    val newLayers = layers.toMutableList()
    newLayers.swap(index, index - 1)
    val reindexed = reassignZIndex(newLayers)
    val newSelectedId = reindexed.lastOrNull()?.id // ← Select the new top layer
    _editorState.update { it.copy(layers = reindexed, selectedLayerId = newSelectedId) }
  }

  fun moveLayerDown(layerId: String) {
    val layers = _editorState.value.layers
    val index = layers.indexOfFirst { it.id == layerId }
    if (index < 0 || index >= layers.size - 1) return

    saveSnapshot()
    val newLayers = layers.toMutableList()
    newLayers.swap(index, index + 1)
    val reindexed = reassignZIndex(newLayers)
    val newSelectedId = reindexed.lastOrNull()?.id // ← Select the new top layer
    _editorState.update { it.copy(layers = reindexed, selectedLayerId = newSelectedId) }
  }

  fun moveLayerToTop(layerId: String) {
    val layers = _editorState.value.layers
    val index = layers.indexOfFirst { it.id == layerId }
    if (index < 0 || index == layers.size - 1) return

    saveSnapshot()
    val newLayers = layers.toMutableList()
    val layer = newLayers.removeAt(index)
    newLayers.add(layer)
    val reindexed = reassignZIndex(newLayers)
    val newSelectedId = reindexed.lastOrNull()?.id // ← Select the new top layer
    _editorState.update { it.copy(layers = reindexed, selectedLayerId = newSelectedId) }
  }

  private fun MutableList<Layer>.swap(i: Int, j: Int) {
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
  }

  fun getBottomBarMode(): BottomBarMode {
    val selectedLayer = getSelectedLayer()
    return when (selectedLayer) {
      is TextLayer -> BottomBarMode.TextLayerSelected
      is ImageLayer -> BottomBarMode.ImageLayerSelected
      else -> BottomBarMode.Primary
    }
  }

  fun startExport(context: Context, canvasFormat: CanvasFormat, canvasScreenSize: IntSize) {
    val currentFileName = _editorState.value.fileName
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "${currentFileName}_${timestamp}.png"

    try {
      val bitmapFromLayer =
          storage.getBitmapFromLayer(
              layers = _editorState.value.layers,
              customizer = _editorState.value.customizer,
              canvasFormat = canvasFormat,
              canvasScreenSize = canvasScreenSize,
          )

      val savedUri = storage.saveBitmapToDownloads(context, bitmapFromLayer, fileName)
      bitmapFromLayer.recycle()

      if (savedUri != null) {
        _exportResult.value = ExportResult.Success(savedUri)
      }
    } catch (e: Exception) {
      e.printStackTrace()
      _exportResult.value = ExportResult.Error("Export failed: ${e.message}")
      Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
  }

  fun showDeleteIconFor(layerId: String) {
    _showDeleteIconForLayer.value = layerId
  }

  fun hideDeleteIcon() {
    _showDeleteIconForLayer.value = null
  }

  fun resetExportResult() {
    _exportResult.value = ExportResult.Idle
  }

  fun saveDraft() {
    viewModelScope.launch { orangeRepository.saveEditorState(editorState = _editorState.value) }
  }

  fun selectedDraft(editorId: String) {
    viewModelScope.launch {
      val draft = orangeRepository.getEditorStatById(editorId = editorId)
      _editorState.update { draft }
    }
  }

  fun updateCanvasSize(size: IntSize) {
    _editorState.update { it.copy(canvasSize = size) }
  }

  fun deleteSavedDraft(editorId: String) {
    viewModelScope.launch { orangeRepository.deleteEditorStateById(editorId = editorId) }
  }

  fun newEditorState(canvasFormat: CanvasFormat) {
    _editorState.value = EditorState(canvasFormat = canvasFormat)
  }
}
