package at.xa1.modulemate.module

import java.io.File

class Modules(
    private val scanner: ModulesScanner
) {
    private val allModules: List<Module> by lazy { scanner.scan() }

    var filter: String? = null
        set(value) {
            if (field != value) {
                field = value
                filteredModules = if (value == null) {
                    emptyList()
                } else {
                    allModules.filter { module -> module.relativePath.startsWith(value) }
                }

            }
        }
    var filteredModules: List<Module> = emptyList()
        private set

    fun getActiveModule(): Module {
        var lastModifiedModule: Module? = null
        var lastModifiedFile: File? = null
        var lastModifiedTime: Long = Long.MIN_VALUE

        filteredModules.forEach { module ->
            File(module.absolutePath, "src").walk().forEach { file ->
                val fileModified = file.lastModified()

                if (fileModified > lastModifiedTime) {
                    lastModifiedTime = fileModified
                    lastModifiedFile = file
                    lastModifiedModule = module
                }
            }
        }

        return lastModifiedModule ?: error("no module selected")
    }
    override fun toString(): String {
        val androidApps = filteredModules.count { it.type == ModuleType.ANDROID_APP }
        val androidLibs = filteredModules.count { it.type == ModuleType.ANDROID_LIB }
        val javaLibs = filteredModules.count { it.type == ModuleType.KOTLIN_LIB }
        val others = filteredModules.size - androidApps - androidLibs - javaLibs
        return "$androidApps apps, $androidLibs androidLibs, $javaLibs javaLibs, $others others"
    }
}
