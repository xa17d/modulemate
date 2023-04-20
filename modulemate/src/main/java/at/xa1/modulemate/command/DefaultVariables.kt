package at.xa1.modulemate.command

import at.xa1.modulemate.git.GitRepository

fun Variables.addDefault(repository: GitRepository) {
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
}
