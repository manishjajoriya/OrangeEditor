package com.ruhaan.orangeeditor.domain.model.layer

import androidx.compose.ui.unit.IntSize
import com.ruhaan.orangeeditor.domain.model.format.CanvasFormat
import java.time.OffsetDateTime
import java.util.UUID

data class EditorState(
    val editorId: String = UUID.randomUUID().toString(),
    val canvasFormat: CanvasFormat = CanvasFormat.POST,
    val layers: List<Layer> = emptyList(),
    val selectedLayerId: String? = null,
    val fileName: String = "Untitled",
    val canvasSize: IntSize = IntSize.Zero,
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
)
