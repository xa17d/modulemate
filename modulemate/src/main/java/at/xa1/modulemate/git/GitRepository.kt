package at.xa1.modulemate.git

import java.io.File
import java.io.InputStreamReader

class GitRepository(
    private val folder: File
) {
    fun getRepositoryRoot(): File {
        return File(execGetLine0("git rev-parse --show-toplevel"))
    }

    fun getBranch(): String {
        return execGetLine0("git rev-parse --abbrev-ref HEAD")
    }

    fun getRemoteOrigin(): GitRemote {
        return GitRemote.create(execGetLine0("git remote get-url origin"))
    }

    private fun exec(command: String): String {
        val process = Runtime.getRuntime().exec(command, null, folder)
        val result = InputStreamReader(process.inputStream).use { it.readText() }
        return result
    }

    private fun execGetLine0(command: String): String {
        val lines = exec(command).lines()
        if (lines.size > 1 && lines[1].isNotEmpty()) {
            error("Expected result to have only one line with a \\n at the end, but lines were: $lines")
        }
        return lines[0]
    }
}
