package com.ruhaan.orangeeditor.domain.model.canvas

data class CanvasCustomizer(
    val canvasBackgroundType: CanvasBackgroundType = CanvasBackgroundType.GRADIENT,
    val canvasColor: CanvasColor = CanvasColor.WHITE,
    val canvasGradient: Gradient = Gradient.SUNSET,
)
