package at.xa1.modulemate.module.filter

import at.xa1.modulemate.module.Module

/**
 * Filter indicating that a custom filtering is applied.
 */
object CustomFilter : ModulesFilter {
    override val name: String
        get() = "Custom"

    override fun filter(allModules: List<Module>): List<Module> =
        error("CustomFilter is only a marker and cannot actually filter")
}
