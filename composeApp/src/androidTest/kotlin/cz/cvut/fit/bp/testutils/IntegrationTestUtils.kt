package cz.cvut.fit.bp.testutils

import domain.fabric.FabricPiece
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.geometry.Transformation
import domain.layout.LayoutScore
import domain.layout.LayoutState
import domain.layout.Placement
import domain.pattern.Pattern
import domain.pattern.PatternPiece
import domain.pattern.PatternVariant
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import domain.project.Project
import domain.solver.NestingResult
import org.junit.Assert.fail

fun waitUntil(
    timeoutMs: Long = 3_000,
    intervalMs: Long = 50,
    condition: () -> Boolean
) {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < deadline) {
        if (condition()) return
        Thread.sleep(intervalMs)
    }
    fail("Condition was not met within ${timeoutMs}ms")
}

fun rectangleShape(width: Double, height: Double): Shape =
    Shape(
        contour = Polygon(
            listOf(
                Point(0.0, 0.0),
                Point(width, 0.0),
                Point(width, height),
                Point(0.0, height)
            )
        )
    )

fun sampleProject(
    id: String = "project-1",
    name: String = "Integration Project"
): Project =
    Project(
        id = id,
        name = name,
        fabricPieces = listOf(
            FabricPiece(
                id = "${id}-fabric-1",
                name = "Fabric 1",
                contour = rectangleShape(1500.0, 2500.0)
            )
        ),
        rules = PlacementRules(
            rotation = RotationRules(listOf(0.0, 180.0)),
            allowMirror = true
        ),
        seamAllowance = 12.0
    )

fun samplePattern(
    id: String = "pattern-1",
    name: String = "Basic T-Shirt",
    variantId: String = "${id}-variant-m",
    size: String = "M"
): Pattern =
    Pattern(
        id = id,
        name = name,
        variants = listOf(
            PatternVariant(
                id = variantId,
                size = size,
                pieces = listOf(
                    PatternPiece(
                        id = "${id}-piece-1",
                        name = "Front",
                        shape = rectangleShape(200.0, 300.0)
                    ),
                    PatternPiece(
                        id = "${id}-piece-2",
                        name = "Back",
                        shape = rectangleShape(210.0, 300.0)
                    )
                )
            )
        ),
        pictureUri = "content://$id",
        downloadLink = "https://example.com/$id"
    )

fun sampleNestingResult(
    id: String = "result-1",
    pieceId: String = "pattern-1-piece-1"
): NestingResult =
    NestingResult(
        id = id,
        bestLayout = LayoutState(
            placed = listOf(
                Placement(
                    id = "$id-placement-1",
                    pieceId = pieceId,
                    fabricPieceIndex = 0,
                    transformation = Transformation()
                )
            ),
            unplaced = emptyList(),
            score = LayoutScore(100.0, 50.0)
        ),
        triedLayouts = 10,
        timeMs = 123
    )
