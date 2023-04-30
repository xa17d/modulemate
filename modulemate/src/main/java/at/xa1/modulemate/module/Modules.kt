package at.xa1.modulemate.module

import at.xa1.modulemate.module.filter.EmptyFilter
import at.xa1.modulemate.module.filter.ModulesFilter
import java.io.File

class Modules(
    private val scanner: ModulesScanner
) {
    val allModules: List<Module> by lazy { scanner.scan().sortedBy { it.path } }

    var filteredModules: List<Module> = emptyList()
        private set

    var filter: ModulesFilter = EmptyFilter()
        private set

    fun applyFilter(filter: ModulesFilter) {
        this.filter = filter
        reapplyFilter()
    }

    fun applyFilterIfFindsModules(filter: ModulesFilter): Boolean {
        val filteredModules = filter.filter(allModules)

        val findsModules = filteredModules.isNotEmpty()
        if (findsModules) {
            this.filteredModules = filteredModules
        }

        return findsModules
    }

    fun reapplyFilter() {
        filteredModules = filter.filter(allModules)
    }

    fun getActiveModule(): Module {
        var lastModifiedModule: Module? = null
        var lastModifiedTime: Long = Long.MIN_VALUE

        filteredModules.forEach { module ->
            File(module.absolutePath, "src").walk().forEach { file ->
                val fileModified = file.lastModified()

                if (fileModified > lastModifiedTime) {
                    lastModifiedTime = fileModified
                    lastModifiedModule = module
                }
            }
        }

        return lastModifiedModule ?: error("no module selected")
    }

    fun getByPath(path: String): Module =
        allModules.find { module -> module.path == path } ?: error("Module with path doesn't exist: $path")
}
