package at.xa1.modulemate.command

import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.system.Shell
import at.xa1.modulemate.system.run

class ReportCommandStep(
    private val shell: Shell,
    private val variables: Variables,
    private val modules: Modules,
    private val pathKotlinLib: String,
    private val pathAndroidLib: String,
    private val pathAndroidApp: String,
) : CommandStep {
    override fun run(): Boolean {
        val activeModule = modules.getActiveModule()

        val variable = Variable("ACTIVE_MODULE_PATH") { activeModule.absolutePath }
        variables.add(variable)

        val path = when (activeModule.type) {
            ModuleType.OTHER -> error("Unknown ModuleType for ${activeModule.path}")
            ModuleType.KOTLIN_LIB -> pathKotlinLib
            ModuleType.ANDROID_LIB -> pathAndroidLib
            ModuleType.ANDROID_APP -> pathAndroidApp
        }

        val result = shell.run("open", variables.replacePlaceholders(path))

        variables.remove(variable.name)

        return (result.exitCode == 0)
    }
}
