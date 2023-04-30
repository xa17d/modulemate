package at.xa1.modulemate

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.variable.DefaultVariables
import at.xa1.modulemate.command.variable.Variable
import at.xa1.modulemate.command.variable.VariableSet
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
        val firstToken = args.nextOrNull() ?: return Result.InputInvalid("")
        val command = commandList.getOrNull(firstToken)

        return if (command != null) {
            val extendedVariables = VariableSet(variables).apply {
                args.getRemainingArgs().forEachIndexed { index, argsValue ->
                    add(Variable(DefaultVariables.COMMAND_ARG(index)) { argsValue })
                }
            }

            runCommand(command, CommandContext(repository, modules, extendedVariables))
            Result.CommandRun
        } else {
            if (firstToken.isNotEmpty()) {
                val filterApplied =
                    modules.applyFilterIfFindsModules(PathPrefixFilter(firstToken))

                if (filterApplied) {
                    Cli.line(
                        "Couldn't find command: ${CliColor.UNDERLINE}$firstToken${CliColor.RESET}, " +
                            "therefore applied as filter."
                    )

                    return Result.FilterApplied
                }
            }
            return Result.InputInvalid(firstToken)
        }
    }

    private fun runCommand(command: Command, context: CommandContext) {
        Cli.heading("▶️  ${command.name}", formatting = CliColor.BACKGROUND_BRIGHT_BLUE)
        val result = command.run(context)
        when (result) {
            CommandResult.SUCCESS ->
                Cli.heading("🎉 Success!", formatting = CliColor.BACKGROUND_BRIGHT_GREEN)

            CommandResult.FAILURE ->
                Cli.heading("⚠️ Failed!", formatting = CliColor.BACKGROUND_BRIGHT_RED)
        }
    }

    sealed class Result {
        data class InputInvalid(val invalidCommand: String) : Result()
        object CommandRun : Result()
        object FilterApplied : Result()
    }
}
