package at.xa1.modulemate.command.step

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.cli.CliFormat.BLUE
import at.xa1.modulemate.cli.CliFormat.RESET
import at.xa1.modulemate.cli.CliFormat.WHITE
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.step.ActiveWork.Companion.ACTIVE_TIME_SPAN
import at.xa1.modulemate.mode.formatModule
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.filter.findModulesByFiles
import java.time.ZonedDateTime

class ConflictAnalysis : CommandStep {

    override fun run(context: CommandContext): CommandResult {
        val repository = context.repository

        val limit = ZonedDateTime.now().minus(ACTIVE_TIME_SPAN)

        try {
            repository.fetchAll()
        } catch (e: IllegalStateException) {
            Cli.line("fetch all failed: $e")
        }

        val remoteTrackingBranch = repository.getRemoteTrackingBranch()

        val modules = mutableMapOf<Module, ModuleConflicts>()
        val consideredModules = context.modules.activeModules
        consideredModules.forEach { module ->
            modules[module] = ModuleConflicts(module, mutableListOf())
        }

        repository.getLatestChangeOnRemote()
            .filter { it.latestCommitDate.isAfter(limit) }
            .forEach { refLatestChange ->
                val changedFiles = repository.getChangedFiles(refLatestChange.ref)
                val changedModules = consideredModules.findModulesByFiles(changedFiles)
                val authors = repository.getAuthors(limit, refLatestChange.ref)

                changedModules.forEach { changedModule ->
                    modules[changedModule]!!.branches.add(ModuleChangeBranch(refLatestChange.ref, authors))
                }
            }

        modules.values.forEach { module ->
            Cli.line(formatModule(module.module))
            module.branches
                .filter { branch -> branch.ref != remoteTrackingBranch }
                .forEach { branch ->
                    Cli.line(
                        "  on $BLUE${branch.ref}$RESET" +
                            " by $WHITE${branch.authors.joinToString(separator = ", ")}$RESET"
                    )
                }
        }

        return CommandResult.SUCCESS
    }

    data class ModuleConflicts(
        val module: Module,
        val branches: MutableList<ModuleChangeBranch>
    )

    data class ModuleChangeBranch(
        val ref: String,
        val authors: Set<String>
    )
}
