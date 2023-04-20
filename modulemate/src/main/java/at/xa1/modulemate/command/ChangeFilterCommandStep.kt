package at.xa1.modulemate.command

import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.filter.ModulesFilter

class ChangeFilterCommandStep(
    override val runWhen: RunWhen,
    private val modules: Modules,
    private val filter: ModulesFilter
) : CommandStep {
    override fun run(): CommandResult {
        modules.applyFilter(filter)
        return CommandResult.SUCCESS
    }
}
