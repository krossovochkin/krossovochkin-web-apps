import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.js.Date
import kotlin.math.floor

fun main() {
    renderComposable(rootElementId = "root") {
        var instant by remember { mutableStateOf(Date()) }
        val epochMillis by derivedStateOf { instant.getTime() }
        val epochSeconds by derivedStateOf { floor(instant.getTime() / 1000) }
        var isMillis by remember { mutableStateOf(true) }

        Select({
            onInput {
                isMillis = it.value == "0"
            }
        }) {
            Option("0") { Text("millis") }
            Option("1") { Text("seconds") }
        }

        Br()

        Input(InputType.Number) {
            value(if (isMillis) epochMillis else epochSeconds)
            onInput {
                instant = if (isMillis) {
                    Date(it.value?.toLong() ?: 0L)
                } else {
                    Date(it.value?.toLong()?.let { it * 1000 } ?: 0L)
                }
            }
        }

        Br()

        Input(InputType.Date) {
            value(instant.toISOString().substringBefore('T'))
            onInput {
                if (it.value.isNotEmpty()) {
                    val components = it.value.split('-')
                    instant = instant.copy(
                        year = components[0].toInt(),
                        month = components[1].toInt(),
                        day = components[2].toInt(),
                    )
                }
            }
        }
        Input(InputType.Time) {
            value(instant.toISOString().substringAfter('T').substringBefore('.'))
            onInput {
                if (it.value.isNotEmpty()) {
                    val components = it.value.split(':')
                    instant = instant.copy(
                        hour = components[0].toInt(),
                        minute = components[1].toInt(),
                        second = components[2].toInt(),
                    )
                }
            }
        }
        if (isMillis) {
            var millis: Int? by remember { mutableStateOf(instant.getMilliseconds()) }
            Input(InputType.Number) {
                value(millis ?: Double.NaN)
                min("0")
                max("999")
                onInput {
                    if ((it.value as? Double)?.isNaN() == true) {
                        millis = null
                    } else {
                        millis = (it.value?.toInt() ?: 0).let {
                            when {
                                it > 999 -> 999
                                it < 0 -> 0
                                else -> it
                            }
                        }
                        instant = instant.copy(millis = millis)
                    }
                }
            }
        }

        Br()

        Text(instant.toISOString())

        Br()

        Text(instant.toUTCString())
    }
}

private fun Date.copy(
    year: Int? = null,
    month: Int? = null,
    day: Int? = null,
    hour: Int? = null,
    minute: Int? = null,
    second: Int? = null,
    millis: Int? = null,
): Date {
    println("$year $month $day $hour $minute $second $millis")
    return Date(
        Date.UTC(
            year ?: this.getUTCFullYear(),
            month ?: this.getUTCMonth(),
            day ?: this.getUTCDay(),
            hour ?: this.getUTCHours(),
            minute ?: this.getUTCMinutes(),
            second ?: this.getUTCSeconds(),
            millis ?: this.getUTCMilliseconds(),
        )
    )
}
