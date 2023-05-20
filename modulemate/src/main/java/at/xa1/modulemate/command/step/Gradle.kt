package at.xa1.modulemate.command.step

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.successToCommandResult
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.system.Shell
import at.xa1.modulemate.system.isSuccess

class Gradle(
    private val shell: Shell,
    private val richConsole: Boolean,
    private val kotlinLibFlags: List<String>,
    private val androidLibFlags: List<String>,
    private val androidAppFlags: List<String>,
    private val kotlinLibTasks: List<String>,
    private val androidLibTasks: List<String>,
    private val androidAppTasks: List<String>,
) : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        val modules = context.modules.activeModules
        val flags = mutableSetOf<String>()

        if (richConsole) {
            flags.add("--console=rich")
        }

        modules.forEach { module ->
            when (module.type) {
                ModuleType.KOTLIN_LIB -> flags.addAll(kotlinLibFlags)
                ModuleType.ANDROID_LIB -> flags.addAll(androidLibFlags)
                ModuleType.ANDROID_APP -> flags.addAll(androidAppFlags)
                ModuleType.OTHER -> {}
            }
        }

        val command = listOf("./gradlew") +
            flags +
            modules.flatMap { module ->
                val tasks = when (module.type) {
                    ModuleType.OTHER -> emptyList()
                    ModuleType.KOTLIN_LIB -> kotlinLibTasks
                    ModuleType.ANDROID_LIB -> androidLibTasks
                    ModuleType.ANDROID_APP -> androidAppTasks
                }

                tasks.map { task -> module.path + ":" + task }
            }

        Cli.stepCommand(command.joinToString(separator = " "))

        val result = shell.run(command.toTypedArray())
        return result.isSuccess.successToCommandResult()
    }
}
