package com.ruhaan.orangeeditor.presentation.editor.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ruhaan.orangeeditor.R
import com.ruhaan.orangeeditor.domain.model.layer.ImageLayer
import com.ruhaan.orangeeditor.domain.model.layer.Layer
import com.ruhaan.orangeeditor.domain.model.layer.TextLayer
import com.ruhaan.orangeeditor.presentation.theme.CanvasOrange
import com.ruhaan.orangeeditor.presentation.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayerPositionSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    layers: List<Layer>,
    selectedLayerId: String?,
    onLayerSelected: (String) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
) {

  ModalBottomSheet(
      modifier = modifier,
      onDismissRequest = onDismissRequest,
  ) {
    Column(
        modifier = Modifier.padding(top = 8.dp, end = 16.dp, bottom = 16.dp, start = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Header
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
            text = "Layers",
            style = Typography.headlineSmall,
        )
        Button(
            onClick = onDismissRequest,
        ) {
          Text(text = "Done")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Layer list
      LazyColumn(
          modifier = Modifier.fillMaxWidth().weight(1f),
          reverseLayout = true, // ← Add this
          verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        items(
            items = layers,
            key = { it.id },
        ) { layer ->
          Box(modifier = Modifier.animateItem()) {
            LayerPositionItem(
                layer = layer,
                isSelected = layer.id == selectedLayerId,
                onLayerLongPress = { onLayerSelected(layer.id) },
                onMoveUp = { onMoveUp(layer.id) },
                onMoveDown = { onMoveDown(layer.id) },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LayerPositionItem(
    layer: Layer,
    isSelected: Boolean,
    onLayerLongPress: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
) {
  val shape = RoundedCornerShape(8.dp)

  val layerName =
      when (layer) {
        is TextLayer -> layer.displayName
        is ImageLayer -> layer.displayName
      }

  Row(
      modifier =
          Modifier.fillMaxWidth()
              .clip(shape)
              .border(
                  border =
                      if (isSelected) BorderStroke(2.dp, CanvasOrange)
                      else BorderStroke(1.dp, Color.LightGray),
                  shape = shape,
              )
              .combinedClickable(
                  onClick = { /* Click does nothing */ },
                  onLongClick = onLayerLongPress,
              )
              .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    // 1) Preview box
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
          modifier =
              Modifier.size(48.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp)),
          contentAlignment = Alignment.Center,
      ) {
        when (layer) {
          is ImageLayer -> {
            layer.bitmap?.let { bmp ->
              Image(
                  painter = BitmapPainter(bmp.asImageBitmap()),
                  contentDescription = null,
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop,
              )
            }
                ?: run {
                  // Fallback if bitmap is null
                  Text(
                      text = "IMG",
                      style = Typography.bodySmall,
                  )
                }
          }

          is TextLayer -> {
            // Option A: simple text preview
            Text(
                text = layer.text.take(10), // truncate for thumbnail
                style =
                    Typography.bodySmall.copy(
                        fontWeight = layer.fontWeight,
                        fontStyle = layer.fontStyle,
                        color = layer.color,
                    ),
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis,
            )

            // Option B (if you prefer bitmap preview instead)
            // layer.bitmap?.let { bmp ->
            //     Image(
            //         painter = BitmapPainter(bmp.asImageBitmap()),
            //         contentDescription = null,
            //         modifier = Modifier.fillMaxSize(),
            //         contentScale = ContentScale.Fit,
            //     )
            // }
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // 2) Layer name
      Text(
          text = layerName,
          style = Typography.bodyMedium,
      )
    }

    // 3) Move up/down controls (unchanged)
    Row {
      IconButton(
          onClick = onMoveUp,
          enabled = layer.zIndex > 0,
      ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_down),
            contentDescription = "Move up",
            tint = LocalContentColor.current,
        )
      }
      IconButton(
          onClick = onMoveDown,
          enabled = layer.zIndex < Int.MAX_VALUE,
      ) {
        Icon(
            painter =
                painterResource(
                    id = R.drawable.ic_up
                ), // not a mistake (the buttons are replaced intentionally)
            contentDescription = "Move down",
            tint = LocalContentColor.current,
        )
      }
    }
  }
}
