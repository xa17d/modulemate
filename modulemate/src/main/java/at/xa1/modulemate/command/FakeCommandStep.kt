package at.xa1.modulemate.command

class FakeCommandStep(
    private val runResult: CommandResult
) : CommandStep {

    var didRun: Boolean = false
    override fun run(): CommandResult {
        didRun = true
        return runResult
    }
}
