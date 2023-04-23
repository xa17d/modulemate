package at.xa1.modulemate.command.variable

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

        add(
            Variable(ACTIVE_MODULE) {
                val activeModule = modules.getActiveModule()
                activeModule.path
            }
        )

        add(
            Variable(ACTIVE_MODULE_FOLDER) {
                val activeModule = modules.getByPath(get(ACTIVE_MODULE))
                activeModule.absolutePath
            }
        )
    }

    fun COMMAND_ARG(index: Int): String = "COMMAND_ARG_$index"

    const val ACTIVE_MODULE_FOLDER: String = "ACTIVE_MODULE_FOLDER"
    const val ACTIVE_MODULE: String = "ACTIVE_MODULE"
}
