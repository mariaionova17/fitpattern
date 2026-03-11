package domain.solver

interface NestingSolver {
    fun solve(problem: NestingProblem): NestingResult
}