package at.xa1.modulemate.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val module: ModuleConfig,
)

@Serializable
data class ModuleConfig(
    val classification: ModuleClassificationConfig,
)

@Serializable
data class ModuleClassificationConfig(
    val javaLibrary: String = Regex.escape("apply plugin: \"java-library\""),
    val androidLibrary: String = Regex.escape("apply plugin: \"com.android.library\""),
    val androidApp: String = Regex.escape("apply plugin: \"com.android.application\""),
)
