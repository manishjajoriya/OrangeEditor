package com.ruhaan.orangeeditor.presentation.crop

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ruhaan.orangeeditor.domain.model.layer.ImageLayer
import com.ruhaan.orangeeditor.domain.model.layer.Layer
import com.ruhaan.orangeeditor.presentation.crop.components.ActionBar
import com.tanishranjan.cropkit.CropDefaults
import com.tanishranjan.cropkit.CropShape
import com.tanishranjan.cropkit.GridLinesType
import com.tanishranjan.cropkit.GridLinesVisibility
import com.tanishranjan.cropkit.ImageCropper
import com.tanishranjan.cropkit.rememberCropController

@Composable
fun CropScreen(
    modifier: Modifier = Modifier,
    selectedLayer: Layer?,
    onSave: (Bitmap) -> Unit,
    onNavigateBack: () -> Unit,
) {
  if (selectedLayer is ImageLayer && selectedLayer.bitmap != null) {
    val bitmap = selectedLayer.bitmap

    var cropMode by remember { mutableStateOf<CropShape>(CropShape.FreeForm) }

    val cropController =
        rememberCropController(
            bitmap = bitmap,
            cropOptions =
                CropDefaults.cropOptions(
                    handleRadius = 12.dp,
                    touchPadding = 14.dp,
                    cropShape = cropMode,
                    gridLinesType = GridLinesType.GRID,
                    gridLinesVisibility = GridLinesVisibility.ON_TOUCH,
                    contentScale = ContentScale.Fit,
                ),
        )

    Scaffold { innerPadding ->
      Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFFD3D3D3),
      ) {
        Column(
            modifier = modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp)) {
            ImageCropper(modifier = Modifier.fillMaxSize(), cropController = cropController)
          }

          Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            AspectRatioButton(
                label = "Free",
                selected = cropMode is CropShape.FreeForm,
                onClick = { cropMode = CropShape.FreeForm },
            )
            AspectRatioButton(
                label = "1:1",
                selected =
                    cropMode is CropShape.AspectRatio &&
                        (cropMode as CropShape.AspectRatio).ratio == 1f,
                onClick = { cropMode = CropShape.AspectRatio(1f) },
            )
            AspectRatioButton(
                label = "4:3",
                selected =
                    cropMode is CropShape.AspectRatio &&
                        (cropMode as CropShape.AspectRatio).ratio == 4f / 3f,
                onClick = { cropMode = CropShape.AspectRatio(4f / 3f) },
            )
            AspectRatioButton(
                label = "16:9",
                selected =
                    cropMode is CropShape.AspectRatio &&
                        (cropMode as CropShape.AspectRatio).ratio == 16f / 9f,
                onClick = { cropMode = CropShape.AspectRatio(16f / 9f) },
            )
            AspectRatioButton(
                label = "Original",
                selected = cropMode is CropShape.Original,
                onClick = { cropMode = CropShape.Original },
            )
          }

          ActionBar(
              onVerticalFlip = cropController::flipVertically,
              onHorizontalFlip = cropController::flipHorizontally,
              onAntiClockWiseRotate = cropController::rotateAntiClockwise,
              onClockWiseRotate = cropController::rotateClockwise,
          )

          Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Button(
                onClick = { onNavigateBack() },
                colors = ButtonDefaults.buttonColors(Color.White),
                modifier = Modifier.padding(18.dp),
            ) {
              Text("Back")
            }
            Button(
                onClick = {
                  onSave(cropController.crop())
                  onNavigateBack()
                },
                modifier = Modifier.padding(18.dp),
            ) {
              Text("Save")
            }
          }
        }
      }
    }
  } else {
    onNavigateBack()
  }
}

@Composable
private fun AspectRatioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Button(
      onClick = onClick,
      modifier = modifier,
      colors =
          ButtonDefaults.buttonColors(
              containerColor =
                  if (selected) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.surfaceVariant,
              contentColor =
                  if (selected) MaterialTheme.colorScheme.onPrimary
                  else MaterialTheme.colorScheme.onSurfaceVariant,
          ),
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
  ) {
    Text(text = label, style = MaterialTheme.typography.labelSmall)
  }
}
