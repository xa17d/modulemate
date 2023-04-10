package at.xa1.modulemate.system

import java.io.File
import java.io.InputStreamReader

interface Shell {
    fun folder(folder: File): Shell
    fun run(command: Array<out String>): ShellResult
}

fun Shell.run(vararg command: String) = run(command)

data class ShellResult(
    val out: String,
    val error: String,
    val exitCode: Int
) {
    companion object {
        val SUCCESS_EMPTY: ShellResult
            get() = ShellResult(
                out = "",
                error = "",
                exitCode = 0
            )
    }
}

fun ShellResult.getSingleLine(): String {
    if (exitCode != 0 || error.isNotEmpty()) {
        error("Expected success, but command failed: $this")
    }

    val lines = out.lines()
    if (lines.size > 1 && lines[1].isNotEmpty()) {
        error("Expected result to have only one line with an optional \"\\n\" at the end, but lines were: $lines")
    }
    return lines[0]
}

class RuntimeShell(private val folder: File) : Shell {
    override fun run(command: Array<out String>): ShellResult {
        val process = Runtime.getRuntime().exec(command, null, folder)

        val out = InputStreamReader(process.inputStream).use { it.readText() }
        val error = InputStreamReader(process.errorStream).use { it.readText() }
        process.waitFor()
        val exitCode = process.exitValue()

        return ShellResult(
            out = out,
            error = error,
            exitCode = exitCode
        )
    }

    override fun folder(folder: File): Shell {
        return RuntimeShell(folder)
    }
}
