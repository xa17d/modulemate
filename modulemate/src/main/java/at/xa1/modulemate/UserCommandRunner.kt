package at.xa1.modulemate

import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.variable.Variables
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.filter.PathPrefixFilter

internal class UserCommandRunner(
    private val repository: GitRepository,
    private val modules: Modules,
    private val variables: Variables,
    private val commandList: CommandList
) {
    fun run(args: CliArgs): Result {
        val firstToken = args.nextOrNull() ?: return Result.INPUT_INVALID
        val command = commandList.getOrNull(firstToken)

        return if (command != null) {
            runCommand(command, CommandContext(repository, modules, variables))
            Result.COMMAND_RUN
        } else {
            println("Couldn't find command: $firstToken, therefore applied as filter.")

            modules.applyFilter(PathPrefixFilter(firstToken))

            return if (modules.filteredModules.isNotEmpty()) {
                Result.FILTER_APPLIED
            } else {
                Result.INPUT_INVALID
            }

        }
    }

    private fun runCommand(command: Command, context: CommandContext) {
        print(
            "${CliColor.BACKGROUND_BRIGHT_BLUE}▶️  ${command.name}${CliColor.CLEAR_UNTIL_END_OF_LINE}\n" +
                "${CliColor.RESET}${CliColor.CLEAR_UNTIL_END_OF_LINE}"
        )
        val result = command.run(context)
        when (result) {
            CommandResult.SUCCESS ->
                print(
                    "${CliColor.BACKGROUND_BRIGHT_GREEN}🎉 Success!${CliColor.CLEAR_UNTIL_END_OF_LINE}\n" +
                        "${CliColor.RESET}${CliColor.CLEAR_UNTIL_END_OF_LINE}"
                )

            CommandResult.FAILURE ->
                print(
                    "${CliColor.BACKGROUND_BRIGHT_RED}⚠️ Failed!${CliColor.CLEAR_UNTIL_END_OF_LINE}\n" +
                        "${CliColor.RESET}${CliColor.CLEAR_UNTIL_END_OF_LINE}"
                )
        }
    }

    enum class Result {
        INPUT_INVALID,
        COMMAND_RUN,
        FILTER_APPLIED
    }
}
