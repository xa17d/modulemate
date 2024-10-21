package at.xa1.modulemate.command.variable

import at.xa1.modulemate.config.VariablesConfig

internal fun getHostSubstituted(
    variablesConfig: VariablesConfig,
    host: String,
): String =
    variablesConfig.gitHostSubstitutions
        .find { substitution -> substitution.value.lowercase() == host.lowercase() }?.replacement ?: host
