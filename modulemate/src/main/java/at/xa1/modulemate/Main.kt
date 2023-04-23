package at.xa1.modulemate

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliColor.BACKGROUND_RED
import at.xa1.modulemate.cli.CliColor.BOLD
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandStepConfig
import at.xa1.modulemate.command.StepSuccessCondition
import at.xa1.modulemate.command.createCommandList
import at.xa1.modulemate.command.step.Help
import at.xa1.modulemate.command.variable.CachedVariables
import at.xa1.modulemate.command.variable.DefaultVariables
import at.xa1.modulemate.config.ConfigMerger
import at.xa1.modulemate.config.ConfigResolver
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.RepositoryModulesScanner
import at.xa1.modulemate.module.filter.ChangedModulesFilter
import at.xa1.modulemate.module.filter.PathPrefixFilter
import at.xa1.modulemate.system.PrintingShell
import at.xa1.modulemate.system.RuntimeShell
import at.xa1.modulemate.system.ShellOpenBrowser
import java.io.File

fun main(args: Array<String>) {
    val cliArgs = CliArgs(args)

    val folder = File(cliArgs.getValueOrDefault("--repository", "."))
    val prefixFilter = cliArgs.getValueOrNull("--prefixFilter")
    val shell = RuntimeShell(folder)
    val printingShell = PrintingShell(folder)
    val repository = GitRepository(shell, folder)
    val repositoryRoot = repository.getRepositoryRoot()

    Cli.heading(
        "\uD83E\uDDF0 modulemate v${Modulemate.VERSION} " +
            "〉${repository.getRemoteOrigin().repositoryName} 〉${repository.getBranch()}",
        formatting = "$BOLD$BACKGROUND_RED"
    )

    val configList = ConfigResolver(repositoryRoot).getConfigs()
    val configMerger = ConfigMerger(configList)
    val modules = Modules(
        scanner = RepositoryModulesScanner(configMerger.getModuleClassification(), repositoryRoot)
    )
    if (prefixFilter == null) {
        modules.applyFilter(ChangedModulesFilter(repository))
    } else {
        modules.applyFilter(PathPrefixFilter(prefixFilter))
    }
    val browser = ShellOpenBrowser(shell)
    val defaultVariables = DefaultVariables.create(repository, modules)
    val variables = CachedVariables(defaultVariables)
    val commandList = createCommandList(
        configMerger.getCommandConfigs(),
        browser,
        printingShell
    )
    commandList.add(
        Command(
            Help.SHORTCUT,
            "Help",
            listOf(CommandStepConfig(StepSuccessCondition.PREVIOUS_SUCCESS, Help(commandList)))
        )
    )

    val commandRunner = UserCommandRunner(
        repository = repository,
        modules = modules,
        variables = variables,
        commandList = commandList
    )

    val filterBeforeCommand = modules.filter

    val promptMode = when (commandRunner.run(cliArgs)) {
        UserCommandRunner.Result.COMMAND_RUN ->
            modules.filter != filterBeforeCommand

        UserCommandRunner.Result.INPUT_INVALID -> true // TODO show error
        UserCommandRunner.Result.FILTER_APPLIED -> true
    }

    if (promptMode) {
        PromptMode(modules, variables, commandRunner).run()
    }

    Cli.line("👋 bye")
}
