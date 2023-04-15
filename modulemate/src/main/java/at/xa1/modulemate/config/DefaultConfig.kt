package at.xa1.modulemate.config

val defaultConfig: Config = Config(
    module = ModuleConfig(
        classification = ModuleClassificationConfig(
            kotlinLib = Regex.escape("apply plugin: \"java-library\""),
            androidLib = Regex.escape("apply plugin: \"com.android.library\""),
            androidApp = Regex.escape("apply plugin: \"com.android.application\""),
        )
    ),
    commands = listOf(),
)
