package at.xa1.modulemate.system

interface Browser {
    fun open(url: String): Boolean
}

class ShellOpenBrowser(
    private val shell: Shell
) : Browser {
    override fun open(url: String): Boolean {
        val shellResult = shell.run("open", url)
        return shellResult.isSuccess
    }
}
