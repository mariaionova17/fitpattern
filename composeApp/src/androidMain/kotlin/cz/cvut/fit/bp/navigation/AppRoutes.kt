package cz.cvut.fit.bp.navigation

object AppRoutes {
    const val Projects = "projects"
    const val ProjectCreation = "project_creation"
    const val FabricImage = "fabric_image"
    const val ImagePick = "image_pick"

    const val ProjectDetailRoute = "project_detail/{projectId}"
    fun projectDetail(projectId: String): String {
        return "project_detail/$projectId"
    }
}