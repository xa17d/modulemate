package at.xa1.modulemate.module.filter

import at.xa1.modulemate.module.Module

interface ModulesFilter {
    val name: String

    fun filter(allModules: List<Module>): List<Module>
}
