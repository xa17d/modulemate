package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.activeModule
import at.xa1.modulemate.command.successToCommandResult
import at.xa1.modulemate.command.variable.replacePlaceholders
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.system.Shell
import at.xa1.modulemate.system.isSuccess
import at.xa1.modulemate.system.run

class Report(
    private val shell: Shell,
    private val pathKotlinLib: String,
    private val pathAndroidLib: String,
    private val pathAndroidApp: String
) : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        val activeModule = context.activeModule

        val path = when (activeModule.type) {
            ModuleType.OTHER -> error("Unknown ModuleType for ${activeModule.path}")
            ModuleType.KOTLIN_LIB -> pathKotlinLib
            ModuleType.ANDROID_LIB -> pathAndroidLib
            ModuleType.ANDROID_APP -> pathAndroidApp
        }

        val result = shell.run("open", context.variables.replacePlaceholders(path))

        return result.isSuccess.successToCommandResult()
    }
}
