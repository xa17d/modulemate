package at.xa1.modulemate.system

import java.io.File
import kotlin.test.fail

class FakeShell : Shell {

    var failOnUnexpectedCommand: Boolean = true
    private val expectedCommands = mutableListOf<Pair<List<String>, ShellResult>>()
    val actualCommands: MutableList<List<String>> = mutableListOf<List<String>>()
    override fun folder(folder: File): FakeShell {
        return this
    }

    fun whenRun(vararg command: String, result: ShellResult) {
        expectedCommands.add(command.toList() to result)
    }

    override fun run(command: Array<out String>): ShellResult {
        val commandList = command.toList()
        actualCommands.add(commandList)

        val index = expectedCommands.indexOfFirst { expectedCommand -> expectedCommand.first == commandList }
        if (index < 0) {
            if (failOnUnexpectedCommand) {
                fail("Command was not expected: $commandList")
            } else {
                return ShellResult.SUCCESS_EMPTY
            }
        }
        val result = expectedCommands.removeAt(index)
        return result.second
    }
}
