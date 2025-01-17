package at.xa1.modulemate

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliEmoji
import at.xa1.modulemate.cli.CliFormat
import at.xa1.modulemate.cli.CliFormat.BACKGROUND_RED
import at.xa1.modulemate.cli.CliFormat.BOLD
import at.xa1.modulemate.cli.CliFormat.RESET
import at.xa1.modulemate.cli.CliFormat.YELLOW
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandStepConfig
import at.xa1.modulemate.command.StepSuccessCondition
import at.xa1.modulemate.command.createCommandList
import at.xa1.modulemate.command.step.Help
import at.xa1.modulemate.command.step.KeyTest
import at.xa1.modulemate.command.step.QuitException
import at.xa1.modulemate.command.variable.CachedVariables
import at.xa1.modulemate.command.variable.DefaultVariables
import at.xa1.modulemate.config.ConfigMerger
import at.xa1.modulemate.config.ConfigResolver
import at.xa1.modulemate.config.Source
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.mode.LiveModeRootCoordinator
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.RepositoryModulesScanner
import at.xa1.modulemate.module.filter.PathPrefixFilter
import at.xa1.modulemate.system.PrintingShell
import at.xa1.modulemate.system.RuntimeShell
import at.xa1.modulemate.system.ShellOpenBrowser
import at.xa1.modulemate.ui.CleanExit
import at.xa1.modulemate.ui.Ui
import java.io.File

fun main(args: Array<String>) {
    val cliArgs = CliArgs(args)

    val folder = File(cliArgs.getValueOrDefault("--repository", "."))
    val shell = RuntimeShell(folder)
    val repository = GitRepository(shell, folder)

    val repositoryRoot =
        try {
            repository.getRepositoryRoot()
        } catch (e: IllegalStateException) {
            errorNoGitRepository(folder)
            return
        }

    modulemate(repository, repositoryRoot, shell, cliArgs)
}

private fun header(
    repoName: String,
    branch: String,
) {
    Cli.heading(
        "${CliEmoji.TOOLBOX} modulemate v${Modulemate.VERSION} " +
            "〉$repoName 〉$branch",
        formatting = "$BOLD$BACKGROUND_RED",
    )
}

private fun modulemate(
    repository: GitRepository,
    repositoryRoot: File,
    shell: RuntimeShell,
    cliArgs: CliArgs,
) {
    val prefixFilter = cliArgs.getValueOrNull("--prefixFilter")
    val debugMode = cliArgs.getBooleanOrNull("--debug") ?: false
    val printingShell = PrintingShell(repositoryRoot)

    header(repository.getRemoteOrigin().repositoryName, repository.getBranch())

    val configList = ConfigResolver(repositoryRoot).getConfigs()
    val configMerger = ConfigMerger(configList)
    val modules =
        Modules(
            scanner = RepositoryModulesScanner(configMerger.getModuleClassification(), repositoryRoot),
        )
    if (prefixFilter != null) {
        modules.applyFilter(PathPrefixFilter(prefixFilter))
    }
    val browser = ShellOpenBrowser(shell)
    val defaultVariables = DefaultVariables.create(repository, modules, configMerger.getVariablesConfig())
    val variables = CachedVariables(defaultVariables)
    val commandList =
        createCommandList(
            configMerger.getCommandsWithSource(),
            browser,
            printingShell,
        )

    val ui by lazy { // only initialize when interactive prompt mode is actually entered.
        val ui = Ui.init()
        CleanExit().setup(ui)
        ui
    }

    commandList.add(
        Command(
            Help.SHORTCUTS,
            "Help",
            listOf(CommandStepConfig(StepSuccessCondition.PREVIOUS_SUCCESS, Help(commandList))),
            Source.BuiltIn,
        ),
    )

    if (debugMode) {
        commandList.add(
            Command(
                KeyTest.SHORTCUTS,
                "Debug and Test Input Keys",
                listOf(CommandStepConfig(StepSuccessCondition.PREVIOUS_SUCCESS, KeyTest { ui })),
                Source.BuiltIn,
            ),
        )
    }

    val commandRunner =
        UserCommandRunner(
            repository = repository,
            modules = modules,
            variables = variables,
            commandList = commandList,
        )

    try {
        val filterBeforeCommand = modules.filter

        val promptMode =
            when (val result = commandRunner.run(cliArgs)) {
                UserCommandRunner.Result.CommandRun ->
                    modules.filter != filterBeforeCommand // enter prompt mode when filter changed.

                is UserCommandRunner.Result.InputInvalid -> {
                    val empty = result.invalidCommand.isEmpty()
                    if (!empty) {
                        Cli.heading(
                            "${CliEmoji.WARNING_SIGN} Command unknown: ${result.invalidCommand}",
                            formatting = CliFormat.BACKGROUND_YELLOW,
                        )
                    }
                    empty
                }

                UserCommandRunner.Result.FilterApplied -> true
            }

        if (promptMode) {
            LiveModeRootCoordinator(ui, modules, commandList, commandRunner).run()
        }
    } catch (_: QuitException) {
    }

    Cli.line("${CliEmoji.WAVING_HAND} bye")
}

private fun errorNoGitRepository(folder: File) {
    header(folder.canonicalFile.name, "no git")
    Cli.line(
        "Folder is not within a git repository. " +
            "modulemate requires a git repository. " +
            "Please navigate to a git repository and try again.",
    )
    Cli.line("current folder: $YELLOW${folder.canonicalFile.absolutePath}$RESET")
}
