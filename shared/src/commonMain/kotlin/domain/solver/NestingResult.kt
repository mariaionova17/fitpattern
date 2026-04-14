package domain.solver

import domain.layout.LayoutState

data class NestingResult(
    val id: String,
    val bestLayout: LayoutState,
    val triedLayouts: Int = 0,
    val timeMs: Long = 0
)