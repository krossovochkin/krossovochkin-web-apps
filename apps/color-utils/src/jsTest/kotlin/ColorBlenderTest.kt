import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ColorBlenderTest {

    @Test
    fun blend_ifForegroundFullyVisible_returnsForeground() {
        val foreground = Color.White
        val background = Color.Black
        val blended = ColorBlender.blend(foreground, background)

        assertEquals(expected = "ffffffff", blended.hex)
    }

    @Test
    fun blend_ifForegroundNotVisible_returnsBackground() {
        val foreground = Color.White.apply { alpha = 0 }
        val background = Color.Black
        val blended = ColorBlender.blend(foreground, background)

        assertEquals(expected = "ff000000", blended.hex)
    }

    @Test
    fun blend_ifForegroundHalfVisible_returnsBlended() {
        val foreground = Color.White.apply { alpha = 128 }
        val background = Color.Black
        val blended = ColorBlender.blend(foreground, background)

        assertEquals(expected = "ff808080", blended.hex)
    }

    @Test
    fun blend_ifForegroundPartiallyVisible_returnsBlended() {
        val foreground = Color.White.apply { alpha = 15 }
        val background = Color.Black
        val blended = ColorBlender.blend(foreground, background)

        assertEquals(expected = "ff0f0f0f", blended.hex)
    }

    @Test
    fun blend_ifBackgroundHasOpacity_throws() {
        val foreground = Color.White
        val background = Color.Black.apply { alpha = 45 }
        runCatching { ColorBlender.blend(foreground, background) }
            .onSuccess { fail() }
    }
}