package cz.cvut.fit.bp.navigation

object AppRoutes {
    const val Projects = "projects"
    const val ProjectCreation = "project_creation"
    const val FabricImage = "fabric_image"
    const val SelectImage = "select_image"

    const val PatternSuggestionsRoute = "pattern_suggestions/{projectId}"
    fun patternSuggestions(projectId: String) = "pattern_suggestions/$projectId"

    const val ProjectDetailRoute = "project_detail/{projectId}"
    fun projectDetail(projectId: String) = "project_detail/$projectId"

    const val PatternDetailRoute = "pattern_detail/{projectId}/{patternId}"
    fun patternDetail(projectId: String, patternId: String) = "pattern_detail/$projectId/$patternId"

    const val LayoutRoute = "layout/{projectId}/{variantId}"
    fun layout(projectId: String, variantId: String) = "layout/$projectId/$variantId"
}