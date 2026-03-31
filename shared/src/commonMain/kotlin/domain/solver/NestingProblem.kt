package domain.solver

import domain.fabric.Fabric
import domain.pattern.PatternPiece
import domain.project.Project

data class NestingProblem(
    val project: Project,
    val pieces: List<PatternPiece>
)