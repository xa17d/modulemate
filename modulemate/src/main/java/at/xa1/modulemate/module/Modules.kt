package at.xa1.modulemate.module

import at.xa1.modulemate.module.filter.EmptyFilter
import at.xa1.modulemate.module.filter.ModulesFilter
import java.io.File

class Modules(
    private val scanner: ModulesScanner
) {
    private val mActiveModules = HashSet<Module>()
    private val mRecentModules = mutableListOf<Module>()

    val allModules: List<Module> by lazy { scanner.scan().sortedWith(sortComparator) }

    val recentModules: List<Module> = emptyList()
    val activeModules: Set<Module>
        get() = mActiveModules

    fun isActive(module: Module) = activeModules.contains(module)
    fun activate(module: Module) {
        if (!isActive(module)) {
            mActiveModules.add(module)

            if (!recentModules.contains(module)) {
                mRecentModules.add(module)
                mRecentModules.sortWith(sortComparator)
            }
        }
    }

    fun deactivate(module: Module) {
        mActiveModules.remove(module)
    }

    fun removeRecent(module: Module) {
        deactivate(module)
        mRecentModules.remove(module)
    }

    var filteredModules: List<Module> = emptyList()
        private set

    var filter: ModulesFilter = EmptyFilter()
        private set

    fun applyFilter(filter: ModulesFilter) {
        this.filter = filter
        filteredModules = filter.filter(allModules)
    }

    fun applyFilterIfFindsModules(filter: ModulesFilter): Boolean {
        val filteredModules = filter.filter(allModules)

        val findsModules = filteredModules.isNotEmpty()
        if (findsModules) {
            this.filteredModules = filteredModules
            this.filter = filter
        }

        return findsModules
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

    companion object {
        private val sortComparator = compareBy<Module> { module -> module.path }
    }
}
