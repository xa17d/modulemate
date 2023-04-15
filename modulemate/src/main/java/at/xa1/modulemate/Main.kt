package at.xa1.modulemate

import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliColor.BACKGROUND_BRIGHT_BLUE
import at.xa1.modulemate.cli.CliColor.BACKGROUND_BRIGHT_GREEN
import at.xa1.modulemate.cli.CliColor.BACKGROUND_BRIGHT_RED
import at.xa1.modulemate.cli.CliColor.BACKGROUND_BRIGHT_YELLOW
import at.xa1.modulemate.cli.CliColor.BACKGROUND_RED
import at.xa1.modulemate.cli.CliColor.BLUE
import at.xa1.modulemate.cli.CliColor.BOLD
import at.xa1.modulemate.cli.CliColor.CLEAR_UNTIL_END_OF_LINE
import at.xa1.modulemate.cli.CliColor.CYAN
import at.xa1.modulemate.cli.CliColor.GREEN
import at.xa1.modulemate.cli.CliColor.RESET
import at.xa1.modulemate.cli.CliColor.UNDERLINE
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.Variables
import at.xa1.modulemate.command.addDefault
import at.xa1.modulemate.command.createCommandList
import at.xa1.modulemate.config.ConfigResolver
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.ModulesScanner
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
        "$BOLD$BACKGROUND_RED \uD83E\uDDF0 modulemate " +
            "〉${repository.getRemoteOrigin().repositoryName} 〉${repository.getBranch()}" +
            "$CLEAR_UNTIL_END_OF_LINE\n$RESET$CLEAR_UNTIL_END_OF_LINE"
    )

    val config = ConfigResolver(repositoryRoot).getConfig()
    val modules = Modules(
        scanner = ModulesScanner(config.module.classification, repositoryRoot)
    )
    modules.filter = filter
    val browser = ShellOpenBrowser(shell)
    val variables = Variables().apply {
        addDefault(repository)
    }
    val commandList = createCommandList(config, browser, variables, printingShell, modules)

    val shortcutOrFilter = cliArgs.nextOrNull()
    val shortcutCommand = if (shortcutOrFilter == null) {
        null
    } else {
        commandList.getOrNull(shortcutOrFilter)
    }

    if (shortcutCommand != null) {
        print(
            "$UNDERLINE$BOLD${shortcutCommand.shortcut}:$RESET " +
                "${shortcutCommand.name}$CLEAR_UNTIL_END_OF_LINE\n$RESET"
        )
        runCommand(shortcutCommand)
    } else {
        modules.filter = shortcutOrFilter

        println("${UNDERLINE}Modules:$RESET")
        modules.filteredModules.forEach { module ->
            val formatting = when (module.type) {
                ModuleType.KOTLIN_LIB -> BLUE
                ModuleType.ANDROID_LIB -> GREEN
                ModuleType.ANDROID_APP -> CYAN
                ModuleType.OTHER -> ""
            }
            println("$formatting ${module.path}$RESET")
        }

        var lastCommand = ""
        while (true) {
            print("$BLUE〉$CLEAR_UNTIL_END_OF_LINE")
            val input = readln()
            print(RESET)
            val shortcut = when (input) {
                "" -> {
                    if (lastCommand == "") {
                        break
                    }
                    println("Repeat: ${UNDERLINE}$lastCommand$RESET\n")
                    lastCommand
                }

                "q" -> break
                else -> input
            }
            lastCommand = shortcut
            val command = commandList.getOrNull(shortcut)
            if (command == null) {
                print(
                    "${BACKGROUND_BRIGHT_YELLOW}⚠️ Command unknown: $shortcut$CLEAR_UNTIL_END_OF_LINE\n" +
                        "$RESET$CLEAR_UNTIL_END_OF_LINE" +
                        "${UNDERLINE}Available Commands:$RESET\n"
                )

                commandList.commands.forEach { availableCommand ->
                    println(
                        "$UNDERLINE$BOLD${availableCommand.shortcut}$RESET: ${availableCommand.name}"
                    )
                }

            } else {
                runCommand(command)
            }
        }
    }

    println("👋 bye")
}

fun runCommand(command: Command) {
    print("${BACKGROUND_BRIGHT_BLUE}▶️  ${command.name}$CLEAR_UNTIL_END_OF_LINE\n$RESET$CLEAR_UNTIL_END_OF_LINE")
    val success = command.run()
    if (success) {
        print("${BACKGROUND_BRIGHT_GREEN}🎉 Success!$CLEAR_UNTIL_END_OF_LINE\n$RESET$CLEAR_UNTIL_END_OF_LINE")
    } else {
        print("${BACKGROUND_BRIGHT_RED}⚠️ Failed!$CLEAR_UNTIL_END_OF_LINE\n$RESET$CLEAR_UNTIL_END_OF_LINE")
    }
}
