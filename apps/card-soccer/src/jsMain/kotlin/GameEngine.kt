object GameEngine {

    fun makeMove(gameState: GameState): GameState {
        if (gameState.isPlayerMove) {
            return gameState
        }
        return when (val phase = gameState.phase) {
            GamePhase.OpenCard -> gameState.reduce(GameAction.OpenCard)
            is GamePhase.ChooseField -> {
                val row = phase.row

                val column =
                    gameState.field[row].indexOfFirst { it.isOpened && gameState.opponentCurrentCard.isMatch(it) }
                        .takeIf { it != -1 }
                        ?: gameState.field[row]
                            .mapIndexed { index, fieldCard -> if (fieldCard.isOpened) -1 else index }
                            .filter { it != -1 }
                            .random()
                gameState.reduce(GameAction.SelectRowCard(row = row, column = column))
            }
            GamePhase.GoalCheck -> gameState.reduce(GameAction.SelectGoal)
            GamePhase.GameOver -> gameState
        }
    }
}