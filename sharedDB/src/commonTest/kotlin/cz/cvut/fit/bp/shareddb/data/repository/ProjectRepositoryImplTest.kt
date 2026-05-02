package cz.cvut.fit.bp.shareddb.data.repository

import cz.cvut.fit.bp.shareddb.data.mapper.toFabricPieceEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toRulesEntity
import domain.fabric.FabricPiece
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import domain.project.Project
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProjectRepositoryImplTest {

    @Test
    fun `saveProject stores project rules and fabrics and clears old fabrics first`() = runTest {
        val dao = FakeProjectDao()
        val nestingDao = FakeNestingResultDao()
        dao.fabricsByProjectId["project-1"] = mutableListOf(
            FabricPiece(
                id = "old-fabric",
                contour = rectangleShape(1.0, 1.0)
            ).toFabricPieceEntity("project-1")
        )

        val repository = ProjectRepositoryImpl(dao, nestingDao)

        val project = sampleProject()

        repository.saveProject(project)

        assertEquals("project-1", dao.projects["project-1"]?.id)
        assertEquals("Summer project", dao.projects["project-1"]?.name)
        assertEquals(1.5, dao.projects["project-1"]?.seamAllowance)

        assertEquals(project.rules.toRulesEntity("project-1"), dao.rules["project-1"])

        val storedFabrics = dao.fabricsByProjectId["project-1"].orEmpty()
        assertEquals(2, storedFabrics.size)
        assertEquals(listOf("fabric-1", "fabric-2"), storedFabrics.map { it.id })

        assertTrue(
            dao.callLog.indexOf("deleteFabrics:project-1") < dao.callLog.indexOf("insertFabrics:project-1")
        )
    }

    @Test
    fun `getProjectById returns null when project is missing`() = runTest {
        val repository = ProjectRepositoryImpl(FakeProjectDao(), FakeNestingResultDao())

        val result = repository.getProjectById("missing")

        assertNull(result)
    }

    @Test
    fun `getProjectById returns null when rules are missing`() = runTest {
        val dao = FakeProjectDao()
        val nestingDao = FakeNestingResultDao()
        dao.projects["project-1"] = cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity(
            id = "project-1",
            name = "Summer project",
            seamAllowance = 1.5
        )

        val repository = ProjectRepositoryImpl(dao, nestingDao)

        val result = repository.getProjectById("project-1")

        assertNull(result)
    }

    @Test
    fun `getProjectById reconstructs full domain project`() = runTest {
        val dao = FakeProjectDao()
        val nestingDao = FakeNestingResultDao()
        val project = sampleProject()

        dao.projects[project.id] = cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity(
            id = project.id,
            name = project.name,
            seamAllowance = project.seamAllowance
        )
        dao.rules[project.id] = project.rules.toRulesEntity(project.id)
        dao.fabricsByProjectId[project.id] = project.fabricPieces
            .map { it.toFabricPieceEntity(project.id) }
            .toMutableList()

        val repository = ProjectRepositoryImpl(dao, nestingDao)

        val restored = repository.getProjectById(project.id)

        assertEquals(project, restored)
    }

    @Test
    fun `getAllProjects skips projects with missing rules`() = runTest {
        val dao = FakeProjectDao()
        val nestingDao = FakeNestingResultDao()

        val validProject = sampleProject()
        dao.projects[validProject.id] = cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity(
            id = validProject.id,
            name = validProject.name,
            seamAllowance = validProject.seamAllowance
        )
        dao.rules[validProject.id] = validProject.rules.toRulesEntity(validProject.id)
        dao.fabricsByProjectId[validProject.id] = validProject.fabricPieces
            .map { it.toFabricPieceEntity(validProject.id) }
            .toMutableList()

        dao.projects["project-2"] = cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity(
            id = "project-2",
            name = "Broken project",
            seamAllowance = 0.0
        )

        val repository = ProjectRepositoryImpl(dao, nestingDao)

        val projects = repository.getAllProjects()

        assertEquals(1, projects.size)
        assertEquals(validProject, projects.single())
    }

    @Test
    fun `deleteProject deletes fabrics then rules then project`() = runTest {
        val dao = FakeProjectDao()
        val nestingDao = FakeNestingResultDao()
        val repository = ProjectRepositoryImpl(dao, nestingDao)

        repository.deleteProject("project-1")

        assertEquals(
            listOf(
                "deleteFabrics:project-1",
                "deleteRules:project-1",
                "deleteProject:project-1"
            ),
            dao.callLog
        )
    }

    private fun sampleProject(): Project =
        Project(
            id = "project-1",
            name = "Summer project",
            fabricPieces = listOf(
                FabricPiece("fabric-1", rectangleShape(10.0, 5.0)),
                FabricPiece("fabric-2", rectangleShape(8.0, 4.0))
            ),
            rules = PlacementRules(
                rotation = RotationRules(listOf(0.0, 180.0)),
                allowMirror = true
            ),
            seamAllowance = 1.5
        )

    private fun rectangleShape(width: Double, height: Double): Shape =
        Shape(
            contour = Polygon(
                listOf(
                    Point(0.0, 0.0),
                    Point(width, 0.0),
                    Point(width, height),
                    Point(0.0, height)
                )
            )
        )
}
