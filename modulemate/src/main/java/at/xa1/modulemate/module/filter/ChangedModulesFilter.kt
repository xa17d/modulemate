package at.xa1.modulemate.module.filter

import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Module

class ChangedModulesFilter(
    private val repository: GitRepository
) : ModulesFilter {
    override val name: String
        get() = "ChangedModules"

    override fun filter(allModules: List<Module>): List<Module> {
        val changedFiles = repository.getChangedFiles()
        return allModules.findModulesByFiles(changedFiles)
    }
}

fun List<Module>.findModuleByFile(fileRelativePath: String): Module? = this
    .filter { module -> fileRelativePath.startsWith(module.relativePath + "/") }
    .fold<Module, Module?>(null) { moduleWithLongestPath, module ->
        when {
            moduleWithLongestPath == null -> module
            moduleWithLongestPath.relativePath.length >= module.relativePath.length -> moduleWithLongestPath
            else -> module
        }
    }

fun List<Module>.findModulesByFiles(filesRelativePath: List<String>): List<Module> = filesRelativePath
    .mapNotNull { file -> this.findModuleByFile(file) }
    .toSet() // deduplicate
    .toList()
