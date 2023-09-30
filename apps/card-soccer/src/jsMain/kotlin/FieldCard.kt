data class FieldCard(
    val card: Card,
    val isOpened: Boolean,
    val selectionState: SelectionState,
) {

    enum class SelectionState {
        NotSelected,
        SelectedMatch,
        SelectedNotMatch,
    }

    override fun toString(): String {
        return if (isOpened) {
            card.toString()
        } else {
            " "
        }
    }
}