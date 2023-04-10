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
    val javaLibrary: String,
    val androidLibrary: String,
    val androidApp: String,
)
