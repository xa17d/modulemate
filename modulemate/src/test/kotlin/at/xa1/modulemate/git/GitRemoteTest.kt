package at.xa1.modulemate.git

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class GitRemoteTest {

    @Test
    fun `ssh remote`() {
        val gitRemote = GitRemote.create("git@github.com:xa17d/modulemate.git")

        assertEquals("git@github.com:xa17d/modulemate.git", gitRemote.url)
        assertEquals("github.com", gitRemote.host)
        assertEquals("xa17d", gitRemote.owner)
        assertEquals("modulemate.git", gitRemote.repository)
        assertEquals("modulemate", gitRemote.repositoryName)
    }

    @Test
    fun `https remote`() {
        val gitRemote = GitRemote.create("https://git.example.com/xa17d/modulemate.git")

        assertEquals("https://git.example.com/xa17d/modulemate.git", gitRemote.url)
        assertEquals("git.example.com", gitRemote.host)
        assertEquals("xa17d", gitRemote.owner)
        assertEquals("modulemate.git", gitRemote.repository)
        assertEquals("modulemate", gitRemote.repositoryName)
    }
}
