package at.xa1.modulemate.command

import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.system.Shell

class GradleCommandStep(
    private val shell: Shell,
    private val modules: Modules,
    private val flags: List<String>,
    private val javaLibraryTasks: List<String>,
    private val androidLibTasks: List<String>,
    private val androidAppTasks: List<String>,
) : CommandStep {
    override fun run(): Boolean {
        val command =
            listOf("./gradlew") +
                flags +
                modules.modules.flatMap { module ->
                    val tasks = when (module.type) {
                        ModuleType.OTHER -> emptyList()
                        ModuleType.JAVA_LIB -> javaLibraryTasks
                        ModuleType.ANDROID_LIB -> androidLibTasks
                        ModuleType.ANDROID_APP -> androidAppTasks
                    }

                    tasks.map { task -> module.path + ":" + task }
                }

        val result = shell.run(command.toTypedArray())
        return result.exitCode == 0
    }
}
