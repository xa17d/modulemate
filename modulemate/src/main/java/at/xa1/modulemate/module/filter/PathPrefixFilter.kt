package at.xa1.modulemate.module.filter

import at.xa1.modulemate.module.Module

class PathPrefixFilter(private val prefix: String) : ModulesFilter {
    override fun filter(allModules: List<Module>): List<Module> =
        allModules.filter { module -> module.relativePath.startsWith(prefix) }
}
