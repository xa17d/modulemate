package at.xa1.modulemate.command

data class CommandStepConfig(
    val successCondition: StepSuccessCondition,
    val step: CommandStep
)

enum class StepSuccessCondition {
    PREVIOUS_SUCCESS,
    PREVIOUS_FAILURE,
    ALWAYS
}
