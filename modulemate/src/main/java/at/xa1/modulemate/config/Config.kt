package at.xa1.modulemate.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val module: ModuleConfig,
    val commands: List<Command>,
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

@Serializable
data class Command(
    val shortcut: String,
    val steps: List<CommandStep>,
)

@Serializable
sealed interface CommandStep {

    @Serializable
    @SerialName("browser")
    data class Browser(
        val url: String
    ) : CommandStep

    @Serializable
    @SerialName("gradle")
    data class Gradle(
        val flags: List<String> = emptyList(),
        val tasks: List<String> = emptyList(),
        val javaLibTasks: List<String> = emptyList(),
        val androidTasks: List<String> = emptyList(),
        val androidLibTasks: List<String> = emptyList(),
        val androidAppTasks: List<String> = emptyList(),
    ) : CommandStep
}
