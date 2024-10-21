package at.xa1.modulemate.system

import java.io.File

class PrintingShell(
    private val folder: File,
) : Shell {
    override fun folder(folder: File): Shell {
        return PrintingShell(folder)
    }

    override fun run(command: Array<out String>): ShellResult {
        val processBuilder =
            ProcessBuilder(command.toList())
                .directory(folder)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
        val process = processBuilder.start()

        val stdoutThread =
            Thread {
                process.inputStream.copyTo(System.out)
            }
        stdoutThread.start()

        val stderrThread =
            Thread {
                process.errorStream.copyTo(System.err)
            }
        stderrThread.start()

        process.waitFor()

        stdoutThread.join()
        stderrThread.join()

        val exitCode = process.exitValue()
        return ShellResult(
            out = "",
            error = "",
            exitCode = exitCode,
        )
    }
}
