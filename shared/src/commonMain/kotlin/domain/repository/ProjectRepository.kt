package domain.repository

import domain.project.Project

interface ProjectRepository {
    suspend fun saveProject(project: Project)
    suspend fun getProjectById(id: String): Project?
    suspend fun getAllProjects(): List<Project>
    suspend fun deleteProject(id: String)
}