package com.ruhaan.orangeeditor.presentation.customize.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruhaan.orangeeditor.domain.model.canvas.CanvasColor

@Composable
fun ColorPatchGridCustom(
    colors: List<CanvasColor>,
    selectedColor: CanvasColor? = null,
    onColorSelected: (CanvasColor) -> Unit,
    itemsPerRow: Int = 6,
    modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    colors.chunked(itemsPerRow).forEach { rowColors ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        rowColors.forEach { canvasColor ->
          ColorPatch(
              color = canvasColor,
              isSelected = canvasColor == selectedColor,
              onClick = { onColorSelected(canvasColor) },
              modifier = Modifier.weight(1f),
          )
        }
        // empty spaces if the last row is not complete
        repeat(itemsPerRow - rowColors.size) { Spacer(modifier = Modifier.weight(1f)) }
      }
    }
  }
}

@Composable
fun ColorPatch(
    color: CanvasColor,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Box(
      modifier =
          modifier
              .clip(RoundedCornerShape(8.dp))
              .background(color.color)
              .border(
                  width = if (isSelected) 3.dp else 1.dp,
                  color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.3f),
                  shape = RoundedCornerShape(8.dp),
              )
              .clickable { onClick() }
              .size(48.dp),
      contentAlignment = Alignment.Center,
  ) {
    if (isSelected) {
      Text(
          text = "✓",
          color = if (color.color.luminance() > 0.5f) Color.Black else Color.White,
          fontSize = 24.sp,
      )
    }
  }
}
