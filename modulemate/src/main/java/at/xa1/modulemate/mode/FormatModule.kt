package at.xa1.modulemate.mode

import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType

fun formatModule(module: Module, withColors: Boolean = true): String {
    val text = module.path

    return if (withColors) {
        val formatting = when (module.type) {
            ModuleType.KOTLIN_LIB -> CliColor.BLUE
            ModuleType.ANDROID_LIB -> CliColor.GREEN
            ModuleType.ANDROID_APP -> CliColor.CYAN
            ModuleType.OTHER -> ""
        }
        return "$formatting${module.path}${CliColor.RESET}"
    } else {
        text
    }
}
