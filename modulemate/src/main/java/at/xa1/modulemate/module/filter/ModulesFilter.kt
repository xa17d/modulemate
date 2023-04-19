package at.xa1.modulemate.module.filter

import at.xa1.modulemate.module.Module

fun interface ModulesFilter {
    fun filter(allModules: List<Module>): List<Module>
}
