package at.xa1.modulemate.git

import at.xa1.modulemate.system.Shell
import at.xa1.modulemate.system.getLines
import at.xa1.modulemate.system.getSingleLine
import at.xa1.modulemate.system.run
import java.io.File

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

    fun getChangedFiles(): List<String> {
        val mainBranch = getMainBranch()
        val mergeBase = shell.run("git", "merge-base", "HEAD", mainBranch).getSingleLine()
        return shell.run("git", "diff", "--name-only", mergeBase).getLines()
    }


    private fun getMainBranch(): String {
        return shell.run("git", "symbolic-ref", "refs/remotes/origin/HEAD").getSingleLine()
    }
}
