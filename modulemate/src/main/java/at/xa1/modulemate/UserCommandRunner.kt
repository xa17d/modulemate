package at.xa1.modulemate

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliEmoji
import at.xa1.modulemate.cli.CliFormat
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.variable.CachedVariables
import at.xa1.modulemate.command.variable.DefaultVariables
import at.xa1.modulemate.command.variable.Variable
import at.xa1.modulemate.command.variable.VariableSet
import at.xa1.modulemate.config.Source
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.filter.PathPrefixFilter

internal class UserCommandRunner(
    private val repository: GitRepository,
    private val modules: Modules,
    private val variables: CachedVariables,
    private val commandList: CommandList
) {
    fun run(command: Command, args: List<String> = emptyList()): Result {
        val extendedVariables = VariableSet(variables).apply {
            args.forEachIndexed { index, argsValue ->
                add(Variable(DefaultVariables.COMMAND_ARG(index)) { argsValue })
            }
            add(
                Variable(DefaultVariables.CONFIG_FOLDER) {
                    when (val source = command.source) {
                        Source.BuiltIn -> error("Only available for commands defined in config files.")
                        is Source.ConfigFile -> source.file.parentFile.absolutePath
                    }
                }
            )
        }

        runCommand(command, CommandContext(repository, modules, extendedVariables))
        variables.clearCache()
        return Result.CommandRun
    }

    fun run(args: CliArgs): Result {
        val firstToken = args.nextOrNull() ?: return Result.InputInvalid("")
        val command = commandList.getOrNull(firstToken)

        return if (command != null) {
            run(command, args.getRemainingArgs())
        } else {
            if (firstToken.isNotEmpty()) {
                val filterApplied =
                    modules.applyFilterIfFindsModules(PathPrefixFilter(firstToken))

                if (filterApplied) {
                    Cli.line(
                        "Couldn't find command: ${CliFormat.UNDERLINE}$firstToken${CliFormat.RESET}, " +
                            "therefore applied as filter."
                    )

                    return Result.FilterApplied
                }
            }
            return Result.InputInvalid(firstToken)
        }
    }

    private fun runCommand(command: Command, context: CommandContext) {
        Cli.heading("${CliEmoji.PLAY_BUTTON} ${command.name}", formatting = CliFormat.BACKGROUND_BRIGHT_BLUE)
        val result = command.run(context)
        when (result) {
            CommandResult.SUCCESS ->
                Cli.heading("${CliEmoji.PARTY_POPPER} Success!", formatting = CliFormat.BACKGROUND_BRIGHT_GREEN)

            CommandResult.FAILURE ->
                Cli.heading("${CliEmoji.WARNING_SIGN} Failed!", formatting = CliFormat.BACKGROUND_BRIGHT_RED)
        }
    }

    sealed class Result {
        data class InputInvalid(val invalidCommand: String) : Result()
        object CommandRun : Result()
        object FilterApplied : Result()
    }
}
