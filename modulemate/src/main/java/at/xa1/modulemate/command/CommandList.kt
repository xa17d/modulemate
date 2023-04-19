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
                            runWhen = step.runWhen.toRunWhen(),
                            browser = browser,
                            variables = variables,
                            urlPattern = step.url,
                        )

                        is CommandStep.Gradle -> GradleCommandStep(
                            runWhen = step.runWhen.toRunWhen(),
                            shell = shell,
                            modules = modules,
                            kotlinLibFlags = step.flags.getForKotlinLib(),
                            androidLibFlags = step.flags.getForAndroidLib(),
                            androidAppFlags = step.flags.getForAndroidApp(),
                            kotlinLibTasks = step.tasks.getForKotlinLib(),
                            androidLibTasks = step.tasks.getForAndroidLib(),
                            androidAppTasks = step.tasks.getForAndroidApp()
                        )

                        is CommandStep.Shell -> ShellCommandStep(
                            runWhen = step.runWhen.toRunWhen(),
                            shell = shell,
                            variables = variables,
                            command = step.command,
                        )

                        is CommandStep.Report -> ReportCommandStep(
                            runWhen = step.runWhen.toRunWhen(),
                            shell = shell,
                            variables = variables,
                            modules = modules,
                            pathKotlinLib = step.path.getForKotlinLib().singleOrNull()
                                ?: error("pathKotlinLib is not unique"),
                            pathAndroidLib = step.path.getForAndroidLib().singleOrNull()
                                ?: error("pathAndroidLib is not unique"),
                            pathAndroidApp = step.path.getForAndroidApp().singleOrNull()
                                ?: error("pathAndroidApp is not unique"),
                        )
                    }
                }
            )
        }
    )
    return commandList
}
