package at.xa1.modulemate

import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.command.step.Help
import at.xa1.modulemate.command.variable.CachedVariables
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.Modules

internal class PromptMode(
    private val modules: Modules,
    private val variables: CachedVariables,
    private val commandRunner: UserCommandRunner
) {
    private var lastFilteredModules: List<Module>? = null
    private var lastCommand = ""
    fun run() {
        while (true) {
            cycle()
            variables.clearCache()
        }
    }

    private fun cycle() {
        if (lastFilteredModules != modules.filteredModules) {
            lastFilteredModules = modules.filteredModules
            printModules(modules)
        }

        print("${CliColor.BLUE}[${CliColor.GREEN}$lastCommand${CliColor.BLUE}] 〉${CliColor.CLEAR_UNTIL_END_OF_LINE}")
        val input = readInputOrLast()
        print(CliColor.RESET)

        val inputTokens = input.split(' ').toTypedArray() // TODO properly tokenize
        val result = commandRunner.run(CliArgs(inputTokens))
        if (result == UserCommandRunner.Result.INPUT_INVALID) {
            print(
                "${CliColor.BACKGROUND_BRIGHT_YELLOW}⚠️ Command unknown: $input${CliColor.CLEAR_UNTIL_END_OF_LINE}\n" +
                    "${CliColor.RESET}${CliColor.CLEAR_UNTIL_END_OF_LINE}"
            )
            commandRunner.run(CliArgs(arrayOf(Help.SHORTCUT)))
            lastCommand = Help.SHORTCUT
        }
    }

    private fun readInputOrLast(): String {
        val input = readln()
        return if (input == "") {
            lastCommand
        } else {
            lastCommand = input
            println("Repeat: ${CliColor.UNDERLINE}$lastCommand${CliColor.RESET}\n")
            input
        }
    }
}

private fun printModules(modules: Modules) {
    println("${CliColor.UNDERLINE}Modules:${CliColor.RESET}")
    modules.filteredModules.forEach { module ->
        val formatting = when (module.type) {
            ModuleType.KOTLIN_LIB -> CliColor.BLUE
            ModuleType.ANDROID_LIB -> CliColor.GREEN
            ModuleType.ANDROID_APP -> CliColor.CYAN
            ModuleType.OTHER -> ""
        }
        println("$formatting ${module.path}${CliColor.RESET}")
    }
}
