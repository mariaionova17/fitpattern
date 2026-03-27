package cz.cvut.fit.bp.imageRecognition

import android.graphics.Bitmap

data class DualPolarityResult(
    val darkCandidate: MaskCandidate?,
    val lightCandidate: MaskCandidate?,
    val bestCandidate: MaskCandidate?
)

data class MaskCandidate(
    val name: String,
    val polygon: List<CvPoint>,
    val score: Double
)

fun detectBestPolygon(bitmap: Bitmap): DualPolarityResult {
    val masks = generateDarkAndLightMasks(bitmap)

    val darkPolygon = extractLargestPolygon(
        masks.darkMaskMat,
        bitmap.width,
        bitmap.height
    )

    val lightPolygon = extractLargestPolygon(
        masks.lightMaskMat,
        bitmap.width,
        bitmap.height
    )

    val darkCandidate = darkPolygon?.let {
        MaskCandidate(
            name = "dark",
            polygon = it,
            score = scorePolygon(it, bitmap.width, bitmap.height)
        )
    }

    val lightCandidate = lightPolygon?.let {
        MaskCandidate(
            name = "light",
            polygon = it,
            score = scorePolygon(it, bitmap.width, bitmap.height)
        )
    }

    val best = listOfNotNull(darkCandidate, lightCandidate)
        .maxByOrNull { it.score }

    return DualPolarityResult(
        darkCandidate = darkCandidate,
        lightCandidate = lightCandidate,
        bestCandidate = best
    )
}