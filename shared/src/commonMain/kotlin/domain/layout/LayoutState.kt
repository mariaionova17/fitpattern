package domain.layout

data class LayoutState(
    val placed: List<Placement>,
    val unplaced: List<String> = emptyList(),
    val score: LayoutScore = LayoutScore(0.0, 0.0)
)