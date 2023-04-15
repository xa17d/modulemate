package at.xa1.modulemate

import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.command.*
import at.xa1.modulemate.config.CommandStep
import at.xa1.modulemate.config.ConfigResolver
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.ModulesScanner
import at.xa1.modulemate.system.PrintingShell
import at.xa1.modulemate.system.RuntimeShell
import at.xa1.modulemate.system.ShellOpenBrowser
import java.io.File
import java.time.Duration
import java.time.Instant

fun main(args: Array<String>) {
    println("\uD83E\uDDF0 modulemate")

    val cliArgs = CliArgs(args)

    val folder = File(cliArgs.getValueOrDefault("--repository", "."))
    val filter = cliArgs.getValueOrDefault("--filter", "")
    val shell = RuntimeShell(folder)
    val printingShell = PrintingShell(folder)
    val repository = GitRepository(shell, folder)
    val repositoryRoot = repository.getRepositoryRoot()

    println("Repository: ${repositoryRoot.canonicalFile.absolutePath}")
    println("Name:       ${repository.getRemoteOrigin().repositoryName}")
    println("Branch:     ${repository.getBranch()}")

    val config = ConfigResolver(repositoryRoot).getConfig()

    val allModules = ModulesScanner(config.module.classification).scan(repository.getRepositoryRoot())

    println("All Modules: $allModules")

    val browser = ShellOpenBrowser(shell)
    val variables = Variables().apply {
        addDefault(repository)
    }

    val modules = Modules(allModules.modules.filter { it.relativePath.startsWith(filter) })

    println("Modules:    $modules")
    modules.modules.forEach { module ->
        val formatting = when (module.type) {
            ModuleType.OTHER -> CliColor.CYAN
            ModuleType.JAVA_LIB -> CliColor.BLUE
            ModuleType.ANDROID_LIB -> CliColor.GREEN
            ModuleType.ANDROID_APP -> CliColor.YELLOW
        }

        println("  $formatting ${module.path}${CliColor.RESET}")
    }

    val commandList = CommandList(
        config.commands.map { command ->
            Command(
                shortcut = command.shortcut,
                steps = command.steps.map { step ->
                    when (step) {
                        is CommandStep.Browser -> BrowserCommandStep(
                            browser, variables, step.url
                        )

                        is CommandStep.Gradle -> GradleCommandStep(
                            shell = printingShell,
                            modules = modules,
                            flags = step.flags,
                            javaLibraryTasks = step.tasks + step.javaLibTasks,
                            androidLibTasks = step.tasks + step.androidTasks + step.androidLibTasks,
                            androidAppTasks = step.tasks + step.androidTasks + step.androidAppTasks
                        )
                    }
                }
            )
        }
    )

    var shortcut = cliArgs.nextOrNull() ?: ""
    while (true) {
        val command = commandList.getOrNull(shortcut)
        if (command == null) {
            println("${CliColor.RED} Cannot find command with shortcut:${CliColor.RESET} $shortcut")
        } else {
            val success = command.run()
            if (success) {
                println("🎉 ${CliColor.GREEN}Success!${CliColor.RESET}")
            } else {
                println("⚠️ ${CliColor.YELLOW}Failed!${CliColor.RESET}") // TODO unify emojis
            }
        }

        print("${CliColor.BLUE}# ")
        shortcut = readln()
        print(CliColor.RESET)

        // TODO variables.clearCache()

        if (shortcut == "exit") {
            break
        }
    }
}
