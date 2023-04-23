package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.module.filter.ModulesFilter

class ChangeFilter(
    private val filterFactory: (context: CommandContext) -> ModulesFilter
) : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        context.modules.applyFilter(filterFactory(context))
        return CommandResult.SUCCESS
    }
}
