package at.xa1.modulemate.command

import at.xa1.modulemate.config.*
import at.xa1.modulemate.config.CommandStep
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.system.Shell
import at.xa1.modulemate.system.ShellOpenBrowser

class CommandList(
    val commands: List<Command>
) {
    fun getOrNull(shortcut: String): Command? {
        return commands.find { it.shortcut == shortcut }
    }
}

internal fun createCommandList(
    config: Config,
    browser: ShellOpenBrowser,
    variables: Variables,
    shell: Shell,
    modules: Modules
): CommandList {
    val commandList = CommandList(
        config.commands.map { command ->
            Command(
                shortcut = command.shortcut,
                name = command.name,
                steps = command.steps.map { step ->
                    when (step) {
                        is CommandStep.Browser -> BrowserCommandStep(
                            browser, variables, step.url
                        )

                        is CommandStep.Gradle -> GradleCommandStep(
                            shell = shell,
                            modules = modules,
                            kotlinLibFlags = step.flags.getForKotlinLib(),
                            androidLibFlags = step.flags.getForAndroidLib(),
                            androidAppFlags = step.flags.getForAndroidApp(),
                            kotlinLibTasks = step.tasks.getForKotlinLib(),
                            androidLibTasks = step.tasks.getForAndroidLib(),
                            androidAppTasks = step.tasks.getForAndroidApp()
                        )
                    }
                }
            )
        }
    )
    return commandList
}
