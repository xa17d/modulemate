package at.xa1.modulemate.git

data class GitRemote private constructor(
    val url: String,
    val host: String,
    val owner: String,
    val repository: String,
    val repositoryName: String
) {
    companion object {
        fun createOrNull(url: String): GitRemote? =
            try {
                create(url)
            } catch (e: IllegalArgumentException) {
                null
            }

        /**
         * Matches e.g.: `git@github.com:xa17d/modulemate.git`
         */
        private val sshSchemeRegex = Regex("([^@]+)@([^:]+):(.*)")

        /**
         * Matches e.g.: `https://git.example.com/xa17d/modulemate.git`
         */
        private val httpsSchemeRegex = Regex("https?://([^/]+)/(.*)")
        fun create(url: String): GitRemote {
            val sshMatch = sshSchemeRegex.matchEntire(url)
            if (sshMatch != null) {
                return create(
                    url = url,
                    host = sshMatch.groupValues[2],
                    path = sshMatch.groupValues[3]
                )
            }

            val httpsMatch = httpsSchemeRegex.matchEntire(url)
            if (httpsMatch != null) {
                return create(
                    url = url,
                    host = httpsMatch.groupValues[1],
                    path = httpsMatch.groupValues[2]
                )
            }

            throw IllegalArgumentException("Unknown remote format: $url")
        }

        private fun create(url: String, host: String, path: String): GitRemote {
            val pathSegments = path.split('/')
            if (pathSegments.size != 2) {
                throw IllegalArgumentException(
                    "Expected exactly 2 path segments, but there are ${pathSegments.size}: $url"
                )
            }
            val owner = pathSegments[0]
            val repository = pathSegments[1]
            val repositoryName = repository.removeSuffix(".git")
            return GitRemote(
                url = url,
                host = host,
                owner = owner,
                repository = repository,
                repositoryName = repositoryName
            )
        }
    }
}
