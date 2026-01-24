package com.ruhaan.orangeeditor.data.repository

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import com.ruhaan.orangeeditor.data.dao.EditorStateDao
import com.ruhaan.orangeeditor.data.dao.ImageLayerDao
import com.ruhaan.orangeeditor.data.dao.TextLayerDao
import com.ruhaan.orangeeditor.data.entity.EditorStateEntity
import com.ruhaan.orangeeditor.data.mapper.toDomain
import com.ruhaan.orangeeditor.data.mapper.toEntity
import com.ruhaan.orangeeditor.data.storage.StorageType
import com.ruhaan.orangeeditor.domain.model.layer.EditorState
import com.ruhaan.orangeeditor.domain.model.layer.ImageLayer
import com.ruhaan.orangeeditor.domain.model.layer.TextLayer
import com.ruhaan.orangeeditor.domain.repository.OrangeRepository
import com.ruhaan.orangeeditor.util.Storage
import kotlin.collections.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OrangeRepositoryImpl(
    private val editorStateDao: EditorStateDao,
    private val imageLayerDao: ImageLayerDao,
    private val textLayerDao: TextLayerDao,
    private val storage: Storage,
) : OrangeRepository {

  override fun getAllEditorState(): Flow<List<EditorStateEntity>> =
      editorStateDao.getAllEditorStateEntity()

  override suspend fun getEditorStatById(editorId: String): EditorState =
      withContext(Dispatchers.IO) {
        val editorStateEntity = editorStateDao.getEditorStateEntityById(editorId = editorId)
        val allImageLayerEntity = imageLayerDao.getImagerLayerByEditorStateId(editorId = editorId)
        val allTextLayerEntity = textLayerDao.geTextLayerByEditorStateId(editorId = editorId)

        val allImageLayer = coroutineScope {
          allImageLayerEntity
              .map { entity ->
                async { entity.toDomain(storage.loadBitmapFromPath(entity.base.bitmapStoredPath)) }
              }
              .awaitAll()
        }

        val allTextLayer = coroutineScope {
          allTextLayerEntity
              .map { entity ->
                async {
                  entity.toDomain(
                      storage.loadBitmapFromPath(entity.base.bitmapStoredPath)
                          ?: createWhiteBitmap()
                  )
                }
              }
              .awaitAll()
        }
        val combineList = (allImageLayer + allTextLayer).sortedBy { it.zIndex }
        val editorState = editorStateEntity.toDomain(layers = combineList)
        return@withContext editorState
      }

  override suspend fun saveEditorState(editorState: EditorState) =
      withContext(Dispatchers.IO) {
        // Delete old entry
        val oldEditorState = editorStateDao.getEditorStateEntityByIdCanNull(editorState.editorId)

        oldEditorState?.let { deleteEditorStateById(oldEditorState.editorId) }

        // Create new entry
        val previewBitmap =
            storage.getBitmapFromLayer(
                layers = editorState.layers,
                customizer = editorState.customizer,
                canvasFormat = editorState.canvasFormat,
                canvasScreenSize = editorState.canvasSize,
            )
        val previewUrl =
            storage.saveBitmapToAppStorage(
                bitmap = previewBitmap,
                storageType = StorageType.PREVIEW_DIR,
                quality = 50,
            )

        val editorStateEntity = editorState.toEntity(previewUrl = previewUrl)
        editorStateDao.saveEditorStateEntity(editorStateEntity = editorStateEntity)
        editorState.layers.forEach { layer ->
          when (layer) {
            is ImageLayer -> {
              layer.bitmap?.let { nonNumBitmap ->
                val bitmapPath =
                    storage.saveBitmapToAppStorage(
                        bitmap = nonNumBitmap,
                        storageType = StorageType.IMAGES_DIR,
                    )
                bitmapPath?.let {
                  val imageLayerEntity =
                      layer.toEntity(editorState.editorId, bitmapPath = bitmapPath)
                  imageLayerDao.saveImageLayerEntity(imageLayerEntity = imageLayerEntity)
                }
              }
            }

            is TextLayer -> {
              layer.bitmap?.let { nonNumBitmap ->
                val bitmapPath =
                    storage.saveBitmapToAppStorage(
                        bitmap = nonNumBitmap,
                        storageType = StorageType.TEXT_DIR,
                    )
                bitmapPath?.let {
                  val textLayerEntity =
                      layer.toEntity(editorState.editorId, bitmapPath = bitmapPath)
                  textLayerDao.saveTextLayerEntity(textLayerEntity = textLayerEntity)
                }
              }
            }
          }
        }
      }

  override suspend fun deleteEditorStateById(editorId: String) =
      withContext(Dispatchers.IO) {
        // Delete text
        val allTextLayerEntity = textLayerDao.geTextLayerByEditorStateId(editorId)
        allTextLayerEntity.forEach { storage.deleteBitmapFromPath(it.base.bitmapStoredPath) }

        // Delete image
        val allImageLayerEntity = imageLayerDao.getImagerLayerByEditorStateId(editorId)
        allImageLayerEntity.forEach { storage.deleteBitmapFromPath(it.base.bitmapStoredPath) }

        // Delete editor state
        val editorState = editorStateDao.getEditorStateEntityById(editorId)
        editorState.previewUrl?.let { path -> storage.deleteBitmapFromPath(path) }
        editorStateDao.deleteEditorStateEntity(editorId)
      }

  private fun createWhiteBitmap(): Bitmap {
    val bitmap = createBitmap(1, 1)

    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.White.toArgb())

    return bitmap
  }
}
