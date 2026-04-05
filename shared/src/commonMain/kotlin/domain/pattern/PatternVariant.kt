package domain.pattern

data class PatternVariant(
    val id: String,
    val size: String,
    val pieces: List<PatternPiece>
)