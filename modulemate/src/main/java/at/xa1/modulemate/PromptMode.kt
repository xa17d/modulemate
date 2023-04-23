package at.xa1.modulemate

import at.xa1.modulemate.cli.Cli
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

        val input = readInputOrLast()

        val inputTokens = input.split(' ').toTypedArray() // TODO properly tokenize
        val result = commandRunner.run(CliArgs(inputTokens))
        if (result == UserCommandRunner.Result.INPUT_INVALID) {
            Cli.heading("⚠️ Command unknown: $input", formatting = CliColor.BACKGROUND_BRIGHT_YELLOW)
            commandRunner.run(CliArgs(arrayOf(Help.SHORTCUT)))
            lastCommand = Help.SHORTCUT
        }
    }

    private fun readInputOrLast(): String {
        val input = Cli.prompt(lastCommand)
        return if (input == "") {
            lastCommand
        } else {
            lastCommand = input
            Cli.line("Repeat: ${CliColor.UNDERLINE}$lastCommand${CliColor.RESET}")
            input
        }
    }
}

fun Cli.module(module: Module, indent: String = "  ") {
    val formatting = when (module.type) {
        ModuleType.KOTLIN_LIB -> CliColor.BLUE
        ModuleType.ANDROID_LIB -> CliColor.GREEN
        ModuleType.ANDROID_APP -> CliColor.CYAN
        ModuleType.OTHER -> ""
    }
    line("$indent$formatting${module.path}${CliColor.RESET}")
}
private fun printModules(modules: Modules) {
    Cli.subHeading("Modules:")
    modules.filteredModules.forEach { module ->
        Cli.module(module)
    }
}
