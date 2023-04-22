package at.xa1.modulemate.module

import at.xa1.modulemate.module.filter.EmptyFilter
import at.xa1.modulemate.module.filter.ModulesFilter
import java.io.File

class Modules(
    private val scanner: ModulesScanner
) {
    val allModules: List<Module> by lazy { scanner.scan() }

    var filteredModules: List<Module> = emptyList()
        private set

    private var filter: ModulesFilter = EmptyFilter()
    fun applyFilter(filter: ModulesFilter) {
        this.filter = filter
        reapplyFilter()
    }

    fun reapplyFilter() {
        filteredModules = filter.filter(allModules)
    }

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

    fun getByPath(path: String): Module =
        allModules.find { module -> module.path == path } ?: error("Module with path doesn't exist: $path")
}
