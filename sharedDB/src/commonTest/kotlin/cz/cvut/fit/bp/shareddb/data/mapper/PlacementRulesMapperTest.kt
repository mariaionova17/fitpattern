package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.PlacementRulesEntity
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import kotlin.test.Test
import kotlin.test.assertEquals

class PlacementRulesMapperTest {

    @Test
    fun `toRulesEntity stores project id mirror flag and allowed angles`() {
        val rules = PlacementRules(
            rotation = RotationRules(listOf(0.0, 90.0, 180.0, 270.0)),
            allowMirror = true
        )

        val entity = rules.toRulesEntity("project-1")

        assertEquals("project-1", entity.projectId)
        assertEquals(true, entity.allowMirror)
        assertEquals(listOf(0.0, 90.0, 180.0, 270.0), entity.toPlacementRules().rotation.allowedAnglesDeg)
    }

    @Test
    fun `toPlacementRules restores rules from entity`() {
        val entity = PlacementRulesEntity(
            projectId = "project-1",
            allowMirror = true,
            allowedAnglesJson = "[0.0,90.0,180.0,270.0]"
        )

        val rules = entity.toPlacementRules()

        assertEquals(true, rules.allowMirror)
        assertEquals(listOf(0.0, 90.0, 180.0, 270.0), rules.rotation.allowedAnglesDeg)
    }
}
