package at.xa1.modulemate.command

import at.xa1.modulemate.system.Shell

class ShellCommandStep(
    private val shell: Shell,
    private val variables: Variables,
    private val command: List<String>,
) : CommandStep {
    override fun run(): Boolean {
        val result = shell.run(
            command.map { variables.replacePlaceholders(it) }.toTypedArray()
        )
        return (result.exitCode == 0)
    }
}
