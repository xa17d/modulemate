package at.xa1.modulemate.command.step

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.cli.CliColor.BACKGROUND_BLUE
import at.xa1.modulemate.cli.CliColor.BOLD
import at.xa1.modulemate.cli.CliColor.WHITE
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.module
import at.xa1.modulemate.module.filter.findModulesByFiles
import java.time.Duration
import java.time.ZonedDateTime

class ActiveWork : CommandStep {

    override fun run(context: CommandContext): CommandResult {
        val repository = context.repository
        val limit = ZonedDateTime.now().minus(ACTIVE_TIME_SPAN)

        try {
            repository.fetchAll()
        } catch (e: IllegalStateException) {
            Cli.line("fetch all failed: $e")
        }

        val modules = context.modules.allModules

        repository.getLatestChangeOnRemote()
            .filter { it.latestCommitDate.isAfter(limit) }
            .forEach { refLatestChange ->
                val refName = refLatestChange.ref.removePrefix("refs/remotes/")
                val changedFiles = repository.getChangedFiles(refLatestChange.ref)
                val changedModules = modules.findModulesByFiles(changedFiles)
                val authors = repository.getAuthors(limit, refLatestChange.ref)

                if (changedFiles.isNotEmpty()) {
                    val authorsString = authors.joinToString(separator = ", ")
                    Cli.heading(
                        refName,
                        formatting = "$BOLD$BACKGROUND_BLUE",
                        addendum = " by $authorsString",
                        addendumFormatting = "$WHITE$BACKGROUND_BLUE"
                    )
                    changedModules.forEach { module ->
                        Cli.module(module, indent = "  - ")
                    }
                }
            }

        return CommandResult.SUCCESS
    }

    companion object {
        internal val ACTIVE_TIME_SPAN: Duration = Duration.ofDays(14)
    }
}
