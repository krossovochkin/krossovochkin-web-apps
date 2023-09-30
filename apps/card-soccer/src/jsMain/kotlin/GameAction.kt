sealed interface GameAction {

    object NewGame : GameAction

    object OpenCard : GameAction

    data class SelectRowCard(
        val row: Int,
        val column: Int,
    ) : GameAction

    object SelectGoal : GameAction
}