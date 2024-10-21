package at.xa1.modulemate.command.variable

import at.xa1.modulemate.config.GitHostSubstitutionConfig
import at.xa1.modulemate.config.VariablesConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class GetHostSubstitutedTest {
    @Test
    fun `hosts in config are replaced, hosts not in config are unchanged`() {
        val config = VariablesConfig(
            gitHostSubstitutions = listOf(
                GitHostSubstitutionConfig(value = "localhost", replacement = "example.com"),
                GitHostSubstitutionConfig(value = "github.com-ACME", replacement = "github.com")
            )
        )

        assertEquals("example.com", getHostSubstituted(config, "localhost")) // replaced
        assertEquals("github.com", getHostSubstituted(config, "github.com-ACME")) // replaced
        assertEquals("xa1.at", getHostSubstituted(config, "xa1.at")) // unchanged
    }

    @Test
    fun `substitution is not case sensitive`() {
        val config = VariablesConfig(
            gitHostSubstitutions = listOf(
                GitHostSubstitutionConfig(value = "github.com-ACME", replacement = "github.com")
            )
        )

        assertEquals("github.com", getHostSubstituted(config, "GitHub.com-Acme"))
    }
}
