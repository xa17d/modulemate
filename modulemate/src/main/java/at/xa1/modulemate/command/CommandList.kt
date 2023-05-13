package at.xa1.modulemate.command

import at.xa1.modulemate.command.step.ActiveWork
import at.xa1.modulemate.command.step.Browser
import at.xa1.modulemate.command.step.ChangeFilter
import at.xa1.modulemate.command.step.ConflictAnalysis
import at.xa1.modulemate.command.step.Gradle
import at.xa1.modulemate.command.step.Quit
import at.xa1.modulemate.command.step.Report
import at.xa1.modulemate.command.variable.replacePlaceholders
import at.xa1.modulemate.config.CommandSource
import at.xa1.modulemate.config.CommandStep
import at.xa1.modulemate.config.getForAndroidApp
import at.xa1.modulemate.config.getForAndroidLib
import at.xa1.modulemate.config.getForKotlinLib
import at.xa1.modulemate.config.toShellMode
import at.xa1.modulemate.config.toSuccessCondition
import at.xa1.modulemate.module.filter.ChangedModulesFilter
import at.xa1.modulemate.module.filter.PathPrefixFilter
import at.xa1.modulemate.system.ShellOpenBrowser

class CommandList {
    private val _commands = mutableListOf<Command>()

    val allCommands: List<Command>
        get() = _commands

    fun add(command: Command) {
        _commands.add(command)
    }

    fun addAll(command: Iterable<Command>) {
        _commands.addAll(command)
    }

    fun getOrNull(shortcut: String): Command? {
        return _commands.find { it.shortcuts.contains(shortcut) }
    }
}

internal fun createCommandList(
    commandConfigs: List<CommandSource>,
    browser: ShellOpenBrowser,
    shell: at.xa1.modulemate.system.Shell
): CommandList {
    val commandList = commandConfigs.map { commandSource ->
        val command = commandSource.command

        Command(
            shortcuts = command.shortcuts,
            name = command.name,
            stepConfigs = command.steps.map { step ->
                CommandStepConfig(
                    successCondition = step.runWhen.toSuccessCondition(),
                    step = createCommandStep(step, browser, shell)
                )
            },
            source = commandSource.source
        )
    }

    return CommandList().apply {
        addAll(commandList)
    }
}

private fun createCommandStep(
    step: CommandStep,
    browser: ShellOpenBrowser,
    shell: at.xa1.modulemate.system.Shell
) = when (step) {
    is CommandStep.Browser -> Browser(
        browser = browser,
        urlPattern = step.url
    )

    is CommandStep.Gradle -> Gradle(
        shell = shell,
        kotlinLibFlags = step.flags.getForKotlinLib(),
        androidLibFlags = step.flags.getForAndroidLib(),
        androidAppFlags = step.flags.getForAndroidApp(),
        kotlinLibTasks = step.tasks.getForKotlinLib(),
        androidLibTasks = step.tasks.getForAndroidLib(),
        androidAppTasks = step.tasks.getForAndroidApp()
    )

    is CommandStep.Shell -> at.xa1.modulemate.command.step.Shell(
        mode = step.mode.toShellMode(),
        shell = shell,
        command = step.command
    )

    is CommandStep.Report -> Report(
        shell = shell,
        pathKotlinLib = step.path.getForKotlinLib().singleOrNull()
            ?: error("pathKotlinLib is not unique"),
        pathAndroidLib = step.path.getForAndroidLib().singleOrNull()
            ?: error("pathAndroidLib is not unique"),
        pathAndroidApp = step.path.getForAndroidApp().singleOrNull()
            ?: error("pathAndroidApp is not unique")
    )

    is CommandStep.FilterChangedModules ->
        ChangeFilter { context -> ChangedModulesFilter(context.repository) }

    is CommandStep.FilterPrefix ->
        ChangeFilter { context ->
            val prefix = context.variables.replacePlaceholders(step.prefix)
            PathPrefixFilter(prefix)
        }

    is CommandStep.ActiveWork ->
        ActiveWork()

    is CommandStep.ConflictAnalysis ->
        ConflictAnalysis()

    is CommandStep.Quit ->
        Quit()
}
