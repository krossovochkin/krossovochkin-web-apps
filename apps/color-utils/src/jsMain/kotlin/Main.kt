import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        var foregroundColor by remember { mutableStateOf(Color.White) }
        var backgroundColor by remember { mutableStateOf(Color.White) }
        var blendedColor by remember { mutableStateOf(Color.White) }

        Table(
            { style { fontFamily("monospace") } }
        ) {
            Tr {
                Td { Text("Foreground") }
                Td { Text("Background") }
                Td { Text("Blended") }
            }
            Tr {
                Td({ style { property("vertical-align", "top") } }) {
                    ColorPicker(foregroundColor) {
                        foregroundColor = it
                        blendedColor = ColorBlender.blend(it, backgroundColor)
                    }
                }
                Td({ style { property("vertical-align", "top") } }) {
                    ColorPicker(backgroundColor, allowChangeAlpha = false) {
                        backgroundColor = it
                        blendedColor = ColorBlender.blend(foregroundColor, it)
                    }
                }
                Td({ style { property("vertical-align", "top") } }) {
                    ColorResult(blendedColor)
                }
            }
        }
    }
}

@Composable
fun ColorPicker(color: Color, allowChangeAlpha: Boolean = true, onChanged: (Color) -> Unit) {
    var alpha: Int? by remember(color) { mutableStateOf(color.alpha) }
    var red: Int? by remember(color) { mutableStateOf(color.red) }
    var green: Int? by remember(color) { mutableStateOf(color.green) }
    var blue: Int? by remember(color) { mutableStateOf(color.blue) }
    var hex: String by remember(color) { mutableStateOf(color.hex) }

    Table({
        style { fontFamily("monospace") }
    }) {
        if (allowChangeAlpha) {
            ColorPickerRbgRow("Alpha", alpha) {
                alpha = it
                if (it != null) {
                    onChanged(color.copy().apply { this.alpha = it })
                }
            }
        }
        ColorPickerRbgRow("Red", red) {
            red = it
            if (it != null) {
                onChanged(color.copy().apply { this.red = it })
            }
        }
        ColorPickerRbgRow("Green", green) {
            green = it
            if (it != null) {
                onChanged(color.copy().apply { this.green = it })
            }
        }
        ColorPickerRbgRow("Blue", blue) {
            blue = it
            if (it != null) {
                onChanged(color.copy().apply { this.blue = it })
            }
        }
        ColorPickerHexRow("Hex", if (allowChangeAlpha || hex.length <= 6) hex else hex.substring(2)) {
            hex = it
            val validated = validateHexValue(it, allowAlpha = allowChangeAlpha)
            if (validated != null) {
                onChanged(color.copy().apply { this.hex = if (allowChangeAlpha) validated else "ff$validated" })
            }
        }
        ColorBox(color)
    }
}

@Composable
private fun ColorPickerRbgRow(title: String, value: Int?, onChanged: (Int?) -> Unit) {
    Tr {
        Td { Text(title) }
        Td {
            Input(InputType.Number) {
                value(value ?: Double.NaN)
                onInput { onChanged(validateRgbValue(it.value)) }
                style {
                    fontFamily("monospace")
                    width(100.px)
                    padding(0.px)
                    borderWidth(1.px)
                }
            }
        }
    }
}

@Composable
private fun ColorPickerHexRow(title: String, value: String, onChanged: (String) -> Unit) {
    Tr {
        Td { Text(title) }
        Td {
            Input(InputType.Text) {
                value(value)
                onInput { onChanged(it.value.replace("[^\\da-f]*".toRegex(), "")) }
                style {
                    fontFamily("monospace")
                    width(100.px)
                    padding(0.px)
                    borderWidth(1.px)
                }
            }
        }
    }
}

@Composable
private fun ColorBox(color: Color) {
    Tr {
        Td { Text("Color") }
        Td {
            Span({
                style {
                    display(DisplayStyle.InlineBlock)
                    width(100.px)
                    height(100.px)
                    backgroundColor(rgba(r = color.red, g = color.green, b = color.blue, a = color.opacity))
                    border {
                        color(org.jetbrains.compose.web.css.Color.black)
                        style(LineStyle.Solid)
                        width(1.px)
                    }
                }
            }) { }
        }
    }
}

@Composable
private fun ColorResult(color: Color) {
    Table({
        style { fontFamily("monospace") }
    }) {
        Tr {
            Td { Text("Red") }
            Td { Text("${color.red}") }
        }
        Tr {
            Td { Text("Green") }
            Td { Text("${color.green}") }
        }
        Tr {
            Td { Text("Blue") }
            Td { Text("${color.blue}") }
        }
        Tr {
            Td { Text("Hex") }
            Td { Text(color.hex) }
        }
        ColorBox(color)
    }
}

private fun validateHexValue(value: String, allowAlpha: Boolean = true): String? {
    if (value.length < if (allowAlpha) 8 else 6) {
        return null
    }
    if (value.toLongOrNull(16) == null) {
        return null
    }
    return value
}

private fun validateRgbValue(value: Number?): Int? {
    if (value == null || (value as? Double)?.isNaN() == true) {
        return null
    }
    if (value.toInt() < 0) {
        return 0
    }
    if (value.toInt() > 255) {
        return 255
    }
    return value.toInt()
}