package com.ruhaan.orangeeditor.presentation.editor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ruhaan.orangeeditor.domain.model.layer.ExportResult
import com.ruhaan.orangeeditor.domain.model.layer.NeutralAdjustment
import com.ruhaan.orangeeditor.presentation.editor.components.AddTextSheet
import com.ruhaan.orangeeditor.presentation.editor.components.AdjustmentsSheet
import com.ruhaan.orangeeditor.presentation.editor.components.EditorBottomBar
import com.ruhaan.orangeeditor.presentation.editor.components.EditorCanvas
import com.ruhaan.orangeeditor.presentation.editor.components.EditorTopBar
import com.ruhaan.orangeeditor.presentation.editor.components.ExportSuccessSheet
import com.ruhaan.orangeeditor.presentation.editor.components.FileNameSheet
import com.ruhaan.orangeeditor.presentation.editor.components.FilterRow
import com.ruhaan.orangeeditor.presentation.editor.components.LayerPositionSheet
import com.ruhaan.orangeeditor.presentation.navigation.Route
import com.ruhaan.orangeeditor.presentation.theme.CanvasOrange

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel,
    navController: NavHostController,
) {
  // ViewModel states
  val editorState by viewModel.editorState.collectAsState()
  val canvasFormat by remember { derivedStateOf { editorState.canvasFormat } }
  var currentSelectedImageLayer by remember { mutableStateOf(viewModel.getSelectedImagerLayer()) }
  val context = LocalContext.current
  val exportResult by viewModel.exportResult.collectAsState()

  // Local states
  var canvasSize by remember { mutableStateOf(IntSize.Zero) }
  var showAddTextSheet by remember { mutableStateOf(false) }
  var isAddingNewText by remember { mutableStateOf(true) } // true = add, false = edit
  var showImageFilters by remember { mutableStateOf(false) }
  var showAdjustmentsSheet by remember { mutableStateOf(false) }
  var showFileNameSheet by remember { mutableStateOf(false) }
  var showPositionSheet by remember { mutableStateOf(false) }
  var loadingImage by remember { mutableStateOf(false) }
  var showExportSheet by remember { mutableStateOf(false) }

  // Compute current bottom bar mode
  val bottomBarMode by
      remember(editorState.selectedLayerId) { derivedStateOf { viewModel.getBottomBarMode() } }

  LaunchedEffect(editorState) { currentSelectedImageLayer = viewModel.getSelectedImagerLayer() }

  LaunchedEffect(exportResult) {
    when (exportResult) {
      is ExportResult.Success -> {
        showExportSheet = true
      }
      is ExportResult.Error -> {
        // Already handled by Toast in startExport
      }
      ExportResult.Idle -> {
        // Nothing
      }
    }
  }

  fun shareImage(context: Context, uri: Uri) {
    val shareIntent =
        Intent(Intent.ACTION_SEND).apply {
          type = "image/png"
          putExtra(Intent.EXTRA_STREAM, uri)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    context.startActivity(Intent.createChooser(shareIntent, "Share image"))
  }

  if (showAddTextSheet)
      AddTextSheet(
          onDismissRequest = { showAddTextSheet = false },
          isNew = isAddingNewText,
          onTextAdd = { text, fontColor, fontWeight, fontStyle ->
            viewModel.addTextLayer(
                text = text,
                color = fontColor,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                canvasWidthInPx = canvasSize.width.toFloat(),
                canvasHeightInPx = canvasSize.height.toFloat(),
            )
          },
      )

  if (showAdjustmentsSheet) {
    AdjustmentsSheet(
        onDismissRequest = { showAdjustmentsSheet = false },
        adjustment = currentSelectedImageLayer?.adjustment ?: NeutralAdjustment,
        onAdjustmentsConfirm = viewModel::updateAdjustmentsOfSelectedImageLayer,
    )
  }

  if (showFileNameSheet) {
    FileNameSheet(
        currentFileName = editorState.fileName,
        onDismissRequest = { showFileNameSheet = false },
        onSave = viewModel::updateFileName,
    )
  }

  if (showPositionSheet) {
    LayerPositionSheet(
        layers = editorState.layers,
        selectedLayerId = editorState.selectedLayerId,
        onLayerSelected = { layerId -> viewModel.moveLayerToTop(layerId) },
        onMoveUp = viewModel::moveLayerUp,
        onMoveDown = viewModel::moveLayerDown,
        onDismissRequest = { showPositionSheet = false },
    )
  }
  if (showExportSheet) {
    ExportSuccessSheet(
        onDismissRequest = {
          showExportSheet = false
          // Reset exportResult when sheet is dismissed
          viewModel.resetExportResult()
        },
        onShare = {
          val uri = (exportResult as? ExportResult.Success)?.uri
          if (uri != null) {
            shareImage(context, uri)
          } else {
            Toast.makeText(
                    context,
                    "Could not share: file saved but URI unavailable",
                    Toast.LENGTH_LONG,
                )
                .show()
          }
        },
    )
  }

  Scaffold(
      topBar = {
        Box(modifier = Modifier.background(color = CanvasOrange)) {
          EditorTopBar(
              modifier = Modifier.fillMaxWidth().statusBarsPadding(),
              fileName = editorState.fileName,
              canUndo = viewModel.canUndo(),
              canRedo = viewModel.canRedo(),
              canDelete = editorState.selectedLayerId != null,
              onBackClick = {
                viewModel.saveDraft()
                viewModel.resetState()
                navController.popBackStack()
              },
              onFileNameClick = { showFileNameSheet = true },
              onUndoClick = { viewModel.undo() },
              onRedoClick = { viewModel.redo() },
              onDeleteClick = { editorState.selectedLayerId?.let { viewModel.removeLayer(it) } },
              onDraftClick = {
                viewModel.saveDraft()
                navController.navigate(Route.Home.route)
                navController.clearBackStack<String>()
              },
          )
        }
      },
      bottomBar = {
        Column(modifier = Modifier.background(Color.White)) {
          if (showImageFilters) {
            FilterRow(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                onClick = { imageFilter ->
                  viewModel.updateImageFilterOfSelectedImagerLayer(imageFilter)
                },
            )
          }
          HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp)
          Spacer(modifier = Modifier.height(height = 4.dp))
          EditorBottomBar(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).navigationBarsPadding(),
              mode = bottomBarMode,
              onImageImportClick = { loadedBitmap ->
                viewModel.addImageLayer(
                    bitmap = loadedBitmap,
                    canvasWidthInPx = canvasSize.width.toFloat(),
                    canvasHeightInPx = canvasSize.height.toFloat(),
                )
              },
              onAddTextClick = {
                isAddingNewText = true
                showAddTextSheet = true
              },
              onFilterClick = { showImageFilters = !showImageFilters },
              onAdjustmentsClick = { showAdjustmentsSheet = true },
              onCropClick = { navController.navigate(Route.CropScreen.route) },
              onPositionClick = { showPositionSheet = true },
              onExportClick = { viewModel.startExport(context, canvasFormat, canvasSize) },
              onImageLoading = { loadingImage = it },
          )
        }
      },
  ) { _ ->
    Box(
        modifier = modifier.fillMaxSize().background(color = Color(0xFFfaf9f6)),
        contentAlignment = Alignment.Center,
    ) {
      EditorCanvas(
          state = editorState,
          canvasFormat = canvasFormat,
          onCanvasSize = { size ->
            canvasSize = size
            viewModel.updateCanvasSize(size = size)
          },
          onUpdateLayer = viewModel::updateLayer,
          onUpdateSelectedTextLayer = viewModel::updateSelectedTextLayer,
          onLayerTapped = viewModel::selectedLayer,
          viewModel = viewModel,
      )
      if (loadingImage) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          CircularProgressIndicator()
        }
      }
    }
  }
}
