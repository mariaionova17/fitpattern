package cz.cvut.fit.bp.imageRecognition.polygonDetection

// the function improves polygon by adding more points where the shape is badly approximated
fun smoothPolygon(originalContour: List<CvPoint>, initialPolygon: List<CvPoint>, maxErrorPx: Double = 8.0, maxVertices: Int = 20): List<CvPoint> {

    if (originalContour.size < 3 || initialPolygon.size < 3) 
        return initialPolygon

    val result = initialPolygon.toMutableList()

    while (result.size < maxVertices) {
        var bestInsertPosition = -1
        var bestPointToInsert: CvPoint? = null
        var bestError = 0.0

        for (edgeIndex in result.indices) {
            val a = result[edgeIndex]
            val b = result[(edgeIndex + 1) % result.size]

            val aIndex = nearestContourIndex(originalContour, a)
            val bIndex = nearestContourIndex(originalContour, b)

            val contourSection = contourSectionPoints(originalContour, aIndex, bIndex)

            if (contourSection.size <= 2)
                continue

            var maxDistOnThisEdge = 0.0
            var worstPoint: CvPoint? = null

            for ((_, p) in contourSection) {
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

        result.add(bestInsertPosition, bestPointToInsert)
    }

    return result
}