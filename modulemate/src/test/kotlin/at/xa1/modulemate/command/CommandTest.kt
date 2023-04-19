package at.xa1.modulemate.command

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class CommandTest {

    @Test
    fun `step with runWhen=PREVIOUS_FAILURE does not run if previous step succeeds`() {
        val step1 = FakeCommandStep(runWhen = RunWhen.PREVIOUS_SUCCESS, CommandResult.SUCCESS)
        val step2 = FakeCommandStep(runWhen = RunWhen.PREVIOUS_FAILURE, CommandResult.SUCCESS)

        val command = Command(
            shortcut = "test",
            name = "test",
            steps = listOf(step1, step2)
        )

        val result = command.run()

        assertTrue(step1.didRun)
        assertFalse(step2.didRun)

        assertEquals(CommandResult.SUCCESS, result)
    }

    @Test
    fun `step with runWhen=PREVIOUS_SUCCESS does not run if previous step fails`() {
        val step1 = FakeCommandStep(runWhen = RunWhen.PREVIOUS_SUCCESS, CommandResult.FAILURE)
        val step2 = FakeCommandStep(runWhen = RunWhen.PREVIOUS_SUCCESS, CommandResult.SUCCESS)

        val command = Command(
            shortcut = "test",
            name = "test",
            steps = listOf(step1, step2)
        )

        val result = command.run()

        assertTrue(step1.didRun)
        assertFalse(step2.didRun)

        assertEquals(CommandResult.FAILURE, result)
    }

    @Test
    fun `step with runWhen=PREVIOUS_FAILURE does run if previous step fails`() {
        val step1 = FakeCommandStep(runWhen = RunWhen.PREVIOUS_SUCCESS, CommandResult.FAILURE)
        val step2 = FakeCommandStep(runWhen = RunWhen.PREVIOUS_FAILURE, CommandResult.SUCCESS)

        val command = Command(
            shortcut = "test",
            name = "test",
            steps = listOf(step1, step2)
        )

        val result = command.run()

        assertTrue(step1.didRun)
        assertTrue(step2.didRun)

        assertEquals(CommandResult.FAILURE, result)
    }

    @Test
    fun `steps with runWhen=ALWAYS run regardless the previous steps failed`() {
        val step1 = FakeCommandStep(runWhen = RunWhen.PREVIOUS_SUCCESS, CommandResult.SUCCESS)
        val step2 = FakeCommandStep(runWhen = RunWhen.ALWAYS, CommandResult.FAILURE)
        val step3 = FakeCommandStep(runWhen = RunWhen.ALWAYS, CommandResult.FAILURE)

        val command = Command(
            shortcut = "test",
            name = "test",
            steps = listOf(step1, step2, step3)
        )

        val result = command.run()

        assertTrue(step1.didRun)
        assertTrue(step2.didRun)
        assertTrue(step3.didRun)

        assertEquals(CommandResult.FAILURE, result)
    }
}
