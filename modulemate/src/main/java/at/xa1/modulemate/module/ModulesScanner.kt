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
        val buildGradleFiles = mutableListOf<File>()
        scanFolderForBuildGradle(buildGradleFiles, root)

        val modules = buildGradleFiles.map { buildGradleFile ->
            val moduleFile = buildGradleFile.parentFile
            val relativePath = moduleFile.toRelativeString(root)
            val absolutePath = moduleFile.absolutePath
            val path = ":" + relativePath.replace("/", ":")

            Module(
                path = path,
                relativePath = relativePath,
                absolutePath = absolutePath,
                type = getType(buildGradleFile.readText())
            )
        }

        return Modules(modules.toList())
    }

    private fun scanFolderForBuildGradle(result: MutableList<File>, folder: File) {
        if (folder.name in setOf("build", "src", ".git")) return // skip for performance

        (folder.listFiles() ?: emptyArray())
            .forEach { file ->
                if (file.name == "build.gradle") {
                    result.add(file)
                }

                if (file.isDirectory) {
                    scanFolderForBuildGradle(result, file)
                }
            }
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
