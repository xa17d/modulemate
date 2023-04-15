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
    val kotlinLib: String,
    val androidLib: String,
    val androidApp: String,
)

@Serializable
data class Command(
    val shortcut: String,
    val name: String,
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
        val flags: TypeSpecificStringList = TypeSpecificStringList(),
        val tasks: TypeSpecificStringList = TypeSpecificStringList(),
    ) : CommandStep
}

@Serializable
data class TypeSpecificStringList(
    val all: List<String> = emptyList(),
    val kotlinLib: List<String> = emptyList(),
    val android: List<String> = emptyList(),
    val androidLib: List<String> = emptyList(),
    val androidApp: List<String> = emptyList(),
)

fun TypeSpecificStringList.getForKotlinLib(): List<String> = all + kotlinLib
fun TypeSpecificStringList.getForAndroidLib(): List<String> = all + android + androidLib
fun TypeSpecificStringList.getForAndroidApp(): List<String> = all + android + androidApp
