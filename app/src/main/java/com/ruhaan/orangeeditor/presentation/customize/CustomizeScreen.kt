package com.ruhaan.orangeeditor.presentation.customize

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruhaan.orangeeditor.R
import com.ruhaan.orangeeditor.domain.model.canvas.CanvasBackgroundType
import com.ruhaan.orangeeditor.domain.model.canvas.CanvasColor
import com.ruhaan.orangeeditor.domain.model.canvas.CanvasCustomizer
import com.ruhaan.orangeeditor.domain.model.canvas.Gradient
import com.ruhaan.orangeeditor.domain.model.format.CanvasFormat
import com.ruhaan.orangeeditor.domain.model.layer.EditorState
import com.ruhaan.orangeeditor.presentation.components.LargeIconButton
import com.ruhaan.orangeeditor.presentation.customize.components.ColorPatchGridCustom
import com.ruhaan.orangeeditor.presentation.customize.components.meshGradient
import com.ruhaan.orangeeditor.presentation.editor.EditorViewModel
import com.ruhaan.orangeeditor.presentation.theme.CanvasOrange
import com.ruhaan.orangeeditor.presentation.theme.TextPrimary
import com.ruhaan.orangeeditor.presentation.theme.TextSecondary
import com.ruhaan.orangeeditor.presentation.theme.Typography

@Composable
fun CustomizeScreen(
    modifier: Modifier = Modifier,
    viewmodel: EditorViewModel,
    onNavigateBack: () -> Unit,
    onGetStated: () -> Unit,
) {
  // local states
  val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var fileName by remember { mutableStateOf("Untitled") }
  var showFormatDropDownMenu by remember { mutableStateOf(false) }
  var selectedCanvasBackgroundType by remember { mutableStateOf(CanvasBackgroundType.COLOR) }
  var selectedFormated by remember { mutableStateOf(CanvasFormat.POST) }
  var selectedCanvasColor by remember { mutableStateOf(CanvasColor.WHITE) }
  var selectedGradient by remember { mutableStateOf(Gradient.OCEAN_BREEZE) }

  val focusManager = LocalFocusManager.current
  val isKeyboardOpen = isKeyBordOpen()

  LaunchedEffect(isKeyboardOpen) {
    if (!isKeyboardOpen) {
      focusManager.clearFocus()
    }
  }

  Scaffold(
      topBar = {
        Row(modifier = Modifier
          .background(CanvasOrange)
          .fillMaxWidth()
          .height(statusBarHeight)) {}
      },
  ) { _ ->
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier =
            modifier
              .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
              ) {
                focusManager.clearFocus()
              }
              .fillMaxSize()
              .padding(vertical = 8.dp, horizontal = 12.dp)
              .safeDrawingPadding(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
          LargeIconButton(
              iconId = R.drawable.ic_back_arrow,
              contentDescription = "go back",
              onClick = onNavigateBack,
          )
          Button(
              onClick = {
                focusManager.clearFocus()
                viewmodel.newEditState(
                    editorState =
                        EditorState()
                            .copy(
                                canvasFormat = selectedFormated,
                                fileName = fileName,
                                customizer =
                                    CanvasCustomizer(
                                        canvasBackgroundType = selectedCanvasBackgroundType,
                                        canvasColor = selectedCanvasColor,
                                        canvasGradient = selectedGradient,
                                    ),
                            )
                )
                onGetStated()
              }
          ) {
            Text("Get stated")
          }
        }
      }
      item(span = { GridItemSpan(maxLineSpan) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "File name", style = Typography.titleLarge.copy(fontSize = 18.sp))
          TextField(
              value = fileName,
              onValueChange = { fileName = it },
              placeholder = { Text(text = "File name", color = TextSecondary) },
              singleLine = true,
              shape = RectangleShape,
              colors =
                  TextFieldDefaults.colors(
                      focusedContainerColor = Color.Transparent,
                      unfocusedContainerColor = Color.Transparent,
                      disabledContainerColor = Color.Transparent,
                      focusedTextColor = TextPrimary,
                      unfocusedTextColor = TextPrimary,
                  ),
              modifier = Modifier.fillMaxWidth(.75f),
          )
        }
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
          Text(text = "Canvas format", style = Typography.titleLarge.copy(fontSize = 18.sp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            OutlinedButton(
                onClick = {
                  showFormatDropDownMenu = !showFormatDropDownMenu
                  focusManager.clearFocus()
                }
            ) {
              Text(selectedFormated.title)
            }
            DropdownMenu(
                expanded = showFormatDropDownMenu,
                containerColor = CanvasOrange,
                onDismissRequest = { showFormatDropDownMenu = false },
            ) {
              CanvasFormat.entries.forEach { format ->
                DropdownMenuItem(
                    text = { Text("${format.title} (${format.aspectRatio})") },
                    onClick = {
                      selectedFormated = format
                      showFormatDropDownMenu = false
                      focusManager.clearFocus()
                    },
                )
              }
            }
          }
        }
      }
      item(span = { GridItemSpan(maxLineSpan) }) {
        Column {
          Text(text = "Canvas type", style = Typography.titleLarge.copy(fontSize = 18.sp))
          Row(
              verticalAlignment = Alignment.CenterVertically,
          ) {
            CanvasBackgroundType.entries.forEach { option ->
              Row(
                  modifier =
                      Modifier
                        .clickable(
                          onClick = {
                            selectedCanvasBackgroundType = option
                            focusManager.clearFocus()
                          }
                        )
                        .border(
                          2.dp,
                          if (selectedCanvasBackgroundType == option) CanvasOrange
                          else Color.Gray,
                          shape = RoundedCornerShape(20.dp),
                        )
                        .padding(start = 4.dp, end = 12.dp),
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                RadioButton(
                    selected = (selectedCanvasBackgroundType == option),
                    onClick = {
                      selectedCanvasBackgroundType = option
                      focusManager.clearFocus()
                    },
                )
                Text(
                    text = option.title,
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
            }
          }
        }
      }

      if (selectedCanvasBackgroundType == CanvasBackgroundType.COLOR) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          ColorPatchGridCustom(
              colors = CanvasColor.entries,
              selectedColor = selectedCanvasColor,
              onColorSelected = {
                selectedCanvasColor = it
                focusManager.clearFocus()
              },
          )
        }
      } else {
        Gradient.entries.forEach { gradient ->
          item {
            Box(
                modifier =
                    Modifier
                      .aspectRatio(9 / 16f)
                      .meshGradient(gradient.points)
                      .clickable(
                        onClick = {
                          selectedGradient = gradient
                          focusManager.clearFocus()
                        }
                      ),
                contentAlignment = Alignment.Center,
            ) {
              if (selectedGradient == gradient) {
                Text(
                    text = "✓",
                    color =
                        if (gradient.points[1][1].second.luminance() > 0.5f) Color.Black
                        else Color.White,
                    fontSize = 24.sp,
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun isKeyBordOpen(): Boolean {
  val imeInsets = WindowInsets.ime
  return imeInsets.getBottom(LocalDensity.current) > 0
}
