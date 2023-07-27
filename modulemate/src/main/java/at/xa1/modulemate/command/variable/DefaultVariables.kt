package at.xa1.modulemate.command.variable

import at.xa1.modulemate.Modulemate
import at.xa1.modulemate.git.GitRepository
import at.xa1.modulemate.module.Modules

object DefaultVariables {
    fun create(repository: GitRepository, modules: Modules) = VariableSet().apply {
        add(Variable("GIT_OWNER") { repository.getRemoteOrigin().owner })
        add(Variable("GIT_HOST") { repository.getRemoteOrigin().host })
        add(Variable("GIT_REPOSITORY_NAME") { repository.getRemoteOrigin().repositoryName })
        add(Variable("GIT_BRANCH_NAME") { repository.getBranch() })
        add(
            Variable("JIRA_TICKET") {
                Regex("[A-Z]+\\-[0-9]+").find(repository.getBranch())?.value
                    ?: error("Cannot identify ticket.")
            }
        )

        add(Variable(ACTIVE_MODULE) { get(HOT_MODULE) }) // Deprecated TODO remove
        add(Variable(ACTIVE_MODULE_FOLDER) { get(HOT_MODULE_FOLDER) }) // Deprecated TODO remove

        add(
            Variable(HOT_MODULE) {
                val hotModule = modules.getHotModule()
                hotModule.path
            }
        )

        add(
            Variable(HOT_MODULE_FOLDER) {
                val hotModule = modules.getByPath(get(HOT_MODULE))
                hotModule.absolutePath
            }
        )

        add(Variable("MODULEMATE_HOME") { Modulemate.home.absolutePath })
    }

    fun COMMAND_ARG(index: Int): String = "COMMAND_ARG_$index"

    const val ACTIVE_MODULE_FOLDER: String = "ACTIVE_MODULE_FOLDER" // Note: Deprecated
    const val ACTIVE_MODULE: String = "ACTIVE_MODULE" // Note: Deprecated
    const val HOT_MODULE_FOLDER: String = "HOT_MODULE_FOLDER"
    const val HOT_MODULE: String = "HOT_MODULE"
    const val CONFIG_FOLDER: String = "CONFIG_FOLDER"
}
