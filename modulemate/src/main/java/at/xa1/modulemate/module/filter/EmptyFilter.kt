package at.xa1.modulemate.module.filter

import at.xa1.modulemate.module.Module

class EmptyFilter : ModulesFilter {
    override fun filter(allModules: List<Module>): List<Module> = emptyList()
}
