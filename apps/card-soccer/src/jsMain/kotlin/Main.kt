import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

const val CARD_WIDTH_PX = 50
const val CARD_HEIGHT_PX = 70
const val PADDING_PX = 5

fun main() {
    renderComposable(rootElementId = "root") {
        var gameState by remember { mutableStateOf(GameState.create()) }

        LaunchedEffect(gameState) {
            delay(1_000L)
            gameState = GameEngine.makeMove(gameState)
        }

        Game(
            gameState = gameState,
            onAction = { action ->
                if (gameState.isPlayerMove) {
                    gameState = gameState.reduce(action)
                }
            }
        )
    }
}

@Composable
fun Game(
    gameState: GameState,
    onAction: (GameAction) -> Unit,
) {
    Div({ style { background("lightgray") } }) {
        if (gameState.opponentCurrentCard == null) {
            Deck(
                onClick = { onAction(GameAction.OpenCard) },
                deck = gameState.opponentDeck,
            )
        } else {
            CurrentCard(gameState.opponentCurrentCard)
        }
        Spacer()
        FlipCard(
            fieldCard = gameState.opponentGoal,
            onClick = { onAction(GameAction.SelectGoal) }
        )

        Br()

        gameState.field.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, card ->
                FlipCard(
                    fieldCard = card,
                    onClick = { onAction(GameAction.SelectRowCard(rowIndex, columnIndex)) }
                )
            }
            Br()
        }

        if (gameState.playerCurrentCard == null) {
            Deck(
                onClick = { onAction(GameAction.OpenCard) },
                deck = gameState.playerDeck,
            )
        } else {
            CurrentCard(gameState.playerCurrentCard)
        }
        Spacer()
        FlipCard(
            fieldCard = gameState.playerGoal,
            onClick = { onAction(GameAction.SelectGoal) }
        )

        Br()

        InfoPanel(gameState, onAction)
    }
}

@Composable
fun CardFront(card: FieldCard, onClick: () -> Unit) {
    CardFront(card.card, onClick, card.selectionState)
}

@Composable
fun CardFront(
    card: Card,
    onClick: () -> Unit,
    selectionState: FieldCard.SelectionState = FieldCard.SelectionState.NotSelected
) {
    Span(
        {
            onClick { onClick() }
            style {
                display(DisplayStyle.InlineBlock)
                textAlign("center")
                color(
                    when (card.suite.color) {
                        Card.Color.Red -> Color.red
                        Card.Color.Black -> Color.black
                    }
                )
                background(
                    when (selectionState) {
                        FieldCard.SelectionState.NotSelected -> "white"
                        FieldCard.SelectionState.SelectedMatch -> "lightgreen"
                        FieldCard.SelectionState.SelectedNotMatch -> "pink"
                    }
                )
                width(CARD_WIDTH_PX.px)
                height(CARD_HEIGHT_PX.px)
                margin(PADDING_PX.px)
                lineHeight(CARD_HEIGHT_PX.px)
            }
        }
    ) {
        Text(card.toString())
    }
}

@Composable
fun CardBack(
    color: CSSColorValue = Color.black,
    onClick: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    Span(
        {
            onClick { onClick() }
            style {
                color(color)
                display(DisplayStyle.InlineBlock)
                textAlign("center")
                background("blue")
                width(CARD_WIDTH_PX.px)
                height(CARD_HEIGHT_PX.px)
                margin(PADDING_PX.px)
                lineHeight(CARD_HEIGHT_PX.px)
            }
        }
    ) {
        content()
    }
}

@Composable
fun Deck(
    onClick: () -> Unit,
    deck: List<Card>
) {
    CardBack(color = Color.white, onClick = { onClick() }) {
        Text(deck.size.toString())
    }
}

@Composable
fun FlipCard(fieldCard: FieldCard, onClick: () -> Unit) {
    if (fieldCard.isOpened) {
        CardFront(fieldCard, onClick)
    } else {
        CardBack(
            onClick = { onClick() }
        ) { Text("\u200B") }
    }
}

@Composable
fun CurrentCard(currentCard: Card?) {
    if (currentCard != null) {
        CardFront(currentCard, {})
    }
}

@Composable
fun Spacer() {
    Span({
        style {
            display(DisplayStyle.InlineBlock)
            width((PADDING_PX / 2 + PADDING_PX + CARD_WIDTH_PX / 2).px)
            height(CARD_HEIGHT_PX.px)
        }
    }) { Text("\u200B") }
}

@Composable
fun InfoPanel(gameState: GameState, onAction: (GameAction) -> Unit) {
    Text("Score: ${gameState.playerScore} - ${gameState.opponentScore}")
    Br()
    Text(gameState.instruction)
    Br()
    if (gameState.phase == GamePhase.GameOver) {
        Button(
            { onClick { onAction(GameAction.NewGame) } }
        ) {
            Text("New game")
        }
    }
}