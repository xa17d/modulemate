package at.xa1.modulemate.config

import at.xa1.modulemate.command.RunWhen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val module: ModuleConfig,
    val commands: List<Command>
)

@Serializable
data class ModuleConfig(
    val classification: ModuleClassificationConfig
)

@Serializable
data class ModuleClassificationConfig(
    val kotlinLib: String,
    val androidLib: String,
    val androidApp: String
)

@Serializable
data class Command(
    val shortcut: String,
    val name: String,
    val steps: List<CommandStep>
)

@Serializable
sealed interface CommandStep {

    val runWhen: CommandStepRunWhen

    @Serializable
    @SerialName("browser")
    data class Browser(
        override val runWhen: CommandStepRunWhen = CommandStepRunWhen.PREVIOUS_SUCCESS,
        val url: String
    ) : CommandStep

    @Serializable
    @SerialName("gradle")
    data class Gradle(
        override val runWhen: CommandStepRunWhen = CommandStepRunWhen.PREVIOUS_SUCCESS,
        val flags: TypeSpecificStringList = TypeSpecificStringList(),
        val tasks: TypeSpecificStringList = TypeSpecificStringList()
    ) : CommandStep

    @Serializable
    @SerialName("shell")
    data class Shell(
        override val runWhen: CommandStepRunWhen = CommandStepRunWhen.PREVIOUS_SUCCESS,
        val command: List<String> = emptyList()
    ) : CommandStep

    @Serializable
    @SerialName("report")
    data class Report(
        override val runWhen: CommandStepRunWhen = CommandStepRunWhen.PREVIOUS_SUCCESS,
        val path: TypeSpecificStringList = TypeSpecificStringList()
    ) : CommandStep
}

@Serializable
enum class CommandStepRunWhen {
    PREVIOUS_SUCCESS,
    PREVIOUS_FAILURE,
    ALWAYS
}

fun CommandStepRunWhen.toRunWhen(): RunWhen = when (this) {
    CommandStepRunWhen.PREVIOUS_SUCCESS -> RunWhen.PREVIOUS_SUCCESS
    CommandStepRunWhen.PREVIOUS_FAILURE -> RunWhen.PREVIOUS_FAILURE
    CommandStepRunWhen.ALWAYS -> RunWhen.ALWAYS
}

@Serializable
data class TypeSpecificStringList(
    val all: List<String> = emptyList(),
    val kotlinLib: List<String> = emptyList(),
    val android: List<String> = emptyList(),
    val androidLib: List<String> = emptyList(),
    val androidApp: List<String> = emptyList()
)

fun TypeSpecificStringList.getForKotlinLib(): List<String> = all + kotlinLib
fun TypeSpecificStringList.getForAndroidLib(): List<String> = all + android + androidLib
fun TypeSpecificStringList.getForAndroidApp(): List<String> = all + android + androidApp
