package at.xa1.modulemate.command

class Command(
    val shortcut: String,
    val name: String,
    val stepConfigs: List<CommandStepConfig>
) {
    fun run(context: CommandContext): CommandResult {
        var result = CommandResult.SUCCESS
        for (stepConfig in stepConfigs) {
            val runStep = when (result) {
                CommandResult.SUCCESS ->
                    stepConfig.successCondition == StepSuccessCondition.PREVIOUS_SUCCESS ||
                        stepConfig.successCondition == StepSuccessCondition.ALWAYS

                CommandResult.FAILURE ->
                    stepConfig.successCondition == StepSuccessCondition.PREVIOUS_FAILURE ||
                        stepConfig.successCondition == StepSuccessCondition.ALWAYS
            }

            if (runStep) {
                val stepResult = stepConfig.step.run(context)
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
