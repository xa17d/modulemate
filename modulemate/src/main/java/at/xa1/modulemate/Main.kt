package at.xa1.modulemate

import at.xa1.modulemate.git.GitRepository
import java.io.File

fun main(args: Array<String>) {
    println("\uD83E\uDDF0 modulemate")

    val cliArgs = CliArgs(args)

    val repositoryFolder = File(cliArgs.getValueOrNull("--repository") ?: ".")
    val repository = GitRepository(repositoryFolder)

    println("Repository: ${repository.getRepositoryRoot().canonicalFile.absolutePath}")
    println("Name:       ${repository.getRemoteOrigin().repositoryName}")
    println("Branch:     ${repository.getBranch()}")
}
