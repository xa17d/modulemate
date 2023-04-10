package at.xa1.modulemate

import at.xa1.modulemate.command.BrowserCommand
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.command.Variables
import at.xa1.modulemate.command.addDefault
import at.xa1.modulemate.config.ConfigResolver
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.ModulesScanner
import at.xa1.modulemate.system.RuntimeShell
import at.xa1.modulemate.system.ShellOpenBrowser
import java.io.File

fun main(args: Array<String>) {
    println("\uD83E\uDDF0 modulemate")

    val cliArgs = CliArgs(args)

    val shell = RuntimeShell(File("."))
    val folder = File(cliArgs.getValueOrNull("--repository") ?: ".")
    val repository = GitRepository(shell, folder)
    val repositoryRoot = repository.getRepositoryRoot()

    println("Repository: ${repositoryRoot.canonicalFile.absolutePath}")
    println("Name:       ${repository.getRemoteOrigin().repositoryName}")
    println("Branch:     ${repository.getBranch()}")

    val config = ConfigResolver(repositoryRoot).getConfig()

    val modules = ModulesScanner(config.module.classification).scan(repository.getRepositoryRoot())

    println("Modules:    $modules")

    val shortcut = cliArgs.nextOrNull() ?: return
    val browser = ShellOpenBrowser(shell)
    val variables = Variables().apply {
        addDefault(repository)
    }

    val commandList = CommandList(
        listOf(
            BrowserCommand(
                "pr",
                browser,
                variables,
                "https://{GIT_HOST}/{GIT_OWNER}/{GIT_REPOSITORY_NAME}/pull/{GIT_BRANCH_NAME}"
            )
        )
    )

    val command = commandList.runOrNull(shortcut)
    if (command == null) {
        error("Cannot find command with shortcut: ${shortcut}")
    }
}
