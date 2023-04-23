package at.xa1.modulemate.command.step

import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.command.CommandResult

class Help(
    private val commandList: CommandList
) : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        commandList.allCommands.forEach { command ->
            println(
                "${command.shortcut}: ${command.name}"
            )
        }

        println("--- variables ---")

        context.variables.getAll().forEach {
            val value = try {
                it.getValue()
            } catch (e: IllegalStateException) {
                CliColor.RED + CliColor.BOLD + "Error: " + CliColor.RESET + CliColor.RED + e.message + CliColor.RESET
            }

            println("${it.name}: $value")
        }
        return CommandResult.SUCCESS
    }
}
