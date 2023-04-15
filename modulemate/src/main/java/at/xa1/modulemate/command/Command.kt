package at.xa1.modulemate.command

class Command(
    val shortcut: String,
    val steps: List<CommandStep>
) {
    fun run(): Boolean {
        var overallSuccess = true
        for (step in steps) {
            val success = step.run()
            if (!success) {
                overallSuccess = false // TODO stop if step is terminal failing.
            }
        }
        return overallSuccess
    }
}

sealed interface CommandStep {
    fun run(): Boolean
}
