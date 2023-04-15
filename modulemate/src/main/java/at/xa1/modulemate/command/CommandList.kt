package at.xa1.modulemate.command

class CommandList(
    private val commands: List<Command>
) {
    fun getOrNull(shortcut: String): Command? {
        return commands.find { it.shortcut == shortcut }
    }
}
