package com.ruhaan.orangeeditor.presentation.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ruhaan.orangeeditor.presentation.crop.CropScreen
import com.ruhaan.orangeeditor.presentation.editor.EditorScreen
import com.ruhaan.orangeeditor.presentation.editor.EditorViewModel
import com.ruhaan.orangeeditor.presentation.home.HomeScreen
import kotlinx.coroutines.delay

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Route.Home.route,
) {

  val viewmodel: EditorViewModel = hiltViewModel()

  NavHost(navController = navController, startDestination = startDestination) {
    composable(route = Route.Home.route) {
      HomeScreen(navController = navController, viewmodel = viewmodel)
    }

    composable(
        route = Route.Editor.route,
    ) {
      val context = LocalContext.current
      var backPressedOnce by remember { mutableStateOf(false) }

      LaunchedEffect(key1 = backPressedOnce) {
        if (backPressedOnce) {
          delay(2000)
          backPressedOnce = false
        }
      }

      BackHandler(enabled = true) {
        if (backPressedOnce) {
          navController.popBackStack()
          viewmodel.saveDraft()
          viewmodel.resetState()
        } else {
          backPressedOnce = true
          Toast.makeText(
                  context,
                  "Press back again to exit and save changes to draft",
                  Toast.LENGTH_SHORT,
              )
              .show()
        }
      }

      EditorScreen(
          viewModel = viewmodel,
          navController = navController,
      )
    }

    composable(route = Route.CropScreen.route) {
      CropScreen(
          selectedLayer = viewmodel.getSelectedLayer(),
          onSave = viewmodel::updateBitmapOfSelectedImageLayer,
          onNavigateBack = navController::popBackStack,
      )
    }
  }
}
