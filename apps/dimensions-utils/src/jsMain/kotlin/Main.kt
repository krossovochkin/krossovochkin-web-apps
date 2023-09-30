import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.math.sqrt

// https://material.io/blog/device-metrics
// TODO: put into tables
// fix fonts and paddings
fun main() {
    renderComposable(rootElementId = "root") {
        DpiCalculator()

        Br()
        Br()

        DpPxConverter()
    }

}

@Composable
private fun DpiCalculator() {
    var screenDiagonalInchesText by remember { mutableStateOf("4.95") }
    val screenDiagonalInches by derivedStateOf { screenDiagonalInchesText.toDoubleOrNull() }
    var screenWidthPx: Int? by remember { mutableStateOf(1080) }
    var screenHeightPx: Int? by remember { mutableStateOf(1920) }
    val dpi: Double? by derivedStateOf {
        when {
            screenWidthPx == null -> null
            screenHeightPx == null -> null
            screenDiagonalInches == null -> null
            else -> sqrt(((screenWidthPx!! * screenWidthPx!!) + (screenHeightPx!! * screenHeightPx!!)).toDouble()) / screenDiagonalInches!!
        }
    }
    val density: Double? by derivedStateOf { dpi?.let { it / 160 } }
    Text("Screen diagonal in: ")
    Input(InputType.Text) {
        value(screenDiagonalInchesText)
        onInput { screenDiagonalInchesText = it.value }
    }
    Br()
    Text("Screen width px: ")
    Input(InputType.Number) {
        value(if (screenWidthPx != null) screenWidthPx!! else Double.NaN)
        onInput {
            screenWidthPx = if ((it.value as? Double)?.isNaN() != true) {
                it.value?.toInt()
            } else {
                null
            }
        }
    }
    Br()
    Text("Screen height px: ")
    Input(InputType.Number) {
        value(if (screenHeightPx != null) screenHeightPx!! else Double.NaN)
        onInput {
            screenHeightPx = if ((it.value as? Double)?.isNaN() != true) {
                it.value?.toInt()
            } else {
                null
            }
        }
    }
    if (dpi != null) {
        Br()
        Text("dpi: ${dpi?.floor(2)}")
    }
    if (density != null) {
        Br()
        Text("density: ${density?.floor(2)}")
    }
}

@Composable
private fun DpPxConverter() {
    var value: Number? by remember { mutableStateOf(48) }
    var isDpInput: Boolean by remember { mutableStateOf(false) }
    var dpi: String by remember { mutableStateOf("mdpi") }
    var customDpiText: String by remember { mutableStateOf("1") }
    val customDpi: Double? by derivedStateOf { customDpiText.toDoubleOrNull() }
    val density: Double? by derivedStateOf {
        when (dpi) {
            "mdpi" -> 1.0
            "hdpi" -> 1.5
            "xhdpi" -> 2.0
            "xxhdpi" -> 3.0
            "xxxhdpi" -> 4.0
            "custom" -> customDpi
            else -> throw IllegalStateException("Unknown dpi: $dpi")
        }
    }
    val dp: Double? by derivedStateOf {
        when {
            isDpInput -> value?.toDouble()
            value == null -> null
            density == null -> null
            else -> value?.toDouble()!! / density!!
        }
    }

    Input(InputType.Number) {
        value(if (value != null) value!! else Double.NaN)
        onInput {
            value = if ((it.value as? Double)?.isNaN() != true) {
                it.value
            } else {
                null
            }
        }
    }
    Select(
        {
            onInput {
                isDpInput = it.value == "dp"
            }
        }
    ) {
        Option("px") { Text("px") }
        Option("dp") { Text("dp") }

    }
    if (!isDpInput) {
        Select(
            {
                onInput {
                    dpi = it.value!!
                }
            }
        ) {
            Option("mdpi") { Text("mdpi") }
            Option("hdpi") { Text("hdpi") }
            Option("xhdpi") { Text("xhdpi") }
            Option("xxhdpi") { Text("xxhdpi") }
            Option("xxxhdpi") { Text("xxxhdpi") }
            Option("custom") { Text("custom") }
        }
    }
    if (!isDpInput && dpi == "custom") {
        Input(InputType.Text) {
            value(customDpiText)
            onInput {
                customDpiText = it.value
            }
        }
    }

    Br()

    val currentDp = dp
    if (currentDp != null) {
        Text("${currentDp.floor(2)}dp")
        Br()
        Text("mdpi: ${currentDp.floor(2)}px")
        Br()
        Text("hdpi: ${(currentDp * 1.5).floor(2)}px")
        Br()
        Text("xhdpi: ${(currentDp * 2.0).floor(2)}px")
        Br()
        Text("xxhdpi: ${(currentDp * 3.0).floor(2)}px")
        Br()
        Text("xxxhdpi: ${(currentDp * 4.0).floor(2)}px")
    }
}

private fun Double.floor(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.floor(this * multiplier) / multiplier
}

