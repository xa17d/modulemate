package at.xa1.modulemate.module

import at.xa1.modulemate.config.ModuleClassificationConfig
import java.io.File

class ModulesScanner(
    config: ModuleClassificationConfig
) {
    private val javaLibraryRegex = Regex(config.javaLibrary)
    private val androidLibraryRegex = Regex(config.androidLibrary)
    private val androidAppRegex = Regex(config.androidApp)
    fun scan(root: File): Modules {
        val buildGradleFiles = root.walk()
            .filter { it.isFile && it.name == "build.gradle" }

        val modules = buildGradleFiles.map { buildGradleFile ->
            val modulePath = buildGradleFile.parentFile.toRelativeString(root)
            val path = ":" + modulePath.replace("/", ":")

            Module(
                path = path,
                modulePath = modulePath,
                type = getType(buildGradleFile.readText())
            )
        }

        return Modules(modules.toList())
    }

    private fun getType(buildGradleContent: String): ModuleType {
        return when {
            javaLibraryRegex.containsMatchIn(buildGradleContent) -> ModuleType.JAVA_LIB
            androidLibraryRegex.containsMatchIn(buildGradleContent) -> ModuleType.ANDROID_LIB
            androidAppRegex.containsMatchIn(buildGradleContent) -> ModuleType.ANDROID_APP
            else -> ModuleType.OTHER
        }
    }
}
