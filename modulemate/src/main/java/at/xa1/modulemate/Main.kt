package at.xa1.modulemate

import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliColor.BACKGROUND_RED
import at.xa1.modulemate.cli.CliColor.BOLD
import at.xa1.modulemate.cli.CliColor.CLEAR_UNTIL_END_OF_LINE
import at.xa1.modulemate.cli.CliColor.RESET
import at.xa1.modulemate.command.createCommandList
import at.xa1.modulemate.command.variable.CachedVariables
import at.xa1.modulemate.command.variable.DefaultVariables
import at.xa1.modulemate.config.ConfigMerger
import at.xa1.modulemate.config.ConfigResolver
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.RepositoryModulesScanner
import at.xa1.modulemate.module.filter.PathPrefixFilter
import at.xa1.modulemate.system.PrintingShell
import at.xa1.modulemate.system.RuntimeShell
import at.xa1.modulemate.system.ShellOpenBrowser
import java.io.File

fun main(args: Array<String>) {
    val cliArgs = CliArgs(args)

    val folder = File(cliArgs.getValueOrDefault("--repository", "."))
    val filter = cliArgs.getValueOrNull("--filter")
    val shell = RuntimeShell(folder)
    val printingShell = PrintingShell(folder)
    val repository = GitRepository(shell, folder)
    val repositoryRoot = repository.getRepositoryRoot()

    print(
        "$BOLD$BACKGROUND_RED \uD83E\uDDF0 modulemate v${Modulemate.VERSION} " +
            "〉${repository.getRemoteOrigin().repositoryName} 〉${repository.getBranch()}" +
            "$CLEAR_UNTIL_END_OF_LINE\n$RESET$CLEAR_UNTIL_END_OF_LINE"
    )

    val configList = ConfigResolver(repositoryRoot).getConfigs()
    val configMerger = ConfigMerger(configList)
    val modules = Modules(
        scanner = RepositoryModulesScanner(configMerger.getModuleClassification(), repositoryRoot)
    )
    if (filter != null) {
        modules.applyFilter(PathPrefixFilter(filter))
    }
    val browser = ShellOpenBrowser(shell)
    val defaultVariables = DefaultVariables.create(repository, modules)
    val variables = CachedVariables(defaultVariables)
    val commandList = createCommandList(
        configMerger.getCommandConfigs(),
        browser,
        printingShell
    )

    val commandRunner = UserCommandRunner(
        repository = repository,
        modules = modules,
        variables = variables,
        commandList = commandList
    )

    when (val initialCommandResult = commandRunner.run(cliArgs)) {
        UserCommandRunner.Result.COMMAND_RUN -> {
            // do nothing and exit
        }
        UserCommandRunner.Result.INPUT_INVALID,
        UserCommandRunner.Result.FILTER_APPLIED -> {
            if (initialCommandResult == UserCommandRunner.Result.INPUT_INVALID) {
                // TODO show error
            }

            PromptMode(modules, variables, commandRunner).run()
        }
    }

    println("👋 bye")
}
