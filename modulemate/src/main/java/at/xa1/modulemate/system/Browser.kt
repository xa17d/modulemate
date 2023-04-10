package at.xa1.modulemate.system

interface Browser {
    fun open(url: String)
}

class ShellOpenBrowser(
    private val shell: Shell
) : Browser {
    override fun open(url: String) {
        shell.run("open", url)
    }
}
