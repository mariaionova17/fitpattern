package domain.layout

import domain.geometry.Point
import domain.geometry.Transformation

data class Placement (
    val id: String,
    val pieceId: String,
    val fabricPieceIndex: Int,
    val transformation: Transformation
)