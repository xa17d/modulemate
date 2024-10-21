package at.xa1.modulemate.module

data class Module(
    val path: String,
    val relativePath: String,
    val absolutePath: String,
    val type: ModuleType,
)

enum class ModuleType {
    OTHER,
    KOTLIN_LIB,
    ANDROID_LIB,
    ANDROID_APP,
}

fun Iterable<Module>.containsAnyAndroidModule(): Boolean =
    containsAnyOfType(ModuleType.ANDROID_LIB) || containsAnyOfType(ModuleType.ANDROID_APP)

fun Iterable<Module>.containsAnyOfType(type: ModuleType): Boolean = any { module -> module.type == type }
