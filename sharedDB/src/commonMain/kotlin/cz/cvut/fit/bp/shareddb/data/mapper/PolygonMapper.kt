package cz.cvut.fit.bp.shareddb.data.mapper

import domain.geometry.Polygon
import kotlinx.serialization.json.Json

object PolygonMapper {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun polygonToJson(polygon: Polygon): String =
        json.encodeToString(polygon)

    fun polygonFromJson(value: String): Polygon =
        json.decodeFromString(value)

    fun polygonsToJson(polygons: List<Polygon>): String =
        json.encodeToString(polygons)

    fun polygonsFromJson(value: String): List<Polygon> =
        json.decodeFromString(value)
}