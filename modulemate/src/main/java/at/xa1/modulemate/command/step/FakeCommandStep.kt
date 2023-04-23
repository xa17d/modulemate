package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult

class FakeCommandStep(
    private val runResult: CommandResult
) : CommandStep {

    var didRun: Boolean = false
    override fun run(commandContext: CommandContext): CommandResult {
        didRun = true
        return runResult
    }
}
