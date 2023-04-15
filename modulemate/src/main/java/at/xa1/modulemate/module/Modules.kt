package at.xa1.modulemate.module

data class Modules(
    val modules: List<Module>
) {
    override fun toString(): String {
        val androidApps = modules.count { it.type == ModuleType.ANDROID_APP }
        val androidLibs = modules.count { it.type == ModuleType.ANDROID_LIB }
        val javaLibs = modules.count { it.type == ModuleType.KOTLIN_LIB }
        val others = modules.size - androidApps - androidLibs - javaLibs
        return "$androidApps apps, $androidLibs androidLibs, $javaLibs javaLibs, $others others"
    }
}
