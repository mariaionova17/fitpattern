package domain.solver

interface NestingSolver {
    fun solve(problem: NestingProblem, onProgress: (Float) -> Unit): NestingResult
}