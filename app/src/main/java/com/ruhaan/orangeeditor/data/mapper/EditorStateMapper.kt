package com.ruhaan.orangeeditor.data.mapper

import androidx.compose.ui.unit.IntSize
import com.ruhaan.orangeeditor.data.entity.EditorStateEntity
import com.ruhaan.orangeeditor.domain.model.layer.EditorState
import com.ruhaan.orangeeditor.domain.model.layer.Layer
import java.time.OffsetDateTime

fun EditorStateEntity.toDomain(layers: List<Layer>): EditorState {
  return EditorState(
      editorId = editorId,
      layers = layers,
      canvasFormat = canvasFormat,
      selectedLayerId = selectedLayerId,
      fileName = fileName,
      canvasSize = IntSize(canvasWidth, canvasHeight),
      timestamp = timestamp,
  )
}

fun EditorState.toEntity(previewUrl: String?): EditorStateEntity {
  return EditorStateEntity(
      editorId = editorId,
      canvasFormat = canvasFormat,
      selectedLayerId = selectedLayerId,
      fileName = fileName,
      previewUrl = previewUrl,
      canvasWidth = canvasSize.width,
      canvasHeight = canvasSize.height,
      timestamp = OffsetDateTime.now(),
  )
}
