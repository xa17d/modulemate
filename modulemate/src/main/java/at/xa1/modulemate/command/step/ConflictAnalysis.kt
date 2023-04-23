package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.step.ActiveWork.Companion.ACTIVE_TIME_SPAN
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
            println("fetch all failed: $e")
        }

        val remoteTrackingBranch = repository.getRemoteTrackingBranch()

        val modules = mutableMapOf<Module, ModuleConflicts>()
        val consideredModules = context.modules.filteredModules
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
            println(module.module.path)
            module.branches
                .filter { branch -> branch.ref != remoteTrackingBranch }
                .forEach { branch ->
                    println("  - ${branch.ref} by ${branch.authors.joinToString(separator = ", ")}")
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
