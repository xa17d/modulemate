package at.xa1.modulemate

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.cli.CliArgs
import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.cli.CliColor.BACKGROUND_WHITE
import at.xa1.modulemate.cli.CliColor.BLACK
import at.xa1.modulemate.cli.CliColor.BLUE
import at.xa1.modulemate.cli.CliColor.CYAN
import at.xa1.modulemate.cli.CliColor.GREEN
import at.xa1.modulemate.cli.CliColor.RESET
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.command.findMostSimilarCommand
import at.xa1.modulemate.command.variable.CachedVariables
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.filter.AllModulesFilter
import at.xa1.modulemate.module.filter.ChangedModulesFilter
import at.xa1.modulemate.module.filter.EmptyFilter

internal class PromptMode(
    private val repository: GitRepository,
    private val modules: Modules,
    private val variables: CachedVariables,
    private val commandRunner: UserCommandRunner,
    private val commandList: CommandList
) {
    private var lastFilteredModules: List<Module>? = null
    private var lastCommand = ""
    fun run() {
        applySensibleFilter()

        while (true) {
            cycle()
            variables.clearCache()
        }
    }

    private fun applySensibleFilter() {
        if (modules.filter is EmptyFilter) {
            modules.applyFilter(ChangedModulesFilter(repository))

            if (modules.filteredModules.isEmpty()) {
                modules.applyFilter(AllModulesFilter())
            }
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
            val mostSimilarCommand = findMostSimilarCommand(
                commandList.allCommands.map { it.shortcut },
                input
            )

            Cli.heading("⚠️ Command unknown: $input", formatting = CliColor.BACKGROUND_BRIGHT_YELLOW)
            Cli.line(
                "Did you mean $GREEN$mostSimilarCommand$RESET? " +
                    "(if yes, press $BACKGROUND_WHITE$BLACK Enter $RESET$RESET)"
            )
            lastCommand = mostSimilarCommand
        }
    }

    private fun readInputOrLast(): String {
        val input = Cli.prompt(lastCommand)
        return if (input == "") {
            Cli.line("Repeat: ${CliColor.UNDERLINE}$lastCommand$RESET")
            lastCommand
        } else {
            lastCommand = input
            input
        }
    }
}

fun Cli.module(module: Module, indent: String = "  ") {
    val formatting = when (module.type) {
        ModuleType.KOTLIN_LIB -> BLUE
        ModuleType.ANDROID_LIB -> GREEN
        ModuleType.ANDROID_APP -> CYAN
        ModuleType.OTHER -> ""
    }
    line("$indent$formatting${module.path}$RESET")
}

private fun printModules(modules: Modules) {
    val androidApps = modules.filteredModules.count { it.type == ModuleType.ANDROID_APP }
    val androidLibs = modules.filteredModules.count { it.type == ModuleType.ANDROID_LIB }
    val kotlinLibs = modules.filteredModules.count { it.type == ModuleType.KOTLIN_LIB }
    val others = modules.filteredModules.size - androidApps - androidLibs - kotlinLibs

    val moduleSummary = "filter: ${modules.filter.name}; " +
        "${CYAN}androidApps: $androidApps$RESET; " +
        "${GREEN}androidLibs: $androidLibs; " +
        "${BLUE}kotlinLibs: $kotlinLibs$RESET; " +
        "others: $others$RESET"

    Cli.subHeading("Modules$RESET ($moduleSummary):")
    modules.filteredModules.forEach { module ->
        Cli.module(module)
    }
}
