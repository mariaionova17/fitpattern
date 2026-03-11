package domain.pattern

import domain.geometry.Shape
import domain.geometry.Transformation
import domain.geometry.apply

fun PatternPiece.placedShape(transform: Transformation): Shape =
    shape.apply(transform)