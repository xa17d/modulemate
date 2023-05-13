package at.xa1.modulemate.command

import at.xa1.modulemate.command.step.FakeCommandStep
import at.xa1.modulemate.config.Source
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommandTest {

    @Test
    fun `step with runWhen=PREVIOUS_FAILURE does not run if previous step succeeds`() {
        val step1 = FakeCommandStep(CommandResult.SUCCESS)
        val step2 = FakeCommandStep(CommandResult.SUCCESS)

        val stepConfig1 = CommandStepConfig(
            successCondition = StepSuccessCondition.PREVIOUS_SUCCESS,
            step = step1
        )

        val stepConfig2 = CommandStepConfig(
            successCondition = StepSuccessCondition.PREVIOUS_FAILURE,
            step = step2
        )

        val command = Command(
            shortcuts = listOf("test"),
            name = "test",
            stepConfigs = listOf(stepConfig1, stepConfig2),
            source = Source.BuiltIn
        )

        val result = command.run(testCommandContext())

        assertTrue(step1.didRun)
        assertFalse(step2.didRun)

        assertEquals(CommandResult.SUCCESS, result)
    }

    @Test
    fun `step with runWhen=PREVIOUS_SUCCESS does not run if previous step fails`() {
        val step1 = FakeCommandStep(CommandResult.FAILURE)
        val step2 = FakeCommandStep(CommandResult.SUCCESS)

        val stepConfig1 = CommandStepConfig(
            successCondition = StepSuccessCondition.PREVIOUS_SUCCESS,
            step = step1
        )

        val stepConfig2 = CommandStepConfig(
            successCondition = StepSuccessCondition.PREVIOUS_SUCCESS,
            step = step2
        )

        val command = Command(
            shortcuts = listOf("test"),
            name = "test",
            stepConfigs = listOf(stepConfig1, stepConfig2),
            source = Source.BuiltIn
        )

        val result = command.run(testCommandContext())

        assertTrue(step1.didRun)
        assertFalse(step2.didRun)

        assertEquals(CommandResult.FAILURE, result)
    }

    @Test
    fun `step with runWhen=PREVIOUS_FAILURE does run if previous step fails`() {
        val step1 = FakeCommandStep(CommandResult.FAILURE)
        val step2 = FakeCommandStep(CommandResult.SUCCESS)

        val stepConfig1 = CommandStepConfig(
            successCondition = StepSuccessCondition.PREVIOUS_SUCCESS,
            step = step1
        )

        val stepConfig2 = CommandStepConfig(
            successCondition = StepSuccessCondition.PREVIOUS_FAILURE,
            step = step2
        )

        val command = Command(
            shortcuts = listOf("test"),
            name = "test",
            stepConfigs = listOf(stepConfig1, stepConfig2),
            source = Source.BuiltIn
        )

        val result = command.run(testCommandContext())

        assertTrue(step1.didRun)
        assertTrue(step2.didRun)

        assertEquals(CommandResult.FAILURE, result)
    }

    @Test
    fun `steps with runWhen=ALWAYS run regardless the previous steps failed`() {
        val step1 = FakeCommandStep(CommandResult.SUCCESS)
        val step2 = FakeCommandStep(CommandResult.FAILURE)
        val step3 = FakeCommandStep(CommandResult.FAILURE)

        val stepConfig1 = CommandStepConfig(
            successCondition = StepSuccessCondition.PREVIOUS_SUCCESS,
            step = step1
        )

        val stepConfig2 = CommandStepConfig(
            successCondition = StepSuccessCondition.ALWAYS,
            step = step2
        )

        val stepConfig3 = CommandStepConfig(
            successCondition = StepSuccessCondition.ALWAYS,
            step = step3
        )

        val command = Command(
            shortcuts = listOf("test"),
            name = "test",
            stepConfigs = listOf(stepConfig1, stepConfig2, stepConfig3),
            source = Source.BuiltIn
        )

        val result = command.run(testCommandContext())

        assertTrue(step1.didRun)
        assertTrue(step2.didRun)
        assertTrue(step3.didRun)

        assertEquals(CommandResult.FAILURE, result)
    }
}
