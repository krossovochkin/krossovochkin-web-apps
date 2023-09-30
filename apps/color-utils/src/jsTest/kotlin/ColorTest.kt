import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ColorTest {

    @Test
    fun colorWhiteTest() {
        val color = Color.White

        assertEquals(expected = "ffffffff", actual = color.hex)

        assertEquals(expected = 255, actual = color.alpha)
        assertEquals(expected = 255, actual = color.red)
        assertEquals(expected = 255, actual = color.green)
        assertEquals(expected = 255, actual = color.blue)

        assertContentEquals(expected = intArrayOf(255, 255, 255, 255), actual = color.argb)
    }

    @Test
    fun colorBlackTest() {
        val color = Color.Black

        assertEquals(expected = "ff000000", actual = color.hex)

        assertEquals(expected = 255, actual = color.alpha)
        assertEquals(expected = 0, actual = color.red)
        assertEquals(expected = 0, actual = color.green)
        assertEquals(expected = 0, actual = color.blue)

        assertContentEquals(expected = intArrayOf(255, 0, 0, 0), actual = color.argb)
    }

    @Test
    fun colorFromArgbTest() {
        val color = Color.fromArgb(
            alpha = 23,
            red = 50,
            green = 240,
            blue = 128,
        )

        assertEquals(expected = "1732f080", actual = color.hex)

        assertEquals(expected = 23, actual = color.alpha)
        assertEquals(expected = 50, actual = color.red)
        assertEquals(expected = 240, actual = color.green)
        assertEquals(expected = 128, actual = color.blue)
    }

    @Test
    fun colorFromHexTest() {
        val color = Color.fromHex(hex = "1732f080")

        assertEquals(expected =  "1732f080", actual = color.hex)

        assertEquals(expected = 23, actual = color.alpha)
        assertEquals(expected = 50, actual = color.red)
        assertEquals(expected = 240, actual = color.green)
        assertEquals(expected = 128, actual = color.blue)
    }

    @Test
    fun updateAlphaTest() {
        val color = Color.White

        color.alpha = 128

        assertEquals(expected = 128, actual = color.alpha)
    }

    @Test
    fun updateRedTest() {
        val color = Color.White

        color.red = 128

        assertEquals(expected = 128, actual = color.red)
    }

    @Test
    fun updateGreenTest() {
        val color = Color.White

        color.green = 128

        assertEquals(expected = 128, actual = color.green)
    }

    @Test
    fun updateBlueTest() {
        val color = Color.White

        color.blue = 128

        assertEquals(expected = 128, actual = color.blue)
    }

    @Test
    fun updateHexTest() {
        val color = Color.White

        color.hex = "2385facd"

        assertEquals(expected = "2385facd", actual = color.hex)
    }
}