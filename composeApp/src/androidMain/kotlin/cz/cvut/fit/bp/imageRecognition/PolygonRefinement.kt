package cz.cvut.fit.bp.imageRecognition

fun refinePolygonAdaptively(
    originalContour: List<CvPoint>,
    initialPolygon: List<CvPoint>,
    maxErrorPx: Double = 8.0,
    maxVertices: Int = 20
): List<CvPoint> {
    if (originalContour.size < 3) return initialPolygon
    if (initialPolygon.size < 3) return initialPolygon

    val refined = initialPolygon.toMutableList()

    while (refined.size < maxVertices) {
        var bestInsertPosition = -1
        var bestPointToInsert: CvPoint? = null
        var bestError = 0.0

        for (edgeIndex in refined.indices) {
            val a = refined[edgeIndex]
            val b = refined[(edgeIndex + 1) % refined.size]

            val aIndex = nearestContourIndex(originalContour, a)
            val bIndex = nearestContourIndex(originalContour, b)

            val arc = contourArcPoints(originalContour, aIndex, bIndex)

            if (arc.size <= 2) continue

            var maxDistOnThisEdge = 0.0
            var worstPoint: CvPoint? = null

            for ((_, p) in arc) {
                val d = pointToSegmentDistance(p, a, b)
                if (d > maxDistOnThisEdge) {
                    maxDistOnThisEdge = d
                    worstPoint = p
                }
            }

            if (maxDistOnThisEdge > bestError) {
                bestError = maxDistOnThisEdge
                bestPointToInsert = worstPoint
                bestInsertPosition = edgeIndex + 1
            }
        }

        if (bestPointToInsert == null || bestError <= maxErrorPx) {
            break
        }

        refined.add(bestInsertPosition, bestPointToInsert)
    }

    return refined
}