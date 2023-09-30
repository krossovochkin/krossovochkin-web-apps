sealed interface GamePhase {

    object OpenCard : GamePhase

    data class ChooseField(val row: Int) : GamePhase

    object GoalCheck : GamePhase

    object GameOver : GamePhase
}
