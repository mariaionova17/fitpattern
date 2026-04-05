package domain.pattern

data class Pattern(
    val id: String,
    val name: String,
    val variants: List<PatternVariant>,
    val pictureUri: String
)