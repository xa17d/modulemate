package at.xa1.modulemate.command

import at.xa1.modulemate.command.variable.DefaultVariables
import at.xa1.modulemate.command.variable.Variables
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.Modules

data class CommandContext(
    val repository: GitRepository,
    val modules: Modules,
    val variables: Variables,
)

val CommandContext.hotModule: Module
    get() = modules.getByPath(variables.get(DefaultVariables.HOT_MODULE))
