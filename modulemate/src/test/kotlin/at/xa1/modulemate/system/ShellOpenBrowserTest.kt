package at.xa1.modulemate.system

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ShellOpenBrowserTest {

    private val fakeShell = FakeShell()
    private val instance = ShellOpenBrowser(fakeShell)

    @Test
    fun `open uses open command on shell`() {
        fakeShell.failOnUnexpectedCommand = false

        instance.open("https://example.com")

        assertEquals(listOf("open", "https://example.com"), fakeShell.actualCommands.single())
    }
}
