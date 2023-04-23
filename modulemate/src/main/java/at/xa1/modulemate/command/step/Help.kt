package at.xa1.modulemate.command.step

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.cli.CliTable
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.command.CommandResult

class Help(
    private val commandList: CommandList
) : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        Cli.subHeading("Variables:")
        Cli.table {
            context.variables.getAll().forEach { variable ->
                val value = try {
                    variable.getValue()
                } catch (e: IllegalStateException) {
                    CliTable.FormattedCell(
                        content = "Error: " + e.message,
                        formatting = CliColor.RED + CliColor.BOLD
                    )
                }

                row(variable.name, value)
            }
        }

        Cli.line()
        Cli.subHeading("Commands:")
        Cli.table {
            commandList.allCommands.forEach { command ->
                row(command.shortcut, command.name)
            }
        }

        return CommandResult.SUCCESS
    }

    companion object {
        const val SHORTCUT = "help"
    }
}
