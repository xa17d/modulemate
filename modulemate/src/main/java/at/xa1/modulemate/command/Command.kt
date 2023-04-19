package at.xa1.modulemate.command

class Command(
    val shortcut: String,
    val name: String,
    val steps: List<CommandStep>
) {
    fun run(): CommandResult {
        var result = CommandResult.SUCCESS
        for (step in steps) {

            val runStep = when (result) {
                CommandResult.SUCCESS -> step.runWhen == RunWhen.PREVIOUS_SUCCESS || step.runWhen == RunWhen.ALWAYS
                CommandResult.FAILURE -> step.runWhen == RunWhen.PREVIOUS_FAILURE || step.runWhen == RunWhen.ALWAYS
            }

            if (runStep) {
                val stepResult = step.run()
                if (stepResult == CommandResult.FAILURE) {
                    result = CommandResult.FAILURE
                }
            }
        }
        return result
    }
}

enum class CommandResult {
    SUCCESS,
    FAILURE
}

fun Boolean.successToCommandResult(): CommandResult = if (this) {
    CommandResult.SUCCESS
} else {
    CommandResult.FAILURE
}

sealed interface CommandStep {
    val runWhen: RunWhen
    fun run(): CommandResult
}
