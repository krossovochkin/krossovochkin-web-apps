class Color private constructor(
    private var value: Long,
) {
    var alpha: Int
        get() = (value and 0xff000000 shr 24).toInt()
        set(value) {
            this.value = (this.value and 0x00ffffff) or (0xff000000 and (value shl 24).toLong())
        }
    var red: Int
        get() = (value and 0x00ff0000 shr 16).toInt()
        set(value) {
            this.value = (this.value and 0xff00ffff) or (0x00ff0000.toLong() and (value shl 16).toLong())
        }
    var green: Int
        get() = (value and 0x0000ff00 shr 8).toInt()
        set(value) {
            this.value = (this.value and 0xffff00ff) or (0x0000ff00.toLong() and (value shl 8).toLong())
        }
    var blue: Int
        get() = (value and 0x000000ff).toInt()
        set(value) {
            this.value = (this.value and 0xffffff00) or (0x000000ff.toLong() and value.toLong())
        }

    val argb: IntArray
        get() = intArrayOf(alpha, red, green, blue)

    var hex: String
        get() = buildString {
            append(alpha.toString(16).padStart(2, '0'))
            append(red.toString(16).padStart(2, '0'))
            append(green.toString(16).padStart(2, '0'))
            append(blue.toString(16).padStart(2, '0'))
        }
        set(value) {
            alpha = value.substring(0, 2).toInt(16)
            red = value.substring(2, 4).toInt(16)
            green = value.substring(4, 6).toInt(16)
            blue = value.substring(6, 8).toInt(16)
        }

    var opacity: Double
        get() = alpha / 255.0
        set(value) {
            alpha = (value * 255).toInt()
        }

    fun copy(): Color = Color(this.value)

    companion object {

        val White get() = Color(0xffffffff)
        val Black get() = Color(0xff000000)

        fun fromArgb(alpha: Int, red: Int, green: Int, blue: Int): Color {
            return White.apply {
                this.alpha = alpha
                this.red = red
                this.green = green
                this.blue = blue
            }
        }

        fun fromHex(hex: String): Color {
            return White.apply { this.hex = hex }
        }
    }
}

