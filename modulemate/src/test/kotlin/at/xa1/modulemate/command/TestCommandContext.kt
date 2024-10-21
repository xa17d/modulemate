package at.xa1.modulemate.command

import at.xa1.modulemate.command.variable.VariableSet
import at.xa1.modulemate.command.variable.Variables
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.testModules
import at.xa1.modulemate.system.FakeShell
import java.io.File

fun testCommandContext(
    repository: GitRepository = GitRepository(FakeShell(), File(".")),
    modules: Modules = testModules(),
    variables: Variables = VariableSet(),
): CommandContext =
    CommandContext(
        repository = repository,
        modules = modules,
        variables = variables,
    )
