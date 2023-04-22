package at.xa1.modulemate.command

import at.xa1.modulemate.cli.CliColor.BACKGROUND_BLUE
import at.xa1.modulemate.cli.CliColor.BLACK
import at.xa1.modulemate.cli.CliColor.BOLD
import at.xa1.modulemate.cli.CliColor.CLEAR_UNTIL_END_OF_LINE
import at.xa1.modulemate.cli.CliColor.RESET
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.module.filter.findModulesByFiles
import java.time.Duration
import java.time.ZonedDateTime

class ActiveWorkCommandStep(
    private val repository: GitRepository,
    private val modulesFull: Modules
) : CommandStep {

    override fun run(): CommandResult {
        val limit = ZonedDateTime.now().minus(ACTIVE_TIME_SPAN)

        try {
            repository.fetchAll()
        } catch (e: IllegalStateException) {
            println("fetch all failed: $e")
        }

        val modules = modulesFull.allModules

        repository.getLatestChangeOnRemote()
            .filter { it.latestCommitDate.isAfter(limit) }
            .forEach { refLatestChange ->
                val refName = refLatestChange.ref.removePrefix("refs/remotes/")
                val changedFiles = repository.getChangedFiles(refLatestChange.ref)
                val changedModules = modules.findModulesByFiles(changedFiles)
                val authors = repository.getAuthors(limit, refLatestChange.ref)

                if (changedFiles.isNotEmpty()) {
                    val authorsString = authors.joinToString(separator = ", ")
                    print(
                        "$BOLD$BACKGROUND_BLUE$BLACK$refName$RESET" +
                            "$BACKGROUND_BLUE by $authorsString$CLEAR_UNTIL_END_OF_LINE\n$RESET$CLEAR_UNTIL_END_OF_LINE"
                    )
                    changedModules.forEach {
                        println("- ${it.path}")
                    }
                }
            }

        return CommandResult.SUCCESS
    }

    companion object {
        internal val ACTIVE_TIME_SPAN: Duration = Duration.ofDays(14)
    }
}
