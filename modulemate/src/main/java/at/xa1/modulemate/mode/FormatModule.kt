package at.xa1.modulemate.mode

import at.xa1.modulemate.cli.CliFormat
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType

fun formatModule(module: Module, withColors: Boolean = true): String {
    val text = module.path

    return if (withColors) {
        val formatting = when (module.type) {
            ModuleType.KOTLIN_LIB -> CliFormat.BLUE
            ModuleType.ANDROID_LIB -> CliFormat.GREEN
            ModuleType.ANDROID_APP -> CliFormat.CYAN
            ModuleType.OTHER -> ""
        }
        return "$formatting${module.path}${CliFormat.RESET}"
    } else {
        text
    }
}
