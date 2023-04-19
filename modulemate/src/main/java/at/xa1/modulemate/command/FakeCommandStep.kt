package at.xa1.modulemate.command

class FakeCommandStep(
    override val runWhen: RunWhen,
    private val runResult: CommandResult,
) : CommandStep {

    var didRun: Boolean = false
    override fun run(): CommandResult {
        didRun = true
        return runResult
    }
}
