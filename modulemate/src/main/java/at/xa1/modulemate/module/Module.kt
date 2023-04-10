package at.xa1.modulemate.module

data class Module(
    val path: String,
    val modulePath: String,
    val type: ModuleType
)

enum class ModuleType {
    OTHER,
    JAVA_LIB,
    ANDROID_LIB,
    ANDROID_APP,
}
