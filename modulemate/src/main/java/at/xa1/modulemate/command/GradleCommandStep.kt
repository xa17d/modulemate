package at.xa1.modulemate.command

import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.system.Shell

class GradleCommandStep(
    private val shell: Shell,
    private val modules: Modules,
    private val kotlinLibFlags: List<String>,
    private val androidLibFlags: List<String>,
    private val androidAppFlags: List<String>,
    private val kotlinLibTasks: List<String>,
    private val androidLibTasks: List<String>,
    private val androidAppTasks: List<String>,
) : CommandStep {
    override fun run(): Boolean {

        val flags = mutableSetOf<String>()
        modules.filteredModules.forEach { module ->
            when (module.type) {
                ModuleType.KOTLIN_LIB -> flags.addAll(kotlinLibFlags)
                ModuleType.ANDROID_LIB -> flags.addAll(androidLibFlags)
                ModuleType.ANDROID_APP -> flags.addAll(androidAppFlags)
                ModuleType.OTHER -> {}
            }
        }


        val command = listOf("./gradlew") +
            flags +
            modules.filteredModules.flatMap { module ->
                val tasks = when (module.type) {
                    ModuleType.OTHER -> emptyList()
                    ModuleType.KOTLIN_LIB -> kotlinLibTasks
                    ModuleType.ANDROID_LIB -> androidLibTasks
                    ModuleType.ANDROID_APP -> androidAppTasks
                }

                tasks.map { task -> module.path + ":" + task }
            }

        val result = shell.run(command.toTypedArray())
        return result.exitCode == 0
    }
}
