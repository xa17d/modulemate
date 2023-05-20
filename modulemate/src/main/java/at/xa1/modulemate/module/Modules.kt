package at.xa1.modulemate.module

import at.xa1.modulemate.module.filter.CustomFilter
import at.xa1.modulemate.module.filter.EmptyFilter
import at.xa1.modulemate.module.filter.ModulesFilter
import java.io.File

class Modules(
    private val scanner: ModulesScanner
) {
    private val mActiveModules = mutableSetOf<Module>()
    private val mRecentModules = mutableListOf<Module>()

    val allModules: List<Module> by lazy { scanner.scan().sortedWith(sortComparator) }

    val recentModules: List<Module>
        get() = mRecentModules
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

            applyFilter(CustomFilter)
        }
    }

    fun deactivate(module: Module) {
        mActiveModules.remove(module)
        applyFilter(CustomFilter)
    }

    fun removeRecent(module: Module) {
        deactivate(module)
        mRecentModules.remove(module)
        applyFilter(CustomFilter)
    }

    var filter: ModulesFilter = EmptyFilter()
        private set

    fun applyFilter(filter: ModulesFilter) {
        if (filter != CustomFilter) {
            mRecentModules.clear()
            mActiveModules.clear()

            val filteredModules = filter.filter(allModules)
            mRecentModules.addAll(filteredModules)
            mActiveModules.addAll(filteredModules)
        }
        this.filter = filter
    }

    fun applyFilterIfFindsModules(filter: ModulesFilter): Boolean {
        val filteredModules = filter.filter(allModules)

        val findsModules = filteredModules.isNotEmpty()
        if (findsModules) {
            applyFilter(filter)
        }

        return findsModules
    }

    fun getHotModule(): Module {
        var lastModifiedModule: Module? = null
        var lastModifiedTime: Long = Long.MIN_VALUE

        activeModules.forEach { module ->
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
