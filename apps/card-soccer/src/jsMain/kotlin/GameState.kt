data class GameState internal constructor(
    val opponentGoal: FieldCard,
    val opponentDeck: List<Card>,
    val field: List<List<FieldCard>>,
    val playerGoal: FieldCard,
    val playerDeck: List<Card>,
    val phase: GamePhase = GamePhase.OpenCard,
    val playerCurrentCard: Card? = null,
    val opponentCurrentCard: Card? = null,
    val opponentScore: Int = 0,
    val playerScore: Int = 0,
    val isPlayerMove: Boolean = true,
) {

    val instruction: String
        get() = buildString {
            if (phase == GamePhase.GameOver) {
                append("Game over: ")
                append(
                    when {
                        playerScore > opponentScore -> "player won"
                        opponentScore > playerScore -> "opponent won"
                        else -> "draw"
                    }
                )
            } else {
                append(if (isPlayerMove) "Player" else "Opponent")
                append(" ")
                append(
                    when (phase) {
                        GamePhase.OpenCard -> "open card"
                        is GamePhase.ChooseField -> "choose card in ${phase.row + 1} row"
                        GamePhase.GoalCheck -> "check goal"
                        GamePhase.GameOver -> ""
                    }
                )
            }
        }

    companion object {
        fun create(): GameState {
            fun createShuffledDeck(): ArrayDeque<Card> {
                val deck = mutableListOf<Card>()
                for (suite in Card.Suite.entries) {
                    for (type in Card.Type.entries) {
                        deck += Card(type, suite)
                    }
                }
                return ArrayDeque(deck.shuffled())
            }

            val deck = createShuffledDeck()

            val opponentGoal = deck.removeFirst().toFieldCard()
            val field: List<List<FieldCard>> = (1..4).map {
                (1..4).map { deck.removeFirst().toFieldCard() }
            }
            val playerGoal = deck.removeFirst().toFieldCard()
            val opponentDeck = (1..9).map { deck.removeFirst() }
            val playerDeck = (1..9).map { deck.removeFirst() }

            return GameState(
                opponentGoal = opponentGoal,
                field = field,
                playerGoal = playerGoal,
                opponentDeck = opponentDeck,
                playerDeck = playerDeck,
            )
        }
    }
}

fun GameState.reduce(action: GameAction): GameState {
    var newState = when (action) {
        GameAction.OpenCard -> {
            if (this.phase == GamePhase.OpenCard) {
                if (this.isPlayerMove) {
                    this
                        .copy(
                            field = this.field.map { it.map { it.copy(selectionState = FieldCard.SelectionState.NotSelected) } },
                            opponentGoal = opponentGoal.copy(selectionState = FieldCard.SelectionState.NotSelected),
                            playerGoal = playerGoal.copy(selectionState = FieldCard.SelectionState.NotSelected),
                            playerDeck = this.playerDeck.drop(1),
                            playerCurrentCard = this.playerDeck[0],
                            opponentCurrentCard = null,
                            phase = GamePhase.ChooseField(row = 0)
                        )
                } else {
                    this
                        .copy(
                            field = this.field.map { it.map { it.copy(selectionState = FieldCard.SelectionState.NotSelected) } },
                            opponentGoal = opponentGoal.copy(selectionState = FieldCard.SelectionState.NotSelected),
                            playerGoal = playerGoal.copy(selectionState = FieldCard.SelectionState.NotSelected),
                            opponentDeck = this.opponentDeck.drop(1),
                            opponentCurrentCard = this.opponentDeck[0],
                            playerCurrentCard = null,
                            phase = GamePhase.ChooseField(row = 0)
                        )
                }
            } else {
                this
            }
        }
        is GameAction.SelectRowCard -> {
            when (this.phase) {
                is GamePhase.ChooseField -> {
                    if (action.row != adjustedRow(phase.row)) {
                        this
                    } else {
                        val currentCard = if (isPlayerMove) playerCurrentCard else opponentCurrentCard
                        val isMatch = currentCard.isMatch(field[action.row][action.column])

                        copy(
                            field = field.mapIndexed { rowIndex, row ->
                                if (rowIndex == action.row) {
                                    field[action.row].mapIndexed { columnIndex, fieldCard ->
                                        if (columnIndex == action.column) {
                                            fieldCard.copy(
                                                isOpened = true,
                                                selectionState = if (isMatch) FieldCard.SelectionState.SelectedMatch else FieldCard.SelectionState.SelectedNotMatch,
                                            )
                                        } else {
                                            fieldCard
                                        }
                                    }
                                } else {
                                    row
                                }
                            },
                            phase = if (isMatch) {
                                GamePhase.ChooseField(row = phase.row + 1)
                            } else {
                                GamePhase.OpenCard
                            },
                            isPlayerMove = if (isMatch) isPlayerMove else !isPlayerMove
                        )
                    }
                }
                GamePhase.OpenCard,
                GamePhase.GoalCheck,
                GamePhase.GameOver -> this
            }
        }
        GameAction.SelectGoal -> {
            if (phase == GamePhase.GoalCheck) {
                if (isPlayerMove) {
                    val isMatch = playerCurrentCard.isMatch(opponentGoal)
                    copy(
                        isPlayerMove = !isPlayerMove,
                        phase = GamePhase.OpenCard,
                        playerScore = if (isMatch) playerScore + 1 else playerScore,
                        opponentGoal = opponentGoal.copy(
                            isOpened = true,
                            selectionState = if (isMatch) FieldCard.SelectionState.SelectedMatch else FieldCard.SelectionState.SelectedNotMatch,
                        )
                    )
                } else {
                    val isMatch = opponentCurrentCard.isMatch(playerGoal)
                    copy(
                        isPlayerMove = !isPlayerMove,
                        phase = GamePhase.OpenCard,
                        opponentScore = if (isMatch) opponentScore + 1 else opponentScore,
                        playerGoal = playerGoal.copy(
                            isOpened = true,
                            selectionState = if (isMatch) FieldCard.SelectionState.SelectedMatch else FieldCard.SelectionState.SelectedNotMatch,
                        )
                    )
                }
            } else {
                this
            }
        }
        GameAction.NewGame -> GameState.create()
    }

    newState = newState.skipPhases()

    if (newState.phase == GamePhase.OpenCard && newState.opponentDeck.isEmpty() && newState.playerDeck.isEmpty()) {
        newState = newState.copy(phase = GamePhase.GameOver)
    }

    if (newState == this) {
        println("Can't create new state from $this and $action")
    }

    return newState
}

private fun GameState.adjustedRow(row: Int): Int {
    return if (isPlayerMove) 3 - row else row
}

private fun GameState.allOpen(row: Int): Boolean {
    return field[adjustedRow(row)].all { it.isOpened }
}

private fun GameState.skipPhases(): GameState {
    if (this.phase !is GamePhase.ChooseField) return this

    var newState = this

    var newRow = phase.row

    while (newRow < 4 && allOpen(newRow)) {
        newState = newState.copy(
            field = newState.field.mapIndexed { rowIndex, row ->
                if (rowIndex == adjustedRow(newRow)) {
                    row.map { it.copy(selectionState = FieldCard.SelectionState.SelectedMatch) }
                } else {
                    row
                }
            }
        )
        newRow++
    }

    val newPhase = if (newRow >= 4) {
        GamePhase.GoalCheck
    } else {
        GamePhase.ChooseField(row = newRow)
    }

    return newState.copy(phase = newPhase)
}