package at.xa1.modulemate.command.step

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.cli.CliFormat
import at.xa1.modulemate.cli.CliTable
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.command.CommandResult

class Help(
    private val commandList: CommandList,
) : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        Cli.subHeading("Variables:")
        Cli.table {
            context.variables.getAll().forEach { variable ->
                val value =
                    try {
                        variable.getValue()
                    } catch (e: IllegalStateException) {
                        CliTable.FormattedCell(
                            content = "Error: " + e.message,
                            formatting = CliFormat.RED + CliFormat.BOLD,
                        )
                    }

                row(variable.name, value)
            }
        }

        Cli.line()
        Cli.subHeading("Commands:")
        Cli.table {
            commandList.allCommands.forEach { command ->
                row(command.shortcuts.joinToString(", "), command.name)
            }
        }

        return CommandResult.SUCCESS
    }

    companion object {
        val SHORTCUTS: List<String> = listOf("help", "?")
    }
}
