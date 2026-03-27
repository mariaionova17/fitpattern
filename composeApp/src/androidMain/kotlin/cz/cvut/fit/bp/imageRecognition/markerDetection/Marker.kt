package cz.cvut.fit.bp.imageRecognition.markerDetection

import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint

data class Marker(
    val topLeft: CvPoint,
    val topRight: CvPoint,
    val bottomRight: CvPoint,
    val bottomLeft: CvPoint
)