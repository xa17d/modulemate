package at.xa1.modulemate.config

class ConfigMerger(
    private val configs: List<ConfigSource>
) {
    fun getModuleClassification(): ModuleClassificationConfig {
        return configs.firstNotNullOfOrNull { configSource -> configSource.config.module.classification }
            ?: error(
                "No configuration provides module classification.\n" +
                    "config files: $configFiles"
            )
    }

    fun getCommandConfigs(): List<Command> = configs.flatMap { configSource -> configSource.config.commands }

    private val configFiles: String
        get() = configs.joinToString(separator = "\n") { configSource -> configSource.source.absolutePath }
}
