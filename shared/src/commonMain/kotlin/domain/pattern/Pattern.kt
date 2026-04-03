package domain.pattern

data class Pattern(
    val id: String,
    val name: String,
    val sizes: List<String>,
    val pieces: List<PatternPiece>
)