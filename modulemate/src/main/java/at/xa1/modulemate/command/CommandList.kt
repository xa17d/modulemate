package at.xa1.modulemate.command

import at.xa1.modulemate.command.step.Browser
import at.xa1.modulemate.command.step.Gradle
import at.xa1.modulemate.command.step.Report
import at.xa1.modulemate.command.variable.Variables
import at.xa1.modulemate.config.CommandStep
import at.xa1.modulemate.config.getForAndroidApp
import at.xa1.modulemate.config.getForAndroidLib
import at.xa1.modulemate.config.getForKotlinLib
import at.xa1.modulemate.config.toShellMode
import at.xa1.modulemate.config.toSuccessCondition
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.system.ShellOpenBrowser

class CommandList {
    private val _commands = mutableListOf<Command>()

    val commands: List<Command>
        get() = _commands

    fun add(command: Command) {
        _commands.add(command)
    }

    fun addAll(command: Iterable<Command>) {
        _commands.addAll(command)
    }

    fun getOrNull(shortcut: String): Command? {
        return _commands.find { it.shortcut == shortcut }
    }
}

internal fun createCommandList(
    commandConfigs: List<at.xa1.modulemate.config.Command>,
    browser: ShellOpenBrowser,
    variables: Variables,
    shell: at.xa1.modulemate.system.Shell,
    modules: Modules
): CommandList {
    val commandList = commandConfigs.map { command ->
        Command(
            shortcut = command.shortcut,
            name = command.name,
            stepConfigs =
            command.steps.map { step ->
                CommandStepConfig(
                    successCondition = step.runWhen.toSuccessCondition(),
                    step = createCommandStep(step, browser, variables, shell, modules)
                )
            }
        )
    }

    return CommandList().apply {
        addAll(commandList)
    }
}

private fun createCommandStep(
    step: CommandStep,
    browser: ShellOpenBrowser,
    variables: Variables,
    shell: at.xa1.modulemate.system.Shell,
    modules: Modules
) = when (step) {
    is CommandStep.Browser -> Browser(
        browser = browser,
        variables = variables,
        urlPattern = step.url
    )

    is CommandStep.Gradle -> Gradle(
        shell = shell,
        modules = modules,
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
        modulesInput = modules,
        variables = variables,
        command = step.command
    )

    is CommandStep.Report -> Report(
        shell = shell,
        variables = variables,
        modules = modules,
        pathKotlinLib = step.path.getForKotlinLib().singleOrNull()
            ?: error("pathKotlinLib is not unique"),
        pathAndroidLib = step.path.getForAndroidLib().singleOrNull()
            ?: error("pathAndroidLib is not unique"),
        pathAndroidApp = step.path.getForAndroidApp().singleOrNull()
            ?: error("pathAndroidApp is not unique")
    )
}
