package at.xa1.modulemate.config

import java.io.File

sealed interface Source {
    data class ConfigFile(val file: File) : Source

    object BuiltIn : Source
}
