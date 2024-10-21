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

    fun getCommandsWithSource(): List<CommandSource> = configs.flatMap { configSource ->
        configSource.config.commands.map { command ->
            CommandSource(source = configSource.source, command = command)
        }
    }

    fun getVariablesConfig(): VariablesConfig {
        val gitHostSubstitutions = configs.flatMap { it.config.variables.gitHostSubstitutions }

        return VariablesConfig(
            gitHostSubstitutions = gitHostSubstitutions
        )
    }

    private val configFiles: String
        get() = configs.mapNotNull { configSource ->
            when (val source = configSource.source) {
                is Source.ConfigFile -> source.file.absolutePath
                Source.BuiltIn -> null
            }
        }.joinToString(separator = "\n")
}
