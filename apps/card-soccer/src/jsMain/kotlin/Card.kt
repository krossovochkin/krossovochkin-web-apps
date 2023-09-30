data class Card(
    val type: Type,
    val suite: Suite,
) {

    override fun toString(): String {
        return "${suite.value}${type.value}"
    }

    enum class Type(val value: String) {
        Six("6"),
        Seven("7"),
        Eight("8"),
        Nine("9"),
        Ten("10"),
        Jack("J"),
        Queen("Q"),
        King("K"),
        Ace("A"),
    }

    enum class Suite(val value: String, val color: Color) {
        Hearts("♥", Color.Red),
        Diamonds("♦", Color.Red),
        Clubs("♣", Color.Black),
        Spades("♠", Color.Black),
    }

    enum class Color {
        Red,
        Black,
    }
}

internal fun Card.toFieldCard(): FieldCard = FieldCard(this, false, FieldCard.SelectionState.NotSelected)

fun Card?.isMatch(card: FieldCard): Boolean {
    return this != null && (this.type == card.card.type || this.suite == card.card.suite)
}
