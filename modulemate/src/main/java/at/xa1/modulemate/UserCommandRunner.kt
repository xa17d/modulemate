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
        val firstToken = args.nextOrNull() ?: return Result.INPUT_INVALID
        val command = commandList.getOrNull(firstToken)

        return if (command != null) {
            val extendedVariables = VariableSet(variables).apply {
                args.getRemainingArgs().forEachIndexed { index, argsValue ->
                    add(Variable(DefaultVariables.COMMAND_ARG(index)) { argsValue })
                }
            }

            runCommand(command, CommandContext(repository, modules, extendedVariables))
            Result.COMMAND_RUN
        } else {
            Cli.line(
                "Couldn't find command: ${CliColor.UNDERLINE}$firstToken${CliColor.RESET}, " +
                    "therefore applied as filter."
            )

            if (firstToken.isNotEmpty()) {
                modules.applyFilter(PathPrefixFilter(firstToken))

                if (modules.filteredModules.isNotEmpty()) {
                    return Result.FILTER_APPLIED
                }
            }
            return Result.INPUT_INVALID
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

    enum class Result {
        INPUT_INVALID,
        COMMAND_RUN,
        FILTER_APPLIED
    }
}
