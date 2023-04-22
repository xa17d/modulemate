package at.xa1.modulemate.command

import at.xa1.modulemate.command.step.CommandStep

data class CommandStepConfig(
    val successCondition: StepSuccessCondition,
    val step: CommandStep
)

enum class StepSuccessCondition {
    PREVIOUS_SUCCESS,
    PREVIOUS_FAILURE,
    ALWAYS
}
