package com.ruhaan.orangeeditor.presentation.editor.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kavi.droid.color.picker.ui.pickers.GridColorPicker
import com.ruhaan.orangeeditor.presentation.theme.CanvasOrange
import com.ruhaan.orangeeditor.presentation.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTextSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    isNew: Boolean,
    onTextAdd:
        (
            text: String,
            fontColor: Color,
            fontWeight: FontWeight,
            fontStyle: FontStyle,
        ) -> Unit,
) {

  val fontWeightOptions = listOf(FontWeight.Normal to "Normal", FontWeight.Bold to "Bold")
  val fontStyleOptions = listOf(FontStyle.Normal to "Normal", FontStyle.Italic to "Italic")

  // States
  var isNewText by remember { mutableStateOf(isNew) }
  var inputText by remember { mutableStateOf("") }
  var selectedColor by remember { mutableStateOf(Color.Black) }
  var selectedFontWeight by remember { mutableStateOf(FontWeight.Normal) }
  var selectedFontStyle by remember { mutableStateOf(FontStyle.Normal) }

  // Other
  val isValidInput by remember { derivedStateOf { inputText.isNotBlank() } }
  var isInteractedWithTextField by remember { mutableStateOf(false) }
  val shape = RoundedCornerShape(8.dp)

  // UI
  ModalBottomSheet(modifier = modifier, onDismissRequest = onDismissRequest) {
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
            text = "Text",
            style = Typography.headlineSmall,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Button(
              onClick = {
                onTextAdd(
                    inputText,
                    selectedColor,
                    selectedFontWeight,
                    selectedFontStyle,
                )
                onDismissRequest()
              },
              enabled = isValidInput,
          ) {
            Text(text = if (isNewText) "Add text" else "Update")
          }
        }
      }

      // Text Field
      TextField(
          value = inputText,
          onValueChange = {
            isInteractedWithTextField = true
            inputText = it
          },
          modifier = Modifier.fillMaxWidth(),
          placeholder = { Text(text = "Enter text") },
          shape = RoundedCornerShape(16.dp),
          colors =
              TextFieldDefaults.colors(
                  focusedIndicatorColor = Color.Transparent,
                  unfocusedIndicatorColor = Color.Transparent,
              ),
          isError = isInteractedWithTextField && !isValidInput,
      )
      Spacer(modifier = modifier.height(4.dp))

      // Font weight
      Text(text = "Font weight", modifier = Modifier.fillMaxWidth(), style = Typography.titleMedium)
      Row(modifier = Modifier.fillMaxWidth()) {
        fontWeightOptions.forEach { fontWeight ->
          RadioButton(
              modifier =
                  Modifier.clip(shape)
                      .then(
                          if (selectedFontWeight == fontWeight.first)
                              Modifier.border(2.dp, CanvasOrange, shape)
                          else Modifier
                      ),
              text = fontWeight.second,
              style = Typography.bodyMedium.copy(fontWeight = fontWeight.first),
          ) {
            selectedFontWeight = fontWeight.first
          }
          Spacer(modifier = modifier.width(width = 4.dp))
        }
      }
      Spacer(modifier = modifier.height(4.dp))

      // Font style
      Text(text = "Font style", modifier = Modifier.fillMaxWidth(), style = Typography.titleMedium)
      Row(modifier = Modifier.fillMaxWidth()) {
        fontStyleOptions.forEach { fontStyle ->
          RadioButton(
              modifier =
                  Modifier.clip(shape)
                      .then(
                          if (selectedFontStyle == fontStyle.first)
                              Modifier.border(2.dp, CanvasOrange, shape)
                          else Modifier
                      ),
              text = fontStyle.second,
              style = Typography.bodyMedium.copy(fontStyle = fontStyle.first),
          ) {
            selectedFontStyle = fontStyle.first
          }
          Spacer(modifier = modifier.width(width = 4.dp))
        }
      }
      Spacer(modifier = modifier.width(width = 4.dp))

      // Font color
      Text(text = "Font color", modifier = Modifier.fillMaxWidth(), style = Typography.titleMedium)
      GridColorPicker(
          modifier = Modifier.padding(),
          lastSelectedColor = selectedColor,
          onColorSelected = { selectedColor = it },
      )
    }
  }
}
