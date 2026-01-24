package com.ruhaan.orangeeditor.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.ruhaan.orangeeditor.R
import com.ruhaan.orangeeditor.domain.model.format.CanvasFormat
import com.ruhaan.orangeeditor.presentation.editor.EditorViewModel
import com.ruhaan.orangeeditor.presentation.home.components.CanvasFormatCard
import com.ruhaan.orangeeditor.presentation.home.components.DraftCard
import com.ruhaan.orangeeditor.presentation.navigation.Route
import com.ruhaan.orangeeditor.presentation.theme.BackgroundLight
import com.ruhaan.orangeeditor.presentation.theme.CanvasOrange
import com.ruhaan.orangeeditor.presentation.theme.CardBackground
import com.ruhaan.orangeeditor.presentation.theme.TextPrimary
import com.ruhaan.orangeeditor.presentation.theme.TextSecondary

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewmodel: EditorViewModel,
    navController: NavHostController,
) {
  val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  val allDraft by viewmodel.allDraft.collectAsState()
  val sortedAllDraft by rememberUpdatedState(allDraft.sortedByDescending { it.timestamp })
  Scaffold(
      topBar = {
        Row(modifier = Modifier.background(CanvasOrange).fillMaxWidth().height(statusBarHeight)) {}
      }
  ) { _ ->
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier =
            modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(bottom = 8.dp, start = 20.dp, end = 20.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

      // Header
      item(span = { GridItemSpan(maxLineSpan) }) {
        Column(modifier = Modifier.padding(top = 28.dp, bottom = 40.dp)) {
          Text(
              text = "Orange Editor",
              fontSize = 36.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = "What will you design today?", fontSize = 16.sp, color = TextSecondary)
        }
      }

      // Canvas format
      item(span = { GridItemSpan(maxLineSpan) }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
              text = "Create New",
              fontSize = 24.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimary,
          )
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            items(CanvasFormat.entries) { format ->
              CanvasFormatCard(
                  canvasFormat = format,
                  onClick = {
                    viewmodel.newEditorStateWithCanvasFormat(canvasFormat = format)
                    navController.navigate(Route.Editor.route)
                  },
              )
            }
            // Custom canvas
            item {
              Card(
                  modifier =
                      modifier.size(180.dp).clickable {
                        navController.navigate(Route.Customize.route)
                      },
                  colors = CardDefaults.cardColors(containerColor = CardBackground),
                  elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
              ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                  Text(
                      text = "Custom",
                      fontSize = 18.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = TextPrimary,
                  )
                }
              }
            }
          }
        }
      }

      // Draft
      item(span = { GridItemSpan(maxLineSpan) }) {
        Column(modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)) {
          Text(
              text = "Draft",
              fontSize = 24.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimary,
          )
        }
      }

      if (allDraft.isNotEmpty()) {
        itemsIndexed(sortedAllDraft) { _, draft ->
          DraftCard(
              modifier =
                  Modifier.shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp), clip = false)
                      .background(
                          color = MaterialTheme.colorScheme.surface,
                          shape = RoundedCornerShape(12.dp),
                      )
                      .padding(8.dp),
              draft = draft,
              onClick = {
                viewmodel.selectedDraft(draft.editorId)
                navController.navigate(Route.Editor.route)
              },
              onDeleteClick = { viewmodel.deleteSavedDraft(draft.editorId) },
          )
        }
        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }
      } else {
        item(span = { GridItemSpan(maxLineSpan) }) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                modifier = Modifier.size(300.dp),
                model = R.drawable.img_emty_draft,
                contentDescription = null,
            )
          }
        }
      }
    }
  }
}
