package com.ruhaan.orangeeditor.presentation.navigation

sealed class Route(val route: String) {

  data object Home : Route("home")

  data object Editor : Route("editor")

  data object CropScreen : Route("crop")

  data object Customize : Route("customize")
}
