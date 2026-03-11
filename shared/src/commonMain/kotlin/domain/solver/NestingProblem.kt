package domain.solver

import domain.fabric.Fabric
import domain.pattern.PatternPiece

data class NestingProblem(
    val fabrics: List<Fabric>,
    val pieces: List<PatternPiece>
)