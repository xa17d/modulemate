package at.xa1.modulemate.git

import at.xa1.modulemate.system.Shell
import at.xa1.modulemate.system.getLines
import at.xa1.modulemate.system.getSingleLine
import at.xa1.modulemate.system.run
import at.xa1.modulemate.system.verifySuccessful
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class GitRepository(
    shell: Shell,
    folder: File
) {
    private val shell: Shell = shell.folder(folder)

    fun getRepositoryRoot(): File {
        return File(shell.run("git", "rev-parse", "--show-toplevel").getSingleLine())
    }

    fun getBranch(): String {
        return shell.run("git", "rev-parse", "--abbrev-ref", "HEAD").getSingleLine()
    }

    fun getRemoteOrigin(): GitRemote {
        return GitRemote.create(shell.run("git", "remote", "get-url", "origin").getSingleLine())
    }

    fun getChangedFiles(ref: String? = null): List<String> {
        val mainBranch = getMainBranch()
        val mergeBase = getMergeBase(ref ?: "HEAD", mainBranch)
        return shell.run(listOfNotNull("git", "diff", "--name-only", mergeBase, ref).toTypedArray())
            .getLines()
            .filter { line -> line.isNotEmpty() }
    }

    fun getAuthors(after: ZonedDateTime, ref: String): Set<String> {
        val mergeBase = getMergeBase(ref)
        val afterFormatted = after.format(DateTimeFormatter.ISO_DATE_TIME)
        return shell.run(
            "git",
            "log",
            "--after=\"$afterFormatted\"",
            "--pretty=format:\"%an <%ae>\"",
            "$mergeBase..$ref"
        ).getLines().toSet()
    }

    fun fetchAll() {
        shell.run("git", "fetch", "--all").verifySuccessful()
    }

    fun getLatestChangeOnRemote(): List<RefLatestChange> {
        val lines = shell.run(
            "git",
            "for-each-ref",
            "--format=%(committerdate:iso-strict)|%(refname)",
            "refs/remotes"
        ).getLines()
            .filter { line -> line.isNotEmpty() }

        return lines.map { line ->
            val parts = line.split('|', limit = 2)

            if (parts.size != 2) {
                error("Expected 2 parts separated be '|', but got: $line")
            }

            RefLatestChange(
                ref = parts[1],
                latestCommitDate = ZonedDateTime.parse(parts[0], DateTimeFormatter.ISO_DATE_TIME)
            )
        }
    }

    /**
     * @return full name of the remote tracking branch, or `null` if the local branch doesn't track a remote branch.
     */
    fun getRemoteTrackingBranch(): String? {
        val result = shell.run("git", "rev-parse", "--symbolic-full-name", "@{u}")
        return when (result.exitCode) {
            0 -> result.getSingleLine()
            128 -> null
            else -> {
                result.verifySuccessful() // this will throw an error
                null
            }
        }
    }

    private fun getMergeBase(ref: String, mainBranch: String = getMainBranch()): String {
        return shell.run("git", "merge-base", ref, mainBranch).getSingleLine()
    }

    private fun getMainBranch(): String {
        return shell.run("git", "symbolic-ref", "refs/remotes/origin/HEAD").getSingleLine()
    }
}

data class RefLatestChange(
    val ref: String,
    val latestCommitDate: ZonedDateTime
)
