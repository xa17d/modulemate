package at.xa1.modulemate.module.filter

import at.xa1.modulemate.module.Module

class AllModulesFilter : ModulesFilter {
    override val name: String
        get() = "AllModules"

    override fun filter(allModules: List<Module>): List<Module> = allModules
}
