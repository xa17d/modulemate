package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.filter.ModulesFilter

class ChangeFilter(
    private val modules: Modules,
    private val filter: ModulesFilter
) : CommandStep {
    override fun run(): CommandResult {
        modules.applyFilter(filter)
        return CommandResult.SUCCESS
    }
}
