package at.xa1.modulemate.module.filter

import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Module

class ChangedModulesFilter(
    private val repository: GitRepository
) : ModulesFilter {
    override fun filter(allModules: List<Module>): List<Module> {
        val changedFiles = repository.getChangedFiles()

        return changedFiles.mapNotNull { file -> findModuleByFile(allModules, file) }
            .toSet() // deduplicate
            .toList()
    }

    private fun findModuleByFile(allModules: List<Module>, relativePath: String): Module? = allModules
        .filter { module -> relativePath.startsWith(module.relativePath + "/") }
        .fold<Module, Module?>(null) { moduleWithLongestPath, module ->
            when {
                moduleWithLongestPath == null -> module
                moduleWithLongestPath.relativePath.length >= module.relativePath.length -> moduleWithLongestPath
                else -> module
            }
        }
}
