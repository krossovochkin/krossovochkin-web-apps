object ColorBlender {
    fun blend(
        foreground: Color,
        background: Color,
    ): Color {
        if (background.alpha != 255) {
            throw IllegalArgumentException("Background should have alpha 255")
        }

        val ratio = foreground.opacity
        val inverseRatio = 1 - foreground.opacity

        return Color.fromArgb(
            alpha = 255,
            red = (ratio * foreground.red + inverseRatio * background.red).toInt(),
            green = (ratio * foreground.green + inverseRatio * background.green).toInt(),
            blue = (ratio * foreground.blue + inverseRatio * background.blue).toInt(),
        )
    }
}