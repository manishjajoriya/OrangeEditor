package com.ruhaan.orangeeditor.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ruhaan.orangeeditor.domain.model.format.CanvasFormat
import java.time.OffsetDateTime

@Entity(tableName = "editor_states")
data class EditorStateEntity(
    @PrimaryKey val editorId: String,
    val canvasFormat: CanvasFormat,
    val selectedLayerId: String? = null,
    val fileName: String = "Draft",
    val canvasWidth: Int,
    val canvasHeight: Int,
    val previewUrl: String?,
    val timestamp: OffsetDateTime,
)
