package at.xa1.modulemate.module.filter

import at.xa1.modulemate.module.Module

class EmptyFilter : ModulesFilter {
    override val name: String
        get() = "Empty"

    override fun filter(allModules: List<Module>): List<Module> = emptyList()
}
