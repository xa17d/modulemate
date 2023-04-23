package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.successToCommandResult
import at.xa1.modulemate.command.variable.replacePlaceholders
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.containsAnyAndroidModule
import at.xa1.modulemate.module.containsAnyOfType
import at.xa1.modulemate.system.Shell
import at.xa1.modulemate.system.isSuccess

class Shell(
    private val mode: ShellMode,
    private val shell: Shell,
    private val command: List<String>
) : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        val modules = context.modules.filteredModules

        val shouldRun = when (mode) {
            ShellMode.RUN_ONCE -> true
            ShellMode.RUN_IF_AT_LEAST_ONE_ANDROID_MODULE -> modules.containsAnyAndroidModule()
            ShellMode.RUN_IF_AT_LEAST_ONE_ANDROID_LIB_MODULE -> modules.containsAnyOfType(ModuleType.ANDROID_LIB)
            ShellMode.RUN_IF_AT_LEAST_ONE_ANDROID_APP_MODULE -> modules.containsAnyOfType(ModuleType.ANDROID_APP)
            ShellMode.RUN_IF_AT_LEAST_ONE_KOTLIN_LIB_MODULE -> modules.containsAnyOfType(ModuleType.KOTLIN_LIB)
            ShellMode.RUN_IF_AT_LEAST_ONE_OTHER_MODULE -> modules.containsAnyOfType(ModuleType.OTHER)
        }

        return if (shouldRun) {
            val result = shell.run(
                command.map { context.variables.replacePlaceholders(it) }.toTypedArray()
            )

            result.isSuccess.successToCommandResult()
        } else {
            CommandResult.SUCCESS
        }
    }
}
enum class ShellMode {
    RUN_ONCE,
    RUN_IF_AT_LEAST_ONE_ANDROID_MODULE,
    RUN_IF_AT_LEAST_ONE_ANDROID_LIB_MODULE,
    RUN_IF_AT_LEAST_ONE_ANDROID_APP_MODULE,
    RUN_IF_AT_LEAST_ONE_KOTLIN_LIB_MODULE,
    RUN_IF_AT_LEAST_ONE_OTHER_MODULE
}
