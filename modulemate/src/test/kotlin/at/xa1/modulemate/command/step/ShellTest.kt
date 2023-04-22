package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.variable.VariableSet
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.testModules
import at.xa1.modulemate.system.FakeShell
import at.xa1.modulemate.system.ShellResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ShellTest {

    private val fakeShell = FakeShell()

    @Test
    fun `command is not run if mode=RUN_IF_AT_LEAST_ONE_ANDROID_MODULE but only kotlinLib module present`() {
        val modules = testModules(Module(":kotlin-lib", "", "", ModuleType.KOTLIN_LIB))

        val step = Shell(
            mode = ShellMode.RUN_IF_AT_LEAST_ONE_ANDROID_MODULE,
            shell = fakeShell,
            modulesInput = modules,
            variables = VariableSet(),
            command = listOf("myCommand")
        )

        val result = step.run()

        assertEquals(CommandResult.SUCCESS, result)
        fakeShell.assertNothingWasRun()
    }

    @Test
    fun `command is run if mode=RUN_IF_AT_LEAST_ONE_ANDROID_MODULE and there is android-lib module present`() {
        val modules = testModules(
            Module(":kotlin-lib", "", "", ModuleType.KOTLIN_LIB),
            Module(":android-lib", "", "", ModuleType.ANDROID_LIB)
        )

        val step = Shell(
            mode = ShellMode.RUN_IF_AT_LEAST_ONE_ANDROID_MODULE,
            shell = fakeShell,
            modulesInput = modules,
            variables = VariableSet(),
            command = listOf("myCommand")
        )

        fakeShell.whenRun("myCommand", result = ShellResult.SUCCESS_EMPTY)

        val result = step.run()

        assertEquals(CommandResult.SUCCESS, result)
        assertEquals(
            listOf(listOf("myCommand")),
            fakeShell.actualCommands
        )
    }
}
