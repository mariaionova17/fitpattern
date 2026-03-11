package domain.layout

data class LayoutScore(
    val height: Double,
    val width: Double
): Comparable<LayoutScore> {
    override fun compareTo(other: LayoutScore): Int {
        return compareValuesBy(this, other, {it.height}, {it.width})
    }
}