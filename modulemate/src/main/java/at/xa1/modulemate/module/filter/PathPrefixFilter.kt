package at.xa1.modulemate.module.filter

import at.xa1.modulemate.module.Module

class PathPrefixFilter(private val prefix: String) : ModulesFilter {
    override val name: String
        get() = "Prefix"

    override fun filter(allModules: List<Module>): List<Module> =
        allModules.filter { module -> module.relativePath.startsWith(prefix) }
}
