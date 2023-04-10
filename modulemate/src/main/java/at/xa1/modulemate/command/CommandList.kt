package at.xa1.modulemate.command

class CommandList(
    private val commands: List<BrowserCommand>
) {
    fun runOrNull(shortcut: String): BrowserCommand? {
        val command = commands.find { it.shortcut == shortcut } ?: return null
        command.run()
        return command
    }
}
