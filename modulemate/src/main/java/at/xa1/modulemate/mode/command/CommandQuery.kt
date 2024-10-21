package at.xa1.modulemate.mode.command

import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandList

internal fun CommandList.search(queryText: String): List<Command> {
    val query = CommandQuery(queryText)
    return allCommands.filter { command -> query.matchesCommand(command) }
        .sortedByDescending { command -> query.rankCommand(command) }
}

private class CommandQuery(
    queryText: String,
) {
    private val tokens =
        queryText.split(' ')
            .filter { token -> token.isNotBlank() }
            .toSet()

    fun matchesCommand(command: Command): Boolean = rankCommand(command) > 0

    fun rankCommand(command: Command): Int {
        if (tokens.isEmpty()) {
            return 1
        }

        var score = 0

        score += command.shortcuts.intersect(tokens).count() * EXACT_SHORTCUT_SCORE
        score +=
            command.shortcuts.sumOf { shortcut ->
                tokens.count { token -> shortcut.contains(token) } * PARTIAL_SHORTCUT_SCORE
            }
        score +=
            tokens.sumOf { token ->
                if (command.name.lowercase().contains(token.lowercase())) {
                    NAME_CONTAINS_TOKEN_SCORE
                } else {
                    0
                }
            }

        return score
    }

    companion object {
        private const val EXACT_SHORTCUT_SCORE = 1000
        private const val PARTIAL_SHORTCUT_SCORE = 100
        private const val NAME_CONTAINS_TOKEN_SCORE = 1
    }
}
