package domain.layout

import domain.geometry.Point
import domain.geometry.Transformation

data class Placement (
    val pieceId: String,
    val fabricPieceIndex: Int,
    val transformation: Transformation
)