package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.PlacementRulesEntity
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import kotlinx.serialization.json.Json

private val rulesJson = Json

fun PlacementRules.toRulesEntity(projectId: String): PlacementRulesEntity {
    return PlacementRulesEntity(
        projectId = projectId,
        allowMirror = allowMirror,
        allowedAnglesJson = rulesJson.encodeToString(rotation.allowedAnglesDeg)
    )
}

fun PlacementRulesEntity.toPlacementRules(): PlacementRules {
    return PlacementRules(
        rotation = RotationRules(
            allowedAnglesDeg = rulesJson.decodeFromString(allowedAnglesJson)
        ),
        allowMirror = allowMirror
    )
}