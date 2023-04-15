package at.xa1.modulemate.system

import java.io.File
import java.util.concurrent.TimeUnit

class PrintingShell(
    private val folder: File
) : Shell {
    override fun folder(folder: File): Shell {
        return PrintingShell(folder)
    }

    override fun run(command: Array<out String>): ShellResult {
        val processBuilder = ProcessBuilder(command.toList())
            .directory(folder)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
        val process = processBuilder.start()

        val stdoutThread = Thread {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { println(it) }
            }
        }
        stdoutThread.start()

        val stderrThread = Thread {
            process.errorStream.bufferedReader().useLines { lines ->
                lines.forEach { System.err.println(it) }
            }
        }
        stderrThread.start()

        process.waitFor()

        stdoutThread.join()
        stderrThread.join()

        val exitCode = process.exitValue()
        return ShellResult(
            out = "",
            error = "",
            exitCode = exitCode
        )
    }
}
