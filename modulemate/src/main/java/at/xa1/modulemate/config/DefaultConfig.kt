package at.xa1.modulemate.config

val defaultConfig: Config = Config(
    module = ModuleConfig(
        classification = ModuleClassificationConfig(
            javaLibrary = Regex.escape("apply plugin: \"java-library\""),
            androidLibrary = Regex.escape("apply plugin: \"com.android.library\""),
            androidApp = Regex.escape("apply plugin: \"com.android.application\""),
        )
    )
)
